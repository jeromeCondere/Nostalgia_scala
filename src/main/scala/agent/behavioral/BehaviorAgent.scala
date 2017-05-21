package agent.behavioral
import scala.collection.mutable.ArrayBuffer
import behavior.proxy._

import agent.NostalgiaAgent
import behavior.AbstractBehavior
import akka.actor.FSM
import akka.actor.ActorRef

sealed trait BehaviorAgentState
case object Idle
case object Ended
case object Error
case object BehaviorActive

sealed trait BehaviorAgentData
sealed trait BehaviorAgentMessage


abstract class BehaviorAgent extends NostalgiaAgent with FSM[BehaviorAgentState,BehaviorAgentData] {
  val behaviors: ArrayBuffer[ActorRef] = ArrayBuffer.empty
  val behavior: ArrayBuffer[BehaviorProxy[AbstractBehavior]]
  
  def addBehavior(behaviorProxy: AbstractBehavior) = {
    //behaviors
  }
  
}