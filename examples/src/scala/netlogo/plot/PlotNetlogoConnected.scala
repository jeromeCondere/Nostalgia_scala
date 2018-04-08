package netlogo.plot
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._
import scala.io.StdIn

class myPlotConnectedAgent(plotModel: PlotModel) extends NetlogoPlotAgent(plotModel)(18000000)(2) with Simple {
  addPlotPens(PlotPen("plot a * 0.2"))
  addPlotVariables("a")
  println(modelPlot)
  var ui = 0
  override  def receive = {
    case Run => run
    case a: Double => cmd("set a "+a); println("ticks_plot: "+ ui)
    case _ => 
  }
  
  override def setup = {}
  override def check = {}

}
class myNetlogoAgent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)(42)(3) with Simple {
 var netlogo_actor: ActorRef = _
 
  def receive = {
    case Run => run
    case a: ActorRef => netlogo_actor = a
    case _ => 
  }
  var ui = 0
  override def check = {
    println("ticks_net: "+ ui)
    ui += 1
    val source = "count patches with [pcolor = pink]"
    reportAndCallback(source, (altruists: AnyRef) => {
      netlogo_actor ! altruists.asInstanceOf[Double]
    })
  }

  override def setup = {}
}

object PlotNetlogoConnected extends App {
  println(">>> Press ENTER to exit <<<")

  val graphicalPlotParams = GraphicalParam((0,0))
  val plotConnectedModel = PlotModel(graphicalPlotParams, "my_plot_connected")
  
  val graphicalParams = GraphicalParam((700,0), (500,500))
  val netlogoModel = NetlogoModel(graphicalParams, "examples/resources/netlogo/plot/Altruism.nlogo")
  
  val system = ActorSystem("mySystem")
  val myplot = system.actorOf(Props(new myPlotConnectedAgent(plotConnectedModel)), "myPlotConnectedAgent")
  val myNetlogo = system.actorOf(Props(new myNetlogoAgent(netlogoModel)), "myNetlogo")
  
  myNetlogo ! myplot  
  myplot ! Run
  myNetlogo ! Run

  try StdIn.readLine
  finally system.terminate
}