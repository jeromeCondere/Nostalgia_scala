package behavior.proxy
import behavior.AbstractBehavior
/**
 *  a class that allows the user to store a future instance of a behavior
 */
final class BehaviorProxy  [+A <: AbstractBehavior] (val behavior:() => A) {

}

object BehaviorProxy {
  def apply[A <: AbstractBehavior](behavior: => A) = new BehaviorProxy[A](() => behavior)
}
