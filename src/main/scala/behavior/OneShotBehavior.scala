package behavior

/**
 * OneShotBehavior <b
import behavior.AbstractBehaviorr>
 * A behavior that run only once and when finished send a Finished message to its supervisor
 * 
 * @constructor 
 * @param toRun the callback used to run the behavior
 * @param supervisor reference to the actor that use the behavior
 */
class OneShotBehavior(toRun:() =>Unit) extends AbstractBehavior(toRun) {

}

/** One shot behavior object (A behavior that run only once)*/
object OneShotBehavior {
  def apply(toRun: =>Unit) = new OneShotBehavior(() => toRun)
}
