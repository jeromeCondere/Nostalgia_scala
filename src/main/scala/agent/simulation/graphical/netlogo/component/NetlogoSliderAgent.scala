package agent.simulation.graphical.netlogo.component
import agent.simulation.graphical.GraphicalParam
import agent.simulation.graphical.netlogo.NetlogoModel
import agent.simulation.graphical.netlogo.NetlogoSimpleListener
import agent._
import java.awt.Point
import org.nlogo.api.Version

class SliderModel(params: GraphicalParam, val name: String, val size: Int, val defaultValue:Int, val minValue:Int, val maxValue:Int, x: Any*) extends NetlogoModel(params,"", x) {
  
}

object SliderModel {
  def apply(params: GraphicalParam, name: String, size: Int = 300, defaultValue:Int = 50, minValue:Int = 0, maxValue:Int = 100) = new SliderModel(params, name, size, defaultValue, minValue, maxValue)
}
/**
 * A netlogo slider agent
 */
class NetlogoSliderAgent(sliderModel: SliderModel) extends NetlogoAgentComponentNoFps(sliderModel) with Simple {
  def setup = {}
  /**This method is called every time an event is triggered by the slider*/
  def sliderHandle(value: Double, min: Double, increment: Double, max: Double, buttonReleased: Boolean) = {}
  
  override final def runNetlogo = {
    val eps = 20
   wait {
      frame.setSize(sliderModel.size + eps, 40 + eps)
      frame.setLocation(new Point(sliderModel.params.pos._1, sliderModel.params.pos._2))
      frame.add(comp)
      frame.setVisible(true)
      frame.setResizable(false)
      comp.openFromSource("slider", "", modelSlider)
      comp.listenerManager.addListener(new NetlogoSimpleListener {
        override def sliderChanged(name: String, value: Double, min: Double, increment: Double, max: Double, valueChanged: Boolean, buttonReleased: Boolean) = {
          sliderHandle(value, min, increment, max, buttonReleased)
        }
      })
    }
  }
  
  def receive = {
    case Run => run
  }
   
  def modelSlider = {s"""
@#$$#@#$$#@
GRAPHICS-WINDOW
10
166
255
214
16
1
5.7
1
10
1
1
1
0
1
1
1
-16
16
-1
1
0
0
1
ticks
$fps

SLIDER
0
0
${sliderModel.size}
43
${sliderModel.name}
${sliderModel.name}
${sliderModel.minValue}
${sliderModel.maxValue}
${sliderModel.defaultValue}
1
1
NIL
HORIZONTAL

@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
${Version.version}
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
0
@#$$#@#$$#@
"""
  }
   
}