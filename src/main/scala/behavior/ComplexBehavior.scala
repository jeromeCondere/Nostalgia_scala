package behavior

abstract class ComplexBehavior(toRun:() =>Unit)  extends AbstractBehavior(toRun){
  final override def run = {
    self ! ComplexRun
  }
}