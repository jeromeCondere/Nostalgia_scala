package agent
import scala.reflect.ClassTag 

abstract class Skeleton[T <: NostalgiaAgent : ClassTag]{
 def agent:T
}