package agent.simulation.graphical.netlogo
import agent.simulation.graphical.GraphicalAgent
import agent.behavioral.BehaviorAgent
import agent.behavioral.Setup
import agent.Simple
import behavior.OneShotBehavior
import behavior.TickerBehavior
import behavior.ParralelBehavior
import behavior.proxy.BehaviorProxy
import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.actor.Props
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent._;
import scala.util.{Success, Failure}
import java.awt.Point

import org.nlogo.lite.InterfaceComponent
import org.nlogo.core.CompilerException
import org.nlogo.api.LogoException

/**A class used to create a classic netlogoAgent that run the model .nlogo file*/
abstract class NetlogoAgent(netlogoModel : NetlogoModel)(val maxTicks:Int = NetlogoConstants.DEFAULT_MAX_TICKS)(val fps: Float = NetlogoConstants.DEFAULT_FPS) extends GraphicalAgent(netlogoModel) {
  protected  val frame = new javax.swing.JFrame
  protected  val comp = new InterfaceComponent(frame)

  final def cmd(cmdString: String) = comp.command(cmdString)
  
  @throws(classOf[CompilerException])
  @throws(classOf[LogoException])
  final def report(source: String): AnyRef = {
    try {
      return comp.report(source)
    } catch {
      case compilerException: CompilerException => throw compilerException
      case logoException: LogoException => throw logoException
    }
  }
  
  def runNetlogo = {
    wait {
      frame.setSize(netlogoModel.params.dim._1, netlogoModel.params.dim._2)
      frame.setLocation(new Point(netlogoModel.params.pos._1, netlogoModel.params.pos._2))
      frame.add(comp)
      frame.setVisible(true)
      comp.open(netlogoModel.path)
    }
     cmd("setup")
     cmd("repeat "+maxTicks+" [ go ]")
  }
  
  final def wait(block: => Unit) {
    java.awt.EventQueue.invokeAndWait(
      new Runnable() { def run() { block } } 
    ) 
  }
  /**Runs the netlogo model*/
  final def run = {
   val eps = 5
   implicit val timeout = Timeout((maxTicks + eps)/fps seconds)
   import context.dispatcher
   
   val behaviorAgent = context.actorOf(Props(new NostalgiaBehaviorAgent()))
   behaviorAgent ! Setup

   val futureFinished = (behaviorAgent ? agent.Run).mapTo[agent.Finished.type]
   futureFinished onComplete {
    case Success(v) => context.parent ! agent.Finished
                       
    case Failure(e) => println(e.getMessage)
   }
  }
  
  /**setup function before running the netlogo model*/
  def setup
  
  /**function called whenever the netlogo ticks
   * */
  def check
  
  // a netlogo agent uses a behavior agent in order to run both runNetlogo and check
  class NostalgiaBehaviorAgent extends BehaviorAgent with Simple {
    var tick = 0

      addBehavior(BehaviorProxy(OneShotBehavior{
       NetlogoAgent.this.setup
       comp.listenerManager.addListener(new NetlogoSimpleListener{
         override def tickCounterChanged(ticks: Double) = {
           if(tick < maxTicks)
           {
             tick = ticks.toInt
             check
           }
         }
       })
      }))

      addBehavior(BehaviorProxy(OneShotBehavior{
        runNetlogo
      }))
   }
  
}