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

class NetlogoSliderAgent(sliderModel: SliderModel)(maxTicks:Int = 1000)(fps: Int = 30) extends NetlogoAgentComponent(sliderModel)(maxTicks)(fps) with Simple {
  def setup = {}
  final def check = {}
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
      comp.listenerManager.addListener(new NetlogoSimpleListener{
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
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

@#$$#@#$$#@
${Version.version}
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
@#$$#@#$$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180

@#$$#@#$$#@
0
@#$$#@#$$#@

"""
  }
   
}