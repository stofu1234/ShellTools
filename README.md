ShellTools
====

## Overview
Java/他Java環境スクリプト用シェルコマンド実行StreamAPIライブラリ（マルチスレッド対応）です

JavaからShellで実行したコマンドの実行結果をStreamオブジェクトとして受け取るライブラリです

主にパイプライニングでデータ加工を行うようなコマンドの実行結果をJavaでシームレスに受け取ったり、  
そのコマンドをJavaのStreamAPIで置き換えたりする場合に使います。

## Description

## Requirement
sbtがインストールされていること

## Usage
//Scalaインタプリタで実行した場合  
//事前準備  
implicit def funcToConsumer( func : String => Unit ) = new Consumer[String](){ def accept(s: String) = func(s) }  
val funcPrintln: Consumer[String] = (st:String) => println(st)  

//ProcessStream  
import jp.co.stofu.ShellTools.ProcessStream  
ProcessStream.create("cmd","/c","dir","c:\\").forEach(funcPrintln)  

//ShellStream  
import jp.co.stofu.ShellTools.ShellStream  
ShellStream.create("dir c:\\").forEach(funcPrintln)  

//PowershellStream  
import jp.co.stofu.ShellTools.PowerShellStream  
PowerShellStream.create("Get-EventLog -LogName security -Newest 10").forEach(funcPrintln)  

## Install

git clone https://github.com/stofu1234/ShellTools.git  
cd ShellTools  

mkdir out  
sbt compile test:compile assembly  

## Contribution

## Licence

[MIT](https://github.com/tcnksm/tool/blob/master/LICENCE)

## Author

[stofu1234](https://github.com/stofu1234)
