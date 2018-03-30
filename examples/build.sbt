import Dependencies._

name := "examples"

val sampleStringTask = taskKey[Unit]("A sample string task.")

sampleStringTask := {println("hey hey mia")}

scalaSource in Compile := baseDirectory.value / "src/scala"
