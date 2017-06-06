package netlogo.simple
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical._
import agent.Simple
class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(20)(1) with Simple {
  def receive = {
    case "run" => run
    case _ => 
  }
  override def check = println("check ")
  override def setup = println("setup")
}

object SimpleNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0), (800,600))
  val netlogoModel = NetlogoModel(graphicalParams, "Fire.nlogo")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
  myNetlogo ! "run"
}