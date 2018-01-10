package agent.simulation.graphical.netlogo.component
import scala.concurrent.duration._
import agent.simulation.graphical.netlogo._
abstract class NetlogoAgentComponentNoFps(netlogoModel : NetlogoModel) extends NetlogoAgentComponent(netlogoModel)()() {
  final def check = {}
}