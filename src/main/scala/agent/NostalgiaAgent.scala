package agent
import akka.actor.Actor
import akka.actor.ActorRef
import scala.reflect.ClassTag 

abstract class NostalgiaAgent extends Actor {
  /**Move the agent on another place on the network and return the ref
   * of the new location
   * */
  def move: ActorRef
  /**Emit new agent*/
  def emit : Option[ActorRef]
  /**Use when two actor are colliding*/
  def collide(actor :ActorRef):ActorRef
  /**construct an agent by a skeleton*/
  final def skeleton[T <: NostalgiaAgent : ClassTag](s: Skeleton[T]):NostalgiaAgent = s.agent
  
}