package behavior
import akka.actor.ActorRef
import akka.actor.Props
import proxy.BehaviorProxy
import scala.reflect.ClassTag 
import scala.reflect._
/**
 * ParralelBehavior	 <br>
 * A behavior that runs a list of behaviors asynchronously
 * @constructor
 * @param list of behaviors to run asynchronously 
 * @param toRun the callback used to run the behavior
 * @param supervisor reference to the actor that use the behavior
 */
class ParralelBehavior[A <: AbstractBehavior : ClassTag](behaviorProxyList:List[BehaviorProxy[A]]) extends ComplexBehavior(() => {}){
 
  private var behaviorsNotFinished: List[String] = List()
  
  /** setup all Behaviors */
  override final protected def init = {
   behaviorProxyList.zipWithIndex.foreach {
      case(behaviorProxy,index) => val name =  ("@"+self.path.name +"_parallelBehavior"+ index).filterNot("$".toSet)
                                   val actor = context.actorOf(Props(behaviorProxy.behavior()),name)
                                   context.watch(actor)
                                   behaviorsNotFinished = name::behaviorsNotFinished
                                   actor ! Setup()
    }
  }
  
  def complexRun = {
     context.children.foreach { 
       behavior => behavior ! Run
     }
   }
  
  when(ComplexRunning)
  {
    case Event(ComplexRun, _) => complexRun
                                 stay
    
    case Event(Finished, _) => if(!behaviorsNotFinished.isEmpty) {
                                 val behaviorName = sender.path.name
                                 behaviorsNotFinished = behaviorsNotFinished.filter { name => name != behaviorName}

                                 if(behaviorsNotFinished.isEmpty) 
                                   self ! FinishedRun
                               } 
                               else 
                                 self ! FinishedRun
                               stay

    case Event(FinishedRun, _) => self ! Poke
                                  goto(Ended) 
  }
  
}

/** ParralelBehavior (a behavior that runs a list of behaviors asynchronously)*/
object ParralelBehavior {
  def apply[A <: AbstractBehavior : ClassTag](behaviorProxyList:List[BehaviorProxy[A]]) = new ParralelBehavior(behaviorProxyList)
  def props[A <: AbstractBehavior : ClassTag](behaviorProxyList:List[BehaviorProxy[A]]): Props = Props(ParralelBehavior(behaviorProxyList))
}