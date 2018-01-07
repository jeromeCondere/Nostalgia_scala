package netlogo.button
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._

class myButtonAgent(buttonModel: ButtonModel) extends NetlogoButtonAgent(buttonModel)()() with Simple {
  
  override def setup = {}
  override def buttonPressedHandle = println("Button has been Pressed! ->\n")
  override def buttonReleasedHandle = println("Button has been Released! ->\n")
}

object ButtonNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0))
  val buttonModel = ButtonModel(graphicalParams, "my_button", 100, false)
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myButtonAgent(buttonModel)), "myButtonAgent")
 
  myNetlogo ! Run
}
