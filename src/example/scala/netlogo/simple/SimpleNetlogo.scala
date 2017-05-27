package netlogo.simple
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical._
import agent.Simple
class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(1000)(30) with Simple {
  def receive = {
    case "run" => run
    case _ => ???
  }
}

object SimpleNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0), (800,600))
  val netlogoModel = NetlogoModel(graphicalParams, "Fire.nlogo")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
  myNetlogo ! "run"
}