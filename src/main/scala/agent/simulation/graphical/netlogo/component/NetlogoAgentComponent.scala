package agent.simulation.graphical.netlogo.component
import scala.concurrent.duration._
import agent.simulation.graphical.netlogo._

 abstract class NetlogoAgentComponent(netlogoModel : NetlogoModel) extends NetlogoAgent(netlogoModel)()() {
  final def check = {}
}