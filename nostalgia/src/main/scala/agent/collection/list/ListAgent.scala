package agent.collection.list
import agent.NostalgiaAgent
import akka.actor.Props
import akka.actor.ActorRef
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future;
import scala.util.{Success, Failure}
import akka.pattern.pipe
import agent.collection._

class ListAgent[A](l: List[A]) extends NostalgiaAgent {
 implicit val timeout = Timeout(2 seconds)
 import context.dispatcher
  
 override def move = self
 override def collide(actor :ActorRef) = Future{self}
 override def emit = Some(context.actorOf(Props(new ListAgent(l))))

 def receive = {
    case Get(i) => sender ! l(i)
    case GetAll => sender ! l
    case GetClass => sender ! this.getClass
    case Concat(li) => sender ! context.actorOf(ListAgent.props(l::li))
                       
    case Collide(a) => collide(a).pipeTo(sender)
    
    case Filter(p: ((A) => Boolean)) => sender ! context.actorOf(ListAgent.props(l.filter(p)))
    case Map(f: (A => Any)) => sender ! context.actorOf(ListAgent.props(l.map(f)))
    case Foreach(f: ((A) => Unit)) =>  l.foreach(f)
                       
  }
}
object ListAgent {
  def props[A](l: List[A])= Props(new ListAgent(l))
}