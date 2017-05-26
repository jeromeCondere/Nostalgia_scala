package agent
import akka.actor.ActorRef
import scala.collection.mutable.Map
sealed trait Port
/**self << actor*/
case class In(actor: ActorRef, portName: String) extends Port
/**self >> actor*/
case class Out(actor: ActorRef, portName: String) extends Port

case class PortMessage(message: Any, portName: String)

case class PortInput(val name: String, var actorList: List[ActorRef])

/**this trait is used to define a port system <br>
 * whenever a PortMessage is send on a port (Out port) for all actors link on this port they <br>
 * receive the same message
 * <br><br>
 * if a PortMessage is received by an actorRef we check if the actor is allowed to send on <br>
 * this port
 * */
trait PortDevice {
  val inPorts: Map[String, List[ActorRef]] = Map.empty
  val outPorts: Map[String, List[ActorRef]] = Map.empty
  
  /**add an actor to a port(In)*/
  def +< (in: In) = {
    inPorts.get(in.portName) match {
      case None => inPorts += (in.portName -> List(in.actor) )
      case Some(listActor) => inPorts(in.portName) = in.actor::listActor
    }
  }
  
  /**add an actor to a port(Out)*/
  def +> (out: Out) = {
    inPorts.get(out.portName) match {
      case None => inPorts += (out.portName -> List(out.actor) )
      case Some(listActor) => inPorts(out.portName) = out.actor::listActor
    }
  }
  
}