package agent.simulation.graphical.netlogo.component
import agent.simulation.graphical.GraphicalParam
import agent.simulation.graphical.netlogo.NetlogoModel
import agent.simulation.graphical.netlogo.NetlogoSimpleListener
import agent._
import java.awt.Point
import java.net.URI
import java.nio.file.Paths
import org.nlogo.api.Version
import agent.simulation.graphical.netlogo.{NetlogoConstants => NC}


class ButtonModel(params: GraphicalParam, val name: String, val size: Int, val forever: Boolean, x: Any*) extends NetlogoModel(params,"", x) {
  
}

object ButtonModel {
  def apply(params: GraphicalParam, name: String, size: Int = NC.DEFAULT_BUTTON_SIZE, forever: Boolean = NC.DEFAULT_BUTTON_FOREVER) = new ButtonModel(params, name, size, forever)
}

/**
 * A netlogo button agent
 */
class NetlogoButtonAgent (buttonModel: ButtonModel)(maxTicks:Int = NC.DEFAULT_MAX_TICKS)(fps: Float = NC.DEFAULT_FPS) extends NetlogoAgentComponent(buttonModel)(maxTicks)(fps) with Simple {
  
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
        comp.openFromURI(Paths.get("issou", "eud").toUri)
        comp.listenerManager.addListener(new NetlogoSimpleListener {
          override def buttonPressed(buttonName: String) = buttonPressedHandle
         
          override def buttonStopped(buttonName: String) = buttonReleasedHandle
          })
      }
      if(buttonModel.forever)
         cmd("setup")
    }
  
  // If not when the netlogo windows is not in the frame the ticks goes forever
  def tickAction = {s"""
wait ${1.0f/fps}
tick
"""
  }
  def modelButton : String = {s"""
to setup
  clear-ticks
  reset-ticks
end
to go
${if(buttonModel.forever) tickAction else ""}
end
@#$$#@#$$#@
GRAPHICS-WINDOW
689
412
730
454
-1
-1
1.0
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
-16
16
1
1
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