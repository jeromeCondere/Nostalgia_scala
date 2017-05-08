package behavior

import behavior.ComplexRun

import behavior.AbstractBehavior

import akka.actor.actorRef2Scala

abstract class ComplexBehavior(toRun:() =>Unit)  extends AbstractBehavior(toRun){
  final override def run = {
    self ! ComplexRun
  }
}