package netlogo.plot
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._
import scala.io.StdIn

class myPlotConnectedAgent(plotModel: PlotModel) extends NetlogoPlotAgent(plotModel)(1000)(5) with Simple {
  addPlotPens(PlotPen("plot a * 0.2"))
  addPlotVariables("a")
  
  override  def receive = {
    case Run => run
    case a: Int => cmd("set a "+a)
    case _ => 
  }
  
  override def setup = {}
  
}
class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(1000)(15) with Simple {
 var netlogo_actor: ActorRef = _
 
  def receive = {
    case Run => run
    case a: ActorRef => netlogo_actor = a
    case _ => 
  }
  override def check = {
    print("issou")
    try {
      val mt = report("burned-trees")
      println("issouhvd")
    } catch {
      case e: Exception  => println("problem")
    }
    
    //netlogo_actor ! altruists
  }
  override def setup = {}
}

object PlotNetlogoConnected extends App {
  println(">>> Press ENTER to exit <<<")

  val graphicalPlotParams = GraphicalParam((0,0))
  val plotConnectedModel = PlotModel(graphicalPlotParams, "my_plot_connected")
  
  val graphicalParams = GraphicalParam((0,0), (500,500))
  val netlogoModel = NetlogoModel(graphicalParams, getClass.getResource("netlogo/Fire.nlogo").getPath)
  
  val system = ActorSystem("mySystem")
  val myplot = system.actorOf(Props(new myPlotConnectedAgent(plotConnectedModel)), "myPlotConnectedAgent")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
  
  myNetlogo ! myplot  
  //myplot ! Run
  myNetlogo ! Run

  try StdIn.readLine
  finally system.terminate
}