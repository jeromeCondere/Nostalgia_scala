package agent.simulation.graphical.netlogo
import agent.simulation.graphical.GraphicalAgent
import org.nlogo.lite.InterfaceComponent

abstract class NetlogoAgent(netlogoModel : NetlogoModel)(maxTicks:Int = 1000) extends GraphicalAgent(netlogoModel) {
  protected  val frame = new javax.swing.JFrame
  protected  val comp = new InterfaceComponent(frame)
  
  final def cmd(cmdString: String) = comp.command(cmdString)
  final def report(reportString: String) = comp.report(reportString)
  
  final def run = {
    wait {
      frame.setSize(netlogoModel.params.dim._1, netlogoModel.params.dim._1)
      frame.add(comp)
      frame.setVisible(true)
      comp.open(netlogoModel.path)
    }
     cmd("setup")
     cmd("repeat "+maxTicks+" [ go ]")
  }
  
  final def wait(block: => Unit) {
    java.awt.EventQueue.invokeAndWait(
    new Runnable() { def run() { block } } ) 
  }
  
}