package agent.simulation.graphical.netlogo.component
import agent.simulation.graphical.GraphicalParam
import agent.simulation.graphical.netlogo.NetlogoModel
import agent.simulation.graphical.netlogo.NetlogoSimpleListener
import agent._
import java.awt.Point
import java.net.URI
import org.nlogo.api.Version
import agent.simulation.graphical.netlogo.{NetlogoConstants => NC}

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set

case class PlotLimits(xmin: Double, xmax: Double, ymin: Double, ymax: Double)
case class PlotLabels(xlabel: String, ylabel: String)
case class PlotMetaData(
    limits: PlotLimits = PlotLimits(
          NC.DEFAULT_PLOT_XMIN,
          NC.DEFAULT_PLOT_XMAX,
          NC.DEFAULT_PLOT_YMIN,
          NC.DEFAULT_PLOT_YMAX
        ),
    labels: PlotLabels = PlotLabels(NC.DEFAULT_PLOT_XLABEL,NC.DEFAULT_PLOT_YLABEL)
)

class PlotModel(params: GraphicalParam, val name: String, val width: Int, val height: Int, val metadata: PlotMetaData,x: Any*) extends NetlogoModel(params,"", x) {
  
}

object PlotModel {
  def apply(params: GraphicalParam, name: String, width: Int = NC.DEFAULT_PLOT_WIDTH, height: Int = NC.DEFAULT_PLOT_HEIGHT, metadata: PlotMetaData = PlotMetaData()) = new PlotModel(params, name, width, height, metadata)
}

case class PlotPen(updateCommand: String) 

/**
 * A netlogo plot agent
 */
class NetlogoPlotAgent (plotModel: PlotModel)(maxTicks:Int = NC.DEFAULT_MAX_TICKS)(fps: Float = NC.DEFAULT_FPS) extends NetlogoAgentComponent(plotModel)(maxTicks)(fps) with Simple {
  private val pens: ListBuffer[PlotPen] = ListBuffer.empty[PlotPen]
  private val variables: Set[String] = Set.empty[String]
  
  def addPlotPens(pens: PlotPen*) = this.pens ++= pens
  def addPlotVariables(variables: String*) = this.variables ++= variables
  
  def setup = {}
  def check = {}
  def variablesPlot = {
    var varStr = ""
    variables.foreach{ varStr+= _+" "}
    varStr
  }

  def pensPlot = {
    var pensStr = ""
    pens.zipWithIndex.foreach{
      case(p,i) => pensStr += s""""${plotModel.name+"_pen"+i}" 1.0 0 -16777216 false "" "${p.updateCommand}"\n"""
    }
    pensStr
  }

  private def stringOrNil(s: String) = if(s.isEmpty) "NIL" else s

  override final def runNetlogo = {
     val eps = 40
     wait {
        frame.setSize(plotModel.width + eps, plotModel.height + eps)
        frame.setLocation(new Point(plotModel.params.pos._1, plotModel.params.pos._2))
        frame.add(comp)
        frame.setVisible(true)
        frame.setResizable(false)
        comp.openFromSource("", modelPlot)
      }
      cmd("setup")
      cmd("repeat "+maxTicks+" [ go ]")
  }
    
    def modelPlot = {s"""
globals [ ${variablesPlot}]

to setup
 clear-all
 reset-ticks
end

to go
 tick
end
@#$$#@#$$#@
GRAPHICS-WINDOW
9000
9000
9200
9200
-1
-1
13.0
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

PLOT
0
0
${plotModel.width}
${plotModel.height}
${plotModel.name}
${stringOrNil(plotModel.metadata.labels.xlabel)}
${stringOrNil(plotModel.metadata.labels.ylabel)}
${plotModel.metadata.limits.xmin}
${plotModel.metadata.limits.xmax}
${plotModel.metadata.limits.ymin}
${plotModel.metadata.limits.ymax}
true
false
"" ""
PENS
${pensPlot}

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
@#$$#@#$$#@
"""
  }
}