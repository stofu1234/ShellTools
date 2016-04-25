lazy val root = (project in file(".")).
  settings(
    name := "ShellTools",
    version := "1.0",
    scalaVersion := "2.11.7"
  )

import AssemblyKeys._ // put this at the top of the file

assemblySettings

libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
