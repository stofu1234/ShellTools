package jp.co.stofu.ShellTools

import java.util.Iterator
import java.lang.ProcessBuilder
import collection.JavaConversions._
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue
import java.io.BufferedReader
import java.io.InputStreamReader


class ProcessIterator(queueSize: Int, args: String*) extends Iterator[String] {

   val elementQueue: BlockingQueue[Any]     = new LinkedBlockingQueue[Any](queueSize)
   val hasNextQueue: BlockingQueue[Boolean] = new LinkedBlockingQueue[Boolean](queueSize)
   val processBuilder   = new ProcessBuilder(args.toList)
   val process          = processBuilder.start()
   val inputReader      = new BufferedReader(new InputStreamReader(process.getInputStream()))
   val errorReader      = new BufferedReader(new InputStreamReader(process.getErrorStream()))
   val executor         = Executors.newFixedThreadPool(3)
   var isFirstRead      = false
   var isOutputEnd      = false
   var isErrorEnd       = false
   val initialReadWait  = 100

   case class MessageElement(_message: String)
   case class ExceptionElement(ex: Exception)

   implicit def funcToRunnable( func : () => Unit ) = new Runnable(){ def run() = func() }

   val asyncBuffering = (reader: BufferedReader,flagFunc: () => Unit) => {
         try {
            var nextLine=reader.readLine()
            isFirstRead = true
            while(nextLine!=null){
               elementQueue.put(new MessageElement(nextLine))
               hasNextQueue.put(true)
               nextLine=reader.readLine()
            }
            reader.close()
         } catch {
            case e: Exception => {
                                  elementQueue.put(new ExceptionElement(e))
                                  hasNextQueue.put(true)
                                 }
         }
         flagFunc()
         if(isOutputEnd && isErrorEnd && elementQueue.size == 0) {
            hasNextQueue.put(false)
         }
         
      }
   
   //標準入力の非同期読み込み
   executor.execute(() => {
                         asyncBuffering(inputReader,() => {isOutputEnd = true})
                      })
   //標準エラーの非同期読み込み
   executor.execute(() => {
                         asyncBuffering(errorReader,() => {isErrorEnd  = true})
                      })

   def this(args: String*) = this(1024,args:_*)

   def hasNext(): Boolean = {
      //最初の行でExceptionが発生した場合を考慮し、elementQueue.size>0追加
      while(!(isFirstRead || elementQueue.size > 0)){
         Thread.sleep(initialReadWait)
      }
      return !(isOutputEnd && isErrorEnd) && hasNextQueue.take() || elementQueue.size > 0
   }
   def next(): String = {
      val element = elementQueue.take()
      element match {
         case MessageElement(message) => return message
         case ExceptionElement(ex)    => throw ex
         case _                       => throw new NoSuchElementException()
      }
   }
}



