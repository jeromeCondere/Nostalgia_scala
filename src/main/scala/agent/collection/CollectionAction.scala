package agent.collection
import akka.actor.ActorRef

trait CollectionAction {}
case class Get(index: Int) extends CollectionAction
case object GetAll extends CollectionAction
case object GetClass extends CollectionAction
case class Concat[B](l:List[B]) extends CollectionAction
case class Filter[A](p: (A) => Boolean) extends CollectionAction
case class Collide(a: ActorRef) extends CollectionAction
case class Map[A,B](f: (A) => B ) extends CollectionAction
case class Foreach[A](f: (A) => Unit) extends CollectionAction