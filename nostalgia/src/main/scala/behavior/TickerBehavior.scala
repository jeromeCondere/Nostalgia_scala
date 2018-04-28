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
class TickerBehavior (period:FiniteDuration)(toRun:() => Unit) extends ComplexBehavior(toRun){
  
  private[this]  var sched: Cancellable = null
  
  final def tickRun = {
    if(stopTicker == false)
    {
      toRun() 
    } else {  
      self ! FinishedRun
      sched.cancel
    }
  }
  
  final def complexRun =
  {
    val system = context.system
    import system.dispatcher
    sched = system.scheduler.schedule(50 millis, period, self, Tick);
  }

  when(ComplexRunning)
  {
    case Event(ComplexRun, _) => complexRun
                                 stay

    case Event(Tick, _) => tickRun
                           stay

    case Event(FinishedRun, _) => self ! Poke
                                  goto(Ended) 
  }
  
  /** ending condition to finish the behavior*/
  protected def stopTicker:Boolean = {false}
}

object TickerBehavior {
  def apply(delay: FiniteDuration)(toRun: => Unit) = new TickerBehavior(delay)(()=> toRun)
  def props(delay: FiniteDuration)(toRun: => Unit): Props = Props(TickerBehavior(delay)(toRun))
}
