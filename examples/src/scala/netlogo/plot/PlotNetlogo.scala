package netlogo.plot
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._
import scala.io.StdIn

class myPlotAgent(plotModel: PlotModel) extends NetlogoPlotAgent(plotModel)(30)(3000) with Simple {
  
  override def setup = {}
  
}

object PlotNetlogo extends App {
  println(">>> Press ENTER to exit <<<")

  val graphicalParams = GraphicalParam((0,0))
  val plotModel = PlotModel(graphicalParams, "my_plot")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new myPlotAgent(plotModel)), "myPlotAgent")
 
  myNetlogo ! Run

  try StdIn.readLine
  finally system.terminate
}
