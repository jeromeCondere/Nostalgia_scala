package agent.behavioral
import scala.collection.mutable.ArrayBuffer
import behavior.proxy._

import agent.NostalgiaAgent
import behavior.AbstractBehavior
import akka.actor.FSM
import akka.actor.ActorRef
import akka.actor.Props
import scala.reflect.ClassTag 

sealed trait BehaviorAgentState
case object Idle extends BehaviorAgentState
case object Ready extends BehaviorAgentState
case object Running extends BehaviorAgentState
case object Ended extends BehaviorAgentState
case object Error extends BehaviorAgentState
case object BehaviorActive extends BehaviorAgentState

sealed trait BehaviorAgentMessage
case object Setup extends BehaviorAgentMessage
case object Run extends BehaviorAgentState
case object Next extends BehaviorAgentState
case object End extends BehaviorAgentState


abstract class BehaviorAgent extends NostalgiaAgent with FSM[BehaviorAgentState,Int] {
  val behaviors: ArrayBuffer[ActorRef] = ArrayBuffer.empty
  val behaviorsProxyList: ArrayBuffer[BehaviorProxy[AbstractBehavior]]
  
  final def addBehavior[A <: AbstractBehavior : ClassTag](behaviorProxy: BehaviorProxy[A]) = {
    //behaviorsProxyList+= behaviorProxy
  }
  
  def setup = {
    behaviorsProxyList.zipWithIndex.foreach{
      case(behaviorProxy,index) => val behavior = context.actorOf(Props(behaviorProxy.behavior()),"") 
                                   behaviors += behavior
    }
  }
  
  when(Idle)
  {
     case  Event(Setup,_) => setup
                             goto(Ready)
                                  
  }
  
  when(Ready)
  {
    case  Event(Run,_) => self ! Run
                          goto(Running) using 0
  }
  
  when(Running)
  {
    case Event(behavior.Finished, index) => if(index < behaviors.size -2)
                                            {
                                              self ! Run
                                              stay using index + 1
                                            } else {
                                              self ! End
                                              goto(Ended)
                                            }
                                            
    case  Event(Run, index) => behaviors(index) ! behavior.Setup()
                               behaviors(index) ! behavior.Run
                               stay
  }
  
  when (Ended)
  {
     case  Event(End, _) => stop()
  }
  
  
}