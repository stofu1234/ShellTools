package jp.co.stofu.ShellTools

trait StreamElement
class MessageElement(_message: String) extends StreamElement {
   var message = _message
}
class ExceptionElement(_ex: Exception) extends StreamElement {
   var ex = _ex
}
