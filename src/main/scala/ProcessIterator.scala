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
import java.io.PrintWriter
import java.util.Date

class ProcessIterator(queueSize: Int, args: String*) extends Iterator[String] {
   val pb      = new ProcessBuilder(args.toList)
   val process = pb.start()
   val br      = new BufferedReader(new InputStreamReader(process.getInputStream()))
   val er      = new BufferedReader(new InputStreamReader(process.getErrorStream()))
   val exec    = Executors.newFixedThreadPool(3)
   val queue: BlockingQueue[Any] = new LinkedBlockingQueue[Any](queueSize)
   val hasNextQueue: BlockingQueue[Boolean] = new LinkedBlockingQueue[Boolean](queueSize)
   var isFirstRead = false
   var isOutputEnd = false
   var isErrorEnd  = false
   var isAllEnd    = false
   val pw = new PrintWriter(".\\out\\result_ProcessIterator_"+args(0)+".log")

   case class MessageElement(_message: String)
   case class EOFElement()
   case class ExceptionElement(ex: Exception)

   implicit def funcToRunnable( func : () => Unit ) = new Runnable(){ def run() = func() }

   val asyncBuffering = (reader: BufferedReader) => {
         try {
            var nextLine=reader.readLine()
            isFirstRead = true
            while(nextLine!=null){
               pw.println(("%tF %<tT.%<tL" format new Date)+" nextLine:"+nextLine)
               pw.flush()
               queue.put(new MessageElement(nextLine))
               hasNextQueue.put(true)
               nextLine=reader.readLine()
            }
            //var nextLine=""
            //this.synchronized {
            //   nextLine=reader.readLine()
            //   isFirstRead = true
            //   queue.put(new MessageElement(nextLine))
            //}
            //while(nextLine!=null){
            //   this.synchronized {
            //      nextLine=reader.readLine()
            //      queue.put(new MessageElement(nextLine))
            //   }
            //}
            reader.close()
            pw.println(("%tF %<tT.%<tL" format new Date)+" after asyncBuffering")
            pw.flush()
         } catch {
            case e: Exception => {
                                  queue.put(new ExceptionElement(e))
                                  hasNextQueue.put(true)
                                 }
         }
      }
   
   exec.execute(() => {
                         asyncBuffering(br)
                         isOutputEnd = true
                         if(isOutputEnd && isErrorEnd && queue.size == 0) {
                            pw.println(("%tF %<tT.%<tL" format new Date)+" before put EOFElement")
                            pw.flush()
                            isAllEnd = true
                            hasNextQueue.put(false)
                            //queue.put(new EOFElement)
                         }
                      })
   exec.execute(() => {
                         asyncBuffering(er)
                         isErrorEnd  = true
                         if(isOutputEnd && isErrorEnd && queue.size == 0) {
                            pw.println(("%tF %<tT.%<tL" format new Date)+" before put EOFElement")
                            pw.flush()
                            isAllEnd = true
                            hasNextQueue.put(false)
                            //queue.put(new EOFElement)
                         }
                      })

   def this(args: String*) = this(1024,args:_*)

   def hasNext(): Boolean = {
      //最初の行でExceptionが発生した場合を考慮し、queue.size>0追加
      while(!(isFirstRead || queue.size > 0)){
         Thread.sleep(100)
      }
      //return queue.size>0
      //return !(isOutputEnd && isErrorEnd)
      //return (!(isOutputEnd && isErrorEnd) || queue.size > 0) && !isAllEnd
      pw.println(("%tF %<tT.%<tL" format new Date)+" hasNext:"+((!(isOutputEnd && isErrorEnd) || queue.size > 0) && !isAllEnd))
      pw.println(("%tF %<tT.%<tL" format new Date)+" isOutputEnd:"+isOutputEnd+" isErrorEnd:"+isErrorEnd+" queue.size:"+queue.size+" isAllEnd:"+isAllEnd)
      pw.flush()
      this.synchronized {
         //return (!(isOutputEnd && isErrorEnd) || queue.size > 0) && !isAllEnd
         return !(isOutputEnd && isErrorEnd) && hasNextQueue.take() || queue.size > 0
      }
   }
   def next(): String = {
      pw.println(("%tF %<tT.%<tL" format new Date)+" enter next")
      pw.flush()
      //if(queue.size>0){
      //if(!(isOutputEnd && isErrorEnd)){
      if(!(isOutputEnd && isErrorEnd) || queue.size > 0){
         pw.println(("%tF %<tT.%<tL" format new Date)+" before next take")
         pw.flush()
         val element = queue.take()
         pw.println(("%tF %<tT.%<tL" format new Date)+" after  next take")
         pw.flush()
         
         element match {
            case MessageElement(message) => {
                                             pw.println(("%tF %<tT.%<tL" format new Date)+" before MessageElement:"+message)
                                             pw.println(("%tF %<tT.%<tL" format new Date)+" isOutputEnd:"+isOutputEnd+" isErrorEnd:"+isErrorEnd+" queue.size:"+queue.size+" isAllEnd:"+isAllEnd)
                                             pw.flush()
                                             return message
                                            }
            case EOFElement() => {
                                  pw.println(("%tF %<tT.%<tL" format new Date)+" before EOFElement")
                                  pw.flush()
                                  return null
                                 }
            //case EOFElement() => throw new NoSuchElementException()
            case ExceptionElement(ex) => throw ex
            case _ => {
                       pw.println(("%tF %<tT.%<tL" format new Date)+" unknown type:"+element.getClass().getName())
                       pw.flush()
                       return null
                      }
            //case _ => throw new NoSuchElementException()
         }
      }
      pw.println(("%tF %<tT.%<tL" format new Date)+" end of next")
      pw.flush()
      return null
      //throw new NoSuchElementException()
   }
}



