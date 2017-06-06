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

import org.nlogo.lite.InterfaceComponent

/**A class used to create a classic netlogoAgent that run the model nlogo file*/
abstract class NetlogoAgent(netlogoModel : NetlogoModel)(maxTicks:Int = 1000)(val fps: Int = 30) extends GraphicalAgent(netlogoModel) {
  protected  val frame = new javax.swing.JFrame
  protected  val comp = new InterfaceComponent(frame)
  println("fps "+fps)
  final def cmd(cmdString: String) = comp.command(cmdString)
  final def report(reportString: String) = comp.report(reportString)
  
  final def runNetlogo = {
    wait {
      frame.setSize(netlogoModel.params.dim._1, netlogoModel.params.dim._2)
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
  
  final def run = {
   val eps = 5
   implicit val timeout = Timeout((maxTicks + eps)/fps seconds)
   import context.dispatcher
   
   val behaviorAgent = context.actorOf(Props(new NostalgiaBehaviorAgent()))
   behaviorAgent ! Setup

   //TODO add treatment when receiving Finished message
   val futureFinished = (behaviorAgent ? agent.Run).mapTo[agent.Finished.type]
   futureFinished onComplete {
    case Success(v) => context.parent ! agent.Finished
                       
    case Failure(e) => println(e.getMessage)
   }
  }
  
  /**setup function before running the netlogo model*/
  def setup
  
  /**this function is called repeatedly to check netlogo event with report and <br>
   * send them to another netlogo agent
   * */
  def check
  
  // a netlogo agent uses a behavior agent in order to run both runNetlogo and check
  class NostalgiaBehaviorAgent extends BehaviorAgent with Simple {
    var tick = 0
    
      class NostalgiaTickerBehavior(period:FiniteDuration)(toRun:() =>Unit) extends TickerBehavior(period:FiniteDuration)(toRun)
      {
        override final def stopTicker: Boolean = {
          tick > maxTicks
        }
      }
    
     addBehavior(BehaviorProxy(OneShotBehavior{
       NetlogoAgent.this.setup
      }))

      
      val bpCheck = BehaviorProxy(new NostalgiaTickerBehavior((1/fps) seconds)(()=> {
        tick+=1
        check
      }))
      
      val bpRunNetlogo = BehaviorProxy(OneShotBehavior{
        runNetlogo
      })

      val listBp = List(bpRunNetlogo, bpCheck)
      addBehavior(BehaviorProxy(ParralelBehavior(listBp)))
   }
  
}