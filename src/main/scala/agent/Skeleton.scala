package agent
import scala.reflect.ClassTag 
/**A class use to reconstruct a Nostalgia agent with the parameters*/
abstract class Skeleton[T <: NostalgiaAgent : ClassTag]{
 def agent:T
}