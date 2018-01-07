package agent.simulation.graphical.netlogo.component
import scala.concurrent.duration._
import agent.simulation.graphical.netlogo._

 abstract class NetlogoAgentComponent(netlogoModel : NetlogoModel)(maxTicks:Int = 1000)(fps: Int = 30) extends NetlogoAgent(netlogoModel)(maxTicks)(fps) {
  final def check = {}
}