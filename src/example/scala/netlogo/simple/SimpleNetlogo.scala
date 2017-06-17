package netlogo.simple
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical._
import agent.Simple

class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(1000)(30) with Simple {
  def receive = {
    case "run" => run
    case _ => 
  }
  override def check = {}//println(">check<")
  override def setup = {}
}

object SimpleNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0), (500,500))
  val netlogoModel = NetlogoModel(graphicalParams, "Fire.nlogo")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
 
  myNetlogo ! "run"
}