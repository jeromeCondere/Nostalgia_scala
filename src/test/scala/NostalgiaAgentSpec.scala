import org.scalatest._
import behavior.OneShotBehavior
import behavior.TimerBehavior
import behavior.TickerBehavior
import behavior.proxy.BehaviorProxy
import agent._
import akka.actor._
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Future;
import agent.port.PortDevice
import agent.port.SimpleDevice
import agent.port.Out
import agent.port.IsOut
import agent.port.IsIn
import agent.port.In
import agent.port.PortMessage

class NostalgiaAgentSpec extends TestKit(ActorSystem("NostalgiaAgentSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  implicit val systemSupervisor = self
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  "A behaviorAgent" must {
    import agent.behavioral._
    "execute correctly (OneShotBehavior)" in {
      var a = 2
        class MyBehaviorAgent extends BehaviorAgent with Simple {
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=2
            }))
            
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=4
            }))
         }

        val behaviorAgent = TestActorRef(new MyBehaviorAgent(), "myBehaviorAgent")
        behaviorAgent ! Setup
        behaviorAgent ! Run
       
   
        awaitCond(a == 8, 70 millis)
        expectMsg(Finished)
      }
    "execute correctly (TimerBehavior)" in {
        var a = 3
        class MyBehaviorAgent extends BehaviorAgent with Simple {
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=2
            }))
            
            addBehavior(BehaviorProxy(TimerBehavior(200 millis){
              a+=4
            }))
         }

        val behaviorAgent = TestActorRef(new MyBehaviorAgent(), "myTimerBehaviorAgent")
        behaviorAgent ! Setup
        behaviorAgent ! Run
       
        awaitCond(a == 9, 270 millis)
        expectMsg(Finished)
    }
    "execute correctly (TickerBehavior)" in {
        var a = 3
        var b = 4
        class MyTickerBehavior(period:FiniteDuration)(toRun:() =>Unit) extends TickerBehavior(period:FiniteDuration)(toRun)
        {
          override protected def stopTicker: Boolean = {
            b == 12
          }
        }
        
        class MyBehaviorAgent extends BehaviorAgent with Simple {
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=2
            }))
            
            addBehavior(BehaviorProxy(new MyTickerBehavior(50 millis)(() => {
              a+=4
              b+=2
            })))
         }

        val behaviorAgent = TestActorRef(new MyBehaviorAgent(), "myTimerBehaviorAgent")
        behaviorAgent ! Setup
        behaviorAgent ! Run
        
        expectNoMsg(200 millis)
        awaitCond(a == 21 && b == 12, 280 millis)// we must add the 50 millis delay
        expectMsg(Finished)
    }
    "execute correctly (ParallelBehavior)" in {
      fail
    }
 }
  "a PortDevice" must {
    class myAgent extends NostalgiaAgent with Simple with PortDevice with SimpleDevice {}
    "correctly add a in/out connection" in {
      
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
    "correctly add a in/out connection(with pattern)" in {
      import agent.port.Pattern.Connection
      
      val portAgent1 = TestActorRef (new myAgent(), "portAgent11")
      val portAgent2 = TestActorRef (new myAgent(), "portAgent22")
      
      
      (portAgent2, "inA2") <<+ (portAgent1, "outA1") 
      (portAgent1, "outA1") >>+ (portAgent2, "inA2")
      
       
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
      import agent.port.Pattern.Connection
      
      class MyDeviceAgent extends NostalgiaAgent with Simple with PortDevice {
        var probe: ActorRef = _
        
        def normalReceive(x: Any, sender: ActorRef) = {
          x match {
            case a: ActorRef => probe = a
          }
        }
        
        def portReceive(message: Any, portName: String) = {
          val t = (message, portName)
          probe.tell(t, self)
        }
      }
      
      val probe1 = TestProbe()
      val myDeviceAgent1 = system.actorOf(Props(new MyDeviceAgent()), "myDeviceAgent1")
      val myDeviceAgent2 = system.actorOf(Props(new MyDeviceAgent()), "myDeviceAgent2")
      (myDeviceAgent1, "out1") >>+ (myDeviceAgent2, "in2")
      (myDeviceAgent2, "in2") <<+ (myDeviceAgent1, "out1")
      
      fail
    }
  }
}