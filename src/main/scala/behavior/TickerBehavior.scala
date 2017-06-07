package behavior
import scala.concurrent.duration._
import akka.actor.ActorRef
import akka.actor.Props
import akka.actor.Cancellable
case object Tick
/**
 * TickerBehavior <br>
 * A behavior that run according to a frequency
 * 
 * @constructor 
 * @param toRun the callback used to run the behavior
 * @param supervisor reference to the actor that use the behavior
 * @param period amount of time between two runs
 */
class TickerBehavior (period:FiniteDuration)(toRun:() => Unit) extends  AbstractBehavior(toRun){
  
  //once the behavior finished to run it ask for run again
  private[this] var isStarted = false
  private[this]  var sched:Cancellable = null
  override final def run =
  {
    if (isStarted == false)
    {
      val system = context.system
      import system.dispatcher
      sched = system.scheduler.schedule(50 millis, period, self, Tick);
      isStarted = true
    }
    
  }
  // re override in order to add Tick event gestion
  whenUnhandled 
  {
    case Event(Tick, _) =>  if(stopTicker == false)
                            {
                              toRun() 
                            } else {  
                              self ! FinishedRun
                              sched.cancel
                            }
                           stay
    case Event(Show, _) => log.info("\n"+toString)
                           stay
                           
    case Event(Stop, _) => log.debug("stopping behavior "+ self.path.name)
                           self ! Poke
                           goto(Killed)
    case _ => stay
  }
  /** ending condition to finish the behavior*/
  protected def stopTicker:Boolean = {false}
}

object TickerBehavior {
  def apply(delay:FiniteDuration)(toRun: =>Unit) = new TickerBehavior(delay)(()=> toRun)
  def props(delay:FiniteDuration)(toRun: =>Unit): Props = Props(TickerBehavior(delay)(toRun))
}
