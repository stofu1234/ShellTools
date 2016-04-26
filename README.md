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
