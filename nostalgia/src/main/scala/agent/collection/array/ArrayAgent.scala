package agent.collection.array
import agent.NostalgiaAgent
import akka.actor.Props
import akka.actor.ActorRef
import scala.concurrent.duration._
import akka.util.Timeout
import scala.concurrent.Future
import scala.util.{Success, Failure}
import akka.pattern.pipe
import agent.collection._

class ArrayAgent[A] (a: Array[A]) extends NostalgiaAgent {
  implicit val timeout = Timeout(2 seconds)
  import context.dispatcher
  
   override def move = self
   override def collide(actor :ActorRef) = Future{self}
   override def emit = Some(context.actorOf(Props(new ArrayAgent(a))))
   
   def receive = {
    case Get(i) => sender ! a(i)
    case GetAll => sender ! a
    case GetClass => sender ! this.getClass
    case Concat(ai) => sender ! context.actorOf(ArrayAgent.props(Array(a,ai)))
                       
    case Collide(b) => collide(b).pipeTo(sender)
    
    case Filter(p: ((A) => Boolean)) => sender ! context.actorOf(ArrayAgent.props(a.filter(p)))
    case Map(f: (A => Any)) => sender ! context.actorOf(ArrayAgent.props(a.map(f)))
    case Foreach(f: ((A) => Unit)) =>  a.foreach(f)
  }
}

object ArrayAgent {
  def props[A](a: Array[A]) = Props(new ArrayAgent(a))
}