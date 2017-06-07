package behavior
import akka.actor.ActorRef
import akka.actor.FSM
import akka.actor.Actor
import akka.actor.PoisonPill
sealed trait BehaviorMessage
//message types
sealed trait RequestMessage extends BehaviorMessage
sealed trait InformMessage extends BehaviorMessage

//Request messages
case object Run extends RequestMessage
case object ComplexRun extends RequestMessage
case object Stop extends RequestMessage
case object Show extends RequestMessage
class Setup(implicit val supervisor: ActorRef) extends RequestMessage {}
object Setup extends RequestMessage {
  def apply()(implicit supervisor: ActorRef) = new Setup
  def unapply(s: Setup) = Some(s.supervisor)
}
case object Poke extends RequestMessage
//Inform message
case object Finished extends InformMessage
case object Dead extends InformMessage
case class Error(e :Exception)

sealed trait BehaviorState
case object Idle extends BehaviorState
case object Ready extends BehaviorState
case object Running extends BehaviorState
case object ComplexRunning extends BehaviorState
case object FinishedRun extends BehaviorState
case object Ended extends BehaviorState
case object Killed extends BehaviorState


sealed trait BehaviorData
case object Void extends BehaviorData
object doNothing {
  def apply() = () => {}
}

/**
 * Abstract Behavior <br>
 * The classes extending this class are used by the agent
 * @constructor 
 * @param toRun the callback used to run the behavior
 * @param supervisor reference to the actor that use the behavior
 */
abstract class AbstractBehavior(toRun:() => Unit) extends FSM[BehaviorState,BehaviorData]{
  private[this] var isInit = false
  private var supervisor :ActorRef  = _
  
  /** 
   *  initialize the behavior <br>
   *  override this method when extending this class
   **/
  protected def init = {}
  
  /** run the behavior */
  def run = {
    toRun() 
    self ! FinishedRun
  }
  
  startWith(Idle, Void)
  
  when(Idle)
  {
     case  Event(Setup(s),_) => supervisor = s
                                 try {   
                                   init
                                   goto(Ready)
                                 } catch {
                                   case e :Exception => supervisor ! Error(e)
                                                        self ! Poke
                                                        goto(Killed)
                                 }
                                  
  }

  when(Ready)
  {
     case Event(Run, _) => run
                           goto(Running) 
  }
  
  when(Running)
  {
     case Event(ComplexRun, _) => self ! ComplexRun
                                  goto(ComplexRunning)
                                  
     case Event(FinishedRun, _) => self ! Poke
                                   goto(Ended) 
  }
  
  when(Ended)
  {
     case Event(Poke, _) => supervisor ! Finished
                            stop
  }
   
  when(Killed)
  {
     case Event(Poke, _) => supervisor ! Dead
                            stop
  }
  
  whenUnhandled 
  {
    case Event(Show, _) => log.info("\n"+toString)
                           stay
                           
    case Event(Stop, _) => log.debug("stopping behavior "+ self.path.name)
                           self ! Poke
                           goto(Killed)
    case _ => stay

  }
  override def toString = "behavior: "+self.path.name+"\n state: "+stateName + "\n supervisor: "+ supervisor
}
 

