package agent
import akka.actor.ActorRef
import akka.actor.Actor
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
trait PortDevice extends Actor{
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
  
  /**Check if an actor is allowed to send a message on a port(in)*/
  def isIn(sender: ActorRef, portName: String): Boolean = {
    inPorts.get(portName) match {
      case None => false //TODO send exception
      case Some(listActor) => listActor.contains(sender)
    }
  }
  
  /**Check if an actor (receiver) will receive the message sent on the port(out)*/
  def isOut(receiver: ActorRef, portName: String): Boolean = {
    outPorts.get(portName) match {
      case None => false //TODO send exception
      case Some(listActor) => listActor.contains(receiver)
    }
  }
    
  override final def receive = {
    case PortMessage(message, portName)  => if(isIn(sender, portName))
                                               portReceive(message, portName)
    case in: In => this +< in
    case out: Out => this +> out
    case x: Any => normalReceive(x)
  }
  /**receive method for message that are not sent on a port*/
  def normalReceive(x: Any)
  /**receive method for message that are sent on a port*/
  def portReceive(message: Any, portName: String)
  /**send method for message associated with an port(out)*/
  final def portSend(message: Any, portName: String) = {
    outPorts.get(portName) match {
      case None => ???
      case Some(listActor) => listActor.foreach { actor => actor ! PortMessage(message, portName) }
    }
  }
  
}