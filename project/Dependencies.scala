import sbt._

object Dependencies {

 lazy val scalacTic = "org.scalactic" %% "scalactic" % "3.0.1"
 lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
 lazy val commonDependencies = Seq(scalaTest, scalacTic,specs2)

 lazy val specs2 = "org.specs2" %% "specs2-core" % "3.8.6" % "test"

 lazy val akka  = Seq(
 	"com.typesafe.akka" %% "akka-actor" % "2.4.16",
 	"com.typesafe.akka" %% "akka-slf4j"   % "2.4.16",
 	"com.typesafe.akka" %% "akka-remote"  % "2.4.16",
 	"com.typesafe.akka" %% "akka-agent"   % "2.4.16",
 	"com.typesafe.akka" %% "akka-testkit" % "2.4.16" % "test"
 )
 //netlogo dependencies
 lazy val asm = Seq(
 	"org.ow2.asm" % "asm" % "5.0.3",
	"org.ow2.asm" % "asm-all" % "5.0.4",
	"org.ow2.asm" % "asm-analysis" % "5.0.3",
	"org.ow2.asm" % "asm-tree" % "5.0.3",
	"org.ow2.asm" % "asm-util" % "5.0.3"
 )
 lazy val parboiled = "org.parboiled" %% "parboiled" % "2.1.3"

 lazy val netlogoDependencies = asm :+ parboiled
}
