package netlogo.switchs
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._

class mySwitchAgent(switchModel: SwitchModel) extends NetlogoSwitchAgent(switchModel) with Simple {
  
  override def setup = {}
  override def switchHandle(value: Boolean) = if (value) println("On") else println("Off")
}

object SwitchNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0))
  val switchModel = SwitchModel(graphicalParams, "my_switch", 100, false)
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new mySwitchAgent(switchModel)), "mySwitchAgent")
 
  myNetlogo ! Run
}
