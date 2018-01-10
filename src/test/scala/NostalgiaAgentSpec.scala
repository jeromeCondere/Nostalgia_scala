import org.scalatest._
import behavior.OneShotBehavior
import behavior.TimerBehavior
import behavior.TickerBehavior
import behavior.ParralelBehavior
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
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
  "A behaviorAgent" must {
    import agent.behavioral._
    "execute correctly (OneShotBehavior)" in {
      val testProbe = TestProbe()
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
        testProbe.send(behaviorAgent, Setup)
        testProbe.send(behaviorAgent, Run)
   
        testProbe.awaitCond(a == 8, 70 millis)
        testProbe.expectMsg(Finished)
      }
    "execute correctly (TimerBehavior)" in {
        val testProbe = TestProbe()
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
        testProbe.send(behaviorAgent, Setup)
        testProbe.send(behaviorAgent, Run)
       
        testProbe.awaitCond(a == 9, 270 millis)
        testProbe.expectMsg(Finished)
    }
    "execute correctly (TickerBehavior)" in {
        val testProbe = TestProbe()
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
        testProbe.send(behaviorAgent, Setup)
        testProbe.send(behaviorAgent, Run)
        
        testProbe.expectNoMsg(200 millis)
        testProbe.awaitCond(a == 21 && b == 12, 280 millis)// we must add the 50 millis delay
        testProbe.expectMsg(Finished)
    }
    "execute correctly (ParallelBehavior)" in {
      val testProbe = TestProbe()
      var a = 3
      var b = 5
      val bp1 = BehaviorProxy{OneShotBehavior{
              a+=2
      }}
      val bp2 = BehaviorProxy{OneShotBehavior{
              b+=9
      }}
      val listBp = List(bp1,bp2)
      
        class MyBehaviorAgent extends BehaviorAgent with Simple {
            addBehavior(BehaviorProxy(OneShotBehavior{
              a+=2
            }))
            
            addBehavior(BehaviorProxy(ParralelBehavior(listBp)))
         }
      
      val behaviorAgent = TestActorRef(new MyBehaviorAgent(), "myParralelBehaviorAgent")
      testProbe.send(behaviorAgent, Setup)
      testProbe.send(behaviorAgent, Run)

      
      testProbe.awaitCond(a == 7 && b == 14)
      testProbe.expectMsg(Finished)
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
      val probe = TestProbe()
      
      (portAgent2, "inA2") <<+ (portAgent1, "outA1") 
      (portAgent1, "outA1") >>+ (portAgent2, "inA2")
      
       
      probe send ( portAgent2,  IsIn(In(portAgent1, "inA2", "outA1")) )
      probe.expectMsg(true)
      probe send (portAgent2, IsIn(In(portAgent1, "inA7", "outA1")) )
      probe.expectMsg(false)
      
      probe send (portAgent1, IsOut(Out(portAgent2, "outA1", "inA2")) )
      probe.expectMsg(true)
      probe send (portAgent1, IsOut(Out(portAgent2, "outA7", "inA2")) )
      probe.expectMsg(false)
      
    }
    "send the message to the corrects agents" in {
      import agent.port.Pattern.Connection
      import agent.port._
      class MyDeviceAgent extends NostalgiaAgent with Simple with PortDevice {
        var probe: ActorRef = _
        
        def normalReceive(x: Any, sender: ActorRef) = {
          x match {
            case a: ActorRef => probe = a
            case (outPort: String, message: Any) => portSend(outPort, message)
          }
        }
        
        def portReceive(portName: String, message: Any) = {
          val t = (portName, message: Any)
          probe.tell(t, self)
        }
      }
      
      val probe1 = TestProbe()
      val probe2 = TestProbe()
      val myDeviceAgent1 = system.actorOf(Props(new MyDeviceAgent()), "myDeviceAgent1")
      val myDeviceAgent2 = system.actorOf(Props(new MyDeviceAgent()), "myDeviceAgent2")
      //  (myDeviceAgent1, "out1") --> (myDeviceAgent2, "in2")
      (myDeviceAgent1, "out1") >>+ (myDeviceAgent2, "in2")
      (myDeviceAgent2, "in2") <<+ (myDeviceAgent1, "out1")
      
      myDeviceAgent1 ! probe1.ref
      myDeviceAgent2 ! probe2.ref
      
      myDeviceAgent1 ! ("out1", "message to in2")
      probe2.expectMsg(("in2", "message to in2"))
      
    }
  }
}