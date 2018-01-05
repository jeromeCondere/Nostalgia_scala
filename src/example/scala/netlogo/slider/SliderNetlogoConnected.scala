package netlogo.slider
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._
import org.nlogo.core.CompilerException

/*
 * A simple model of a slider agent connected to a normal netlogo agent
 * The slider agent controls the density parameter of the netlogo agent
 * Netlogo model: Fire.nlogo (Earth dynamics)
 */

/** contains slider name and slider default value */
case class Default(val a: Int, val name: String)

class mySliderConnectedAgent(sliderModel: SliderModel) extends NetlogoSliderAgent(sliderModel)(15000)(10) with Simple {
  override def sliderHandle(value: Double, min: Double, increment: Double, max: Double, buttonReleased: Boolean) = {
    issou ! value
  }
  
  var issou: ActorRef = _
  
  override def setup = {}
  override def receive = {
    case "run" => run
    case actor: ActorRef => issou = actor
    case "default" => issou ! Default(sliderModel.defaultValue, "density")
  }
}

class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(100000)(30) with Simple {
 var default_value: Default = _
  def receive = {
    case "run" => run
    case Default(a, name) => default_value = Default(a, name); println(default_value)
    case a: Double => cmd("set "+ default_value.name + " "+a);
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
     
     cmd("set "+ default_value.name + " "+ default_value.a)
     cmd("setup")
     cmd("repeat "+this.maxTicks+" [ go ]")
  }
  override def check = {}
  override def setup = {}
}

object SliderNetlogoConnected  extends App{
  val graphicalParamsSlider = GraphicalParam((100,100))
  val sliderModel = SliderModel(graphicalParamsSlider, "density")
  
  val graphicalParamsNet = GraphicalParam((300,300), (700,600))
  val netlogoModel = NetlogoModel(graphicalParamsNet, "src/example/scala/netlogo/slider/Fire_connected.nlogo")
  
  val system = ActorSystem("mySystem")
  val mySlider = system.actorOf(Props(new mySliderConnectedAgent(sliderModel)), "mySlider")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
 
  /*
   * First we send the Netlogo agent to the slider
   * Then the slider initializes the default parameter of the Netlogo agent
   * At least now that both agents are set up we can run the agents
   */
  
  mySlider ! myNetlogo
  mySlider ! "default"
  mySlider ! "run"
  myNetlogo ! "run"
  
}