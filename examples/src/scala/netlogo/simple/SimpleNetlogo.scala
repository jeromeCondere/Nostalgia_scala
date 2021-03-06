package netlogo.simple
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical._
import agent.Simple
import scala.io.StdIn

class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(200)(30) with Simple {
  def receive = {
    case "run" => run
    case _ => 
  }
  override def check = {}
  override def setup = {}
}

object SimpleNetlogo extends App {
  println(">>> Press ENTER to exit <<<")
  val graphicalParams = GraphicalParam((0,0), (500,500))
  val netlogoModel = NetlogoModel(graphicalParams, "examples/resources/netlogo/Fire.nlogo")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
 
  myNetlogo ! "run"
  
  try StdIn.readLine
  finally system.terminate
}