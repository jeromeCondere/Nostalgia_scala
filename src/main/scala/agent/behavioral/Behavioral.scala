package agent.behavioral
import scala.collection.mutable.ArrayBuffer
import behavior.proxy._

import agent.NostalgiaAgent
import agent.{Message, AskMessage, InformMessage, Run, Finished}
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

sealed trait BehaviorAgentMessage extends Message
case object Setup extends BehaviorAgentMessage
case object Next extends BehaviorAgentMessage
case object End extends BehaviorAgentMessage

trait Behavioral extends FSM[BehaviorAgentState,Int]{
  val behaviors: ArrayBuffer[ActorRef] = ArrayBuffer.empty
  val behaviorsProxyList: ArrayBuffer[BehaviorProxy[AbstractBehavior]] = ArrayBuffer.empty
  
  final def addBehavior(behaviorProxy: BehaviorProxy[AbstractBehavior]) = {
    behaviorsProxyList+= behaviorProxy
  }
  
  def setup = {
    behaviorsProxyList.foreach{
      behaviorProxy => val behavior = context.actorOf(Props(behaviorProxy.behavior())) 
                                   behaviors += behavior
    }
  }
  
  startWith(Idle, -1)
  
  when(Idle)
  {
     case  Event(Setup,_) => setup
                             goto(Ready)
                                  
  }
  
  //the actor who command the behavior agent
  private[this] var supervisor: ActorRef = _
  
  when(Ready)
  {
    case  Event(Run,_) => supervisor = sender
                          self ! Run
                          goto(Running) using 0
  }
  
  when(Running)
  {
    case Event(behavior.Finished, index) => if(index < behaviors.size -1)
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
     case  Event(End, _) => //context.parent ! Finished
                            supervisor ! Finished
                            stop
  }
  
}