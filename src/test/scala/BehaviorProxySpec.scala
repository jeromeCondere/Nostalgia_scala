import org.scalatest._
import behavior._
import behavior.proxy.BehaviorProxy
import akka.actor._
import akka.testkit._

class BehaviorProxySpec extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  implicit val systemSupervisor = self
  
  "A behavior proxy" must {
    "store a behavior" in {
        var a = 2
         class MyBehavior(toRun:() =>Unit) extends OneShotBehavior(toRun)
          {
             override def init = a+=2
          }
        val behaviorProxy = BehaviorProxy{
          new MyBehavior(()=>{
            a+=3
          })
        }
        
        val beRef = TestActorRef (behaviorProxy.behavior() )
        //TODO assert a=5
        
        //TODO run + assert a=8       
      }
    }
  
}