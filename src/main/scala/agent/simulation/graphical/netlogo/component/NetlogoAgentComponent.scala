package agent.simulation.graphical.netlogo.component
import scala.concurrent.duration._
import agent.simulation.graphical.netlogo._

 abstract class NetlogoAgentComponent(netlogoModel : NetlogoModel)(maxTicks:Int = NetlogoConstants.DEFAULT_MAX_TICKS)(fps: Float = NetlogoConstants.DEFAULT_FPS) extends NetlogoAgent(netlogoModel)(maxTicks)(fps) {
}