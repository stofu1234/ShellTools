import jp.co.stofu.ShellTools._
import java.util.function.Consumer
import org.scalatest.FunSpec
import org.scalatest.Matchers._
import java.io.PrintWriter

class TestSpec01 extends FunSpec {
   implicit def funcToConsumer( func : String => Unit ) = new Consumer[String](){ def accept(s: String) = func(s) }
   val funcPrintln: Consumer[String] = (st:String) => println(st)

   describe("spec") {
      it("test01") {
         val pw = new PrintWriter(".\\out\\result_TestSpec01_01.log")
         PowerShellStream.create("& {Get-EventLog -LogName security -Newest 100}").forEach((st:String) => pw.println(st))
         pw.close()
      }
      it("test02") {
         val pw = new PrintWriter(".\\out\\result_TestSpec01_02.log")
         ShellStream.create("dir c:\\").forEach((st:String) => pw.println(st))
         pw.close()
      }
   }
}

