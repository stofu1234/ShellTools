package jp.co.stofu.ShellTools

import java.util.stream.StreamSupport
import java.util.Spliterator
import java.util.Spliterators

object ShellStream {
   def create(cmd: String): java.util.stream.Stream[String] = {
      val osName = System.getProperty("os.name").toLowerCase()
      var shellCmd =""
      var shellCmdExecuteSwitch = ""
      val osRegWindows = "^windows.*".r
      val osRegUnix    = "^(linux|mac|sunos).*".r
      osName match {
         case osRegWindows() => {
                                   shellCmd = "cmd"
                                   shellCmdExecuteSwitch = "/c"
                              }
         case osRegUnix()    => {
                                   shellCmd = "/bin/sh"
                                   shellCmdExecuteSwitch = "-c"
                              }
         case _ => {}
      }
   
      return CommandStream.create(shellCmd,shellCmdExecuteSwitch,("\""+cmd+"\""))
   }
}
