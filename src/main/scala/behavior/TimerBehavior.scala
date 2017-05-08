package behavior
import scala.concurrent.duration._
import akka.actor.ActorRef

/**
 * TimerBehavior <br>
 * A behavior that run only once after a delay and when finished send a Finished message to its supervisor
 * 
 * @constructor 
 * @param toRun the callback used to run the behavior
 * @param supervisor reference to the actor that use the behavior
 * @param delay amount of time to wait before run the behavior
 */
class TimerBehavior(delay:FiniteDuration)(toRun:() => Unit) extends AbstractBehavior(toRun) {

  override final def run() =
  {
    val system = context.system
    import system.dispatcher
    system.scheduler.scheduleOnce(delay){ 
      toRun() 
      self ! FinishedRun
      } 
  }
}
object TimerBehavior {
  def apply(delay:FiniteDuration)(toRun: =>Unit) = new TimerBehavior(delay)(()=> toRun)
}
