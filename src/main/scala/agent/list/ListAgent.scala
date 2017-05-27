package agent.list
import agent.NostalgiaAgent
import akka.actor.Props
import akka.actor.ActorRef
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future;

trait ListAction
case class Get(index: Int) extends ListAction
case object GetAll extends ListAction
case object GetClass extends ListAction
case class Concat[B](l:List[B]) extends ListAction
case class Filter[A](p: (A) => Boolean) extends ListAction
case class Collide(a: ActorRef) extends ListAction
case class Map[A,B](f: (A) => B ) extends ListAction

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
    case Concat(li) => val a = Props(new ListAgent(l::li))
                       sender ! context.actorOf(a)
                       
    case Collide(a) => sender ! collide(a)
    
    case Filter(p: ((A) => Boolean)) => val a = Props(new ListAgent(l.filter(p)))
                                        sender ! context.actorOf(a)
    case Map(f: (A => Any)) => val a = Props(new ListAgent(l.map(f)))
                                        sender ! context.actorOf(a)
    

                       
  }
}