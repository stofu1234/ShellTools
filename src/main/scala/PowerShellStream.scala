package jp.co.stofu.ShellTools

import java.util.stream.StreamSupport
import java.util.Spliterator
import java.util.Spliterators


object PowerShellStream {
   def create(cmd: String): java.util.stream.Stream[String] = {
      var shellCmd ="powershell.exe"
      var shellCmdExecuteSwitch = "-Command"
   
      return CommandStream.create(shellCmd,shellCmdExecuteSwitch,("\""+cmd+"\""))
   }
}
