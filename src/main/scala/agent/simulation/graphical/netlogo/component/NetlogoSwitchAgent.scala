package agent.simulation.graphical.netlogo.component
import agent.simulation.graphical.GraphicalParam
import agent.simulation.graphical.netlogo.NetlogoModel
import agent.simulation.graphical.netlogo.NetlogoSimpleListener
import agent._
import java.awt.Point
import java.net.URI
import org.nlogo.api.Version
import agent.simulation.graphical.netlogo.{NetlogoConstants => NC}

class SwitchModel(params: GraphicalParam, val name: String, val size: Int, val value: Boolean, x: Any*) extends NetlogoModel(params,"", x) {
  
}

object SwitchModel {
  def apply(params: GraphicalParam, name: String, size: Int = NC.DEFAULT_SWITCH_SIZE, defaultValue: Boolean = NC.DEFAULT_SWITCH_VALUE) = new SwitchModel(params, name, size, defaultValue)
}

/**
 * A netlogo switch agent
 */
class NetlogoSwitchAgent(switchModel: SwitchModel) extends NetlogoAgentComponentNoFps(switchModel) with Simple {
  def setup = {}
  /**This method is called every time an event is triggered by the switch*/
  def switchHandle(value: Boolean) = {}
  
  override final def runNetlogo = {
    val eps = 20
   wait {
      frame.setSize(switchModel.size + eps, 40 + eps)
      frame.setLocation(new Point(switchModel.params.pos._1, switchModel.params.pos._2))
      frame.add(comp)
      frame.setVisible(true)
      frame.setResizable(false)
      comp.openFromURI(new URI(modelSwitch))
      comp.listenerManager.addListener(new NetlogoSimpleListener {
        override def switchChanged(name: String, value: Boolean, valueChanged: Boolean) = {
          switchHandle(value)
        }
      })
    }
  }
  
   
  def modelSwitch = {s"""
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

SWITCH
4
4
${switchModel.size}
40
${switchModel.name}
${switchModel.name}
${if(switchModel.value) 0 else 1}
1
-1000

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