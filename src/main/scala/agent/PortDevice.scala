package agent
import akka.actor.ActorRef
import akka.actor.Actor
import scala.collection.mutable.Map
sealed trait Port
/**(self, in) << (actor,out)*/
case class In(actor: ActorRef, inPort: String, outPort: String) extends Port
/**(self,out) >> (actor, in)*/
case class Out(actor: ActorRef, outPort: String, inPort: String) extends Port

case class PortMessage(message: Any, outPort: String, inPort: String) extends InformMessage
case class IsIn(in: In)
case class IsOut(out: Out)

case class PortInput(val name: String, var actorList: List[ActorRef])

/**this trait is used to define a port system <br>
 * whenever a PortMessage is send on a port (Out port) for all actors link on this port they <br>
 * receive the same message
 * <br><br>
 * if a PortMessage is received by an actorRef we check if the actor is allowed to send on <br>
 * this port
 * */
trait PortDevice extends Actor{
  val inPorts: Map[String, Set[(ActorRef, String)]] = Map.empty
  val outPorts: Map[String, Set[(ActorRef, String)]] = Map.empty
  
  /**add an actor to a port(In)*/
  def +< (in: In) = {
    inPorts.get(in.inPort) match {
      case None => inPorts += (in.inPort -> Set((in.actor,in.outPort)) )
      case Some(listActor) => inPorts(in.inPort) = listActor + ((in.actor, in.outPort))
    }
  }
  
  /**add an actor to a port(Out)*/
  def +> (out: Out) = {
    outPorts.get(out.outPort) match {
      case None => outPorts += (out.outPort -> Set((out.actor,out.inPort)) )
      case Some(listActor) => inPorts(out.outPort) = listActor + ((out.actor, out.inPort))
    }
  }
  
  /**Check if an actor is allowed to send a message on a port(in)*/
  def isIn(in: In): Boolean = {
    inPorts.get(in.inPort) match {
      case None => false //TODO send exception
      case Some(listActor) => listActor.contains((in.actor, in.outPort))
    }
  }
  
  /**Check if an actor (receiver) will receive the message sent on the port(out)*/
  def isOut(out: Out): Boolean = {
    outPorts.get(out.outPort) match {
      case None => false //TODO send exception
      case Some(listActor) => listActor.contains((out.actor, out.inPort))
    }
  }
    
  override final def receive = {
    case PortMessage(message, outPort, inPort)  => if(isIn(In(sender, inPort, outPort )))
                                                      portReceive(message, inPort)
    case in: In => this +< in
    case out: Out => this +> out
    case IsIn(in) => sender ! isIn(in)
    case IsOut(out) => sender ! isOut(out)
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
      case Some(listActor) => listActor.foreach { case (actor, inPort) => actor ! PortMessage(message, portName, inPort) }
    }
  }
  
}