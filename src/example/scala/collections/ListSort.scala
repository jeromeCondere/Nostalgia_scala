package collections
import agent.collection._
import agent.collection.list.ListAgent
import akka.actor._
import akka.pattern.{ ask, pipe }
import scala.concurrent._;
import scala.util.{Success, Failure}
import akka.util.Timeout
import scala.concurrent.duration._

class SortedListAgent(val l: List[Int]) extends ListAgent(l.sortWith((a,b) => a < b)) {
  
  override def collide(a: ActorRef) = {
    import context.dispatcher
    val f: Future[List[Int]] =  for {
      z1 <- (a ? GetAll).mapTo[List[Int]]
      z = z1:::l
    } yield z
    
     
   f.map { newlist => context.actorOf(Props(new SortedListAgent(newlist)))}
  }
}

object ListSort extends App {
  implicit val timeout = Timeout(2 seconds)
  import ExecutionContext.Implicits.global
  
  val system = ActorSystem("mySystem")
  val myActorSortedList = system.actorOf(Props(new SortedListAgent(List(1,9,6))), "myActorSortedList")
  val myActorSortedList2 = system.actorOf(Props(new SortedListAgent(List(11,-9,5))), "myActorSortedList2")
  
  val f: Future[ActorRef] = (myActorSortedList ? Collide(myActorSortedList2)).mapTo[ActorRef]
   f onComplete {
    case Success(v) => v ! Foreach(println)
    case Failure(e) => print(e.getMessage)
  }
  
}