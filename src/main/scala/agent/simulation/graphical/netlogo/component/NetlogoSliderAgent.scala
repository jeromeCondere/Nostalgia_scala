package agent.simulation.graphical.netlogo.component
import agent.simulation.graphical.GraphicalParam
import agent.simulation.graphical.netlogo.NetlogoModel
import agent._
class SliderModel(params: GraphicalParam, size: Int, defautValue:Int, maxValue:Int, minValue:Int, x: Any*) extends NetlogoModel(params,"", x) {
  
}
object SliderModel {
  def apply(params: GraphicalParam, size: Int = 300, defautValue:Int, maxValue:Int = 100, minValue:Int = 0) = new SliderModel(params, size,defautValue, maxValue, minValue)
}

class NetlogoSliderAgent(sliderModel: SliderModel)(maxTicks:Int = 1000)(fps: Int = 30) extends NetlogoAgentComponent(sliderModel)(maxTicks)(fps) with Simple {
  def setup = {}
  def check = {}
  
  def receive = {
    case Run => run
  }
}