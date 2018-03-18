package netlogo.plot
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._

class myPlotAgent(plotModel: PlotModel) extends NetlogoPlotAgent(plotModel)()() with Simple {
  
  override def setup = {}
  
}

object PlotNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0))
  val plotModel = PlotModel(graphicalParams, "my_plot")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myPlotAgent(plotModel)), "myPlotAgent")
 
  myNetlogo ! Run
}
