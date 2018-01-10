package agent.simulation.graphical.netlogo.component
import agent.simulation.graphical.GraphicalParam
import agent.simulation.graphical.netlogo.NetlogoModel
import agent.simulation.graphical.netlogo.NetlogoSimpleListener
import agent._
import java.awt.Point
import org.nlogo.api.Version

class ButtonModel(params: GraphicalParam, val name: String, val size: Int, val forever: Boolean, x: Any*) extends NetlogoModel(params,"", x) {
  
}

object ButtonModel {
  def apply(params: GraphicalParam, name: String, size: Int = 150, forever: Boolean = false) = new ButtonModel(params, name, size, forever)
}

/**
 * A netlogo button agent
 */
class NetlogoButtonAgent (buttonModel: ButtonModel)(maxTicks:Int = 1000)(fps: Int = 30) extends NetlogoAgentComponent(buttonModel)(maxTicks)(fps) with Simple {
  
  def setup = {}
  
  def buttonPressedHandle = {}
  def buttonReleasedHandle = {}
  def check = {}
  
    override final def runNetlogo = {
     val eps = 20
     wait {
        frame.setSize(buttonModel.size + eps, 35 + eps)
        frame.setLocation(new Point(buttonModel.params.pos._1, buttonModel.params.pos._2))
        frame.add(comp)
        frame.setVisible(true)
        frame.setResizable(false)
        comp.openFromSource("button", "", modelButton)
        comp.listenerManager.addListener(new NetlogoSimpleListener {
          override def buttonPressed(buttonName: String) = {
            buttonPressedHandle
          }
         
          override def buttonStopped(buttonName: String) = {
            buttonReleasedHandle

          }
        })
      }

    }
  
  def receive = {
    case Run => run
  }
  
  def modelButton : String = {s"""
to setup
  clear-ticks
  reset-ticks
end
to go
${if(buttonModel.forever) " tick" else ""}
end
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

BUTTON
4
4
${buttonModel.size}
43
${buttonModel.name}
go
${if (buttonModel.forever) "T" else "NIL" }
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

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
@#$$#@#$$#@s
"""
  }
}