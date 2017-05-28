import org.scalatest._
import behavior.OneShotBehavior
import behavior.proxy.BehaviorProxy
import agent.behavioral._
import agent._
import akka.actor._
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Future;

class NostalgiaAgentSpec extends TestKit(ActorSystem("NostalgiaAgentSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  implicit val systemSupervisor = self
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  "A behaviorAgent" must {
    "execute correctly (OneShotBehavior)" in {
        class MyBehaviorAgent extends BehaviorAgent with Simple {
            var a = 2
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=2
            }))
            
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=4
            }))
         }
      
        val nasRef = TestActorRef (new MyBehaviorAgent(), "myBehavior")
        val nas = nasRef.underlyingActor
        nasRef ! Setup
        nasRef ! Run
        awaitCond(nas.a == 8, 70 millis)
      }
    "execute correctly (TimerBehavior)" in {
      fail
    }
    "execute correctly (TickerBehavior)" in {
      fail
    }
 }
  "a PortDevice" must {
    "correctly add a in/out connection" in {
      class myAgent extends NostalgiaAgent with Simple with PortDevice {
        def normalReceive(x: Any) = {
          
        }
        def portReceive(message: Any, portName: String) = {
          
        }
      }
      val portAgent1 = TestActorRef (new myAgent(), "portAgent1")
      val portAgent2 = TestActorRef (new myAgent(), "portAgent2")
      
      portAgent2 ! In(portAgent1, "inA2", "outA1") //(portAgent2, inA2) << (portAgent1, outA1)
      portAgent1 ! Out(portAgent2, "outA1", "inA2")//(portAgent1, outA1) >> (portAgent2, inA2)
      
       
      portAgent2 ! IsIn(In(portAgent1, "inA2", "outA1"))
      expectMsg(true)
      portAgent2 ! IsIn(In(portAgent1, "inA7", "outA1"))
      expectMsg(false)
      
      portAgent1 ! IsOut(Out(portAgent2, "outA1", "inA2"))
      expectMsg(true)
      portAgent1 ! IsOut(Out(portAgent2, "outA7", "inA2"))
      expectMsg(false)
      
    }
    "send the message to the corrects agents" in {
      fail
    }
  }
}