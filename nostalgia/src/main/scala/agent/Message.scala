package agent
/**Base trait for agent messaging*/
trait Message {
  
}
/*
 * TODO mutuaize every standard Message here
 * (finshed, Running, etc.)
 */
trait AskMessage extends Message
case object Run extends AskMessage
case object Stop extends AskMessage

trait InformMessage extends Message
case object Finished extends InformMessage
case object Stopped extends InformMessage
case class Error(e: Exception) extends InformMessage