package agent.port
import akka.actor.ActorRef


object Pattern {
  implicit class Connection (t: (ActorRef, String)){
    /**Create an input connection (t, tp) << (p, pp)*/
    def <<+ (p: (ActorRef, String)) = t._1 ! In(p._1, t._2, p._2)
    def >>+ (p: (ActorRef, String)) = t._1 ! Out(p._1, t._2, p._2)
  }
}