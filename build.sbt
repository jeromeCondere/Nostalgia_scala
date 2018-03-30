import Dependencies._

lazy val commonSettings = Seq(
   	scalaVersion := "2.12.3",
   	version      := "0.0.1"
)

lazy val nostalgia = (project in file("nostalgia"))
  .settings(
    commonSettings,
    organization := "org.nostalgia",
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= akka,
    libraryDependencies ++= asm,
  )

lazy val examples = (project in file("examples")).
  settings(
	commonSettings,
	organization := "org.nostalgia.examples",
    libraryDependencies ++= commonDependencies,
    libraryDependencies ++= akka,
    libraryDependencies ++= asm,
    unmanagedBase := (unmanagedBase in nostalgia).value,
    scalaSource in Compile := baseDirectory.value / "src/scala/",
    test / aggregate := false
  ).dependsOn(nostalgia)

lazy val root = (project in file(".")).
  aggregate(nostalgia, examples)
  
