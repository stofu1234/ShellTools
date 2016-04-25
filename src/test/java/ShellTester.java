import jp.co.stofu.ShellTools.*;

public class ShellTester{
	public static void main(String[] args){
		ShellStream.create("dir c:\\").forEach(System.out::println);
		System.exit(0);
	}

}