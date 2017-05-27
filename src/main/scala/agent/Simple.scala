package agent
import scala.concurrent.Future;
import akka.actor._

trait Simple extends NostalgiaAgent {
  import context.dispatcher
  
  override def move = self
  override def collide(a : ActorRef) = Future{self}
  override def emit = Some(self)
}