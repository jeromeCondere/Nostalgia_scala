import org.scalatest._
import behavior.OneShotBehavior
import behavior.proxy.BehaviorProxy
import agent.behavioral._
import akka.actor._
import akka.testkit._
import scala.concurrent.duration._

class NostalgiaAgentSpec extends TestKit(ActorSystem("NostalgiaAgentSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  implicit val systemSupervisor = self
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  "A behaviorAgent" must {
    "execute correctly (OneShotBehavior)" in {
        class MyBehaviorAgent extends BehaviorAgent {
            var a = 2
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=2
            }))
            
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=4
            }))
            
            override def move = self
            override def collide(a : ActorRef) = self
            override def emit = Some(self)
         }
      
        val nasRef = TestActorRef (new MyBehaviorAgent(), "myBehavior")
        val nas = nasRef.underlyingActor
        nasRef ! Setup
        nasRef ! Run
        awaitCond(nas.a == 8, 70 millis)
      }
      
    }
}