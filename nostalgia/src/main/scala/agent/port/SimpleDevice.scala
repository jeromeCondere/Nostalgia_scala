package agent.port
import akka.actor.ActorRef

trait SimpleDevice {
  def normalReceive(x: Any, sender: ActorRef) = {}
  
  def portReceive(portName: String, message: Any) = {}
}