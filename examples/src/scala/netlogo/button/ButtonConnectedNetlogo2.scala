package netlogo.button
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._
import org.nlogo.core.CompilerException
import scala.io.StdIn

class myButtonConnectedAgent2(buttonModel: ButtonModel)(val cmd: String) extends NetlogoButtonAgent(buttonModel)()(4) with Simple {
  
  override def setup = {}
 
  override def buttonPressedHandle = {}
  override def buttonReleasedHandle = netlogo_actor ! cmd
  
  override def check = netlogo_actor ! cmd
    
  var netlogo_actor: ActorRef = _
  
  override def receive = {
    case "run" => run
    case actor: ActorRef => netlogo_actor = actor
  }
}

class myNetlogoAgent2(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(100000)() with Simple {
  
  def receive = {
    case "run" => run
    case a: String => cmd(a)
    
  }
 
 override def runNetlogo = {
    import java.awt.Point
    wait {
      frame.setSize(netlogoModel.params.dim._1, netlogoModel.params.dim._2)
      frame.setLocation(new Point(netlogoModel.params.pos._1, netlogoModel.params.pos._2))
      frame.add(comp)
      frame.setVisible(true)
      comp.open(netlogoModel.path)   
    }
  }
  override def check = {}
  override def setup = {}
}

object ButtonConnectedNetlogo2 extends App {
  println(">>> Press ENTER to exit <<<")
  
  val graphicalParamsButton1 = GraphicalParam((100,100))
  val graphicalParamsButton2 = GraphicalParam((200,200))
  val cmd1 = "setup-empty"
  val cmd2 = "setup-full"
  
  val buttonModel1 = ButtonModel(graphicalParamsButton1, cmd1)
  val buttonModel2 = ButtonModel(graphicalParamsButton2, cmd2)
  
  val graphicalParamsNet = GraphicalParam((300,300), (700,600))
  val netlogoModel = NetlogoModel(graphicalParamsNet, "src/example/scala/netlogo/button/Ethnocentrism_connected.nlogo")
  
  val system = ActorSystem("mySystem")
  val myButton1 = system.actorOf(Props(new myButtonConnectedAgent2(buttonModel1)(cmd1)), "myButton1")
  val myButton2 = system.actorOf(Props(new myButtonConnectedAgent2(buttonModel2)(cmd2)), "myButton2")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent2(netlogoModel)), "myNetlogo")
  
  myButton1 ! "run"
  myButton2 ! "run"
  
  myButton1 ! myNetlogo
  myButton2 ! myNetlogo
  
  myNetlogo ! "run"


  try StdIn.readLine
  finally system.terminate
}