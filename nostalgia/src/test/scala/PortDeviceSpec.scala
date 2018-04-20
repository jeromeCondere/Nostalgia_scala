import org.scalatest._
import agent._
import akka.actor._
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Future
import agent.port.PortDevice
import agent.port.SimpleDevice
import agent.port.Out
import agent.port.IsOut
import agent.port.IsIn
import agent.port.In
import agent.port.PortMessage

class PortDeviceSpec extends TestKit(ActorSystem("PortDeviceSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "a PortDevice" must {
    class myPortDeviceAgent extends NostalgiaAgent with Simple with PortDevice with SimpleDevice {}
    object myPortDeviceAgent {
      def apply() = new myPortDeviceAgent()
    }

    "correctly add a in/out connection" in {
      
      val portAgent1 = TestActorRef (myPortDeviceAgent(), "portAgent1")
      val portAgent2 = TestActorRef (myPortDeviceAgent(), "portAgent2")
      val probe = TestProbe()

      probe.send(portAgent2, In(portAgent1, "inA2", "outA1")) //(portAgent2, inA2) << (portAgent1, outA1)
      probe.send(portAgent1, Out(portAgent2, "outA1", "inA2"))//(portAgent1, outA1) >> (portAgent2, inA2)
      
       
      probe.send(portAgent2, IsIn(In(portAgent1, "inA2", "outA1")) )
      probe.expectMsg(true)
      probe.send(portAgent2, IsIn(In(portAgent1, "inA7", "outA1")) )
      probe.expectMsg(false)
      
      probe.send(portAgent1, IsOut(Out(portAgent2, "outA1", "inA2")) )
      probe.expectMsg(true)
      probe.send(portAgent1, IsOut(Out(portAgent2, "outA7", "inA2")) )
      probe.expectMsg(false)
    }


    "correctly add a in/out connection(with pattern)" in {
      import agent.port.Pattern.Connection
      
      val portAgent1 = TestActorRef (myPortDeviceAgent(), "portAgent11")
      val portAgent2 = TestActorRef (myPortDeviceAgent(), "portAgent22")
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
      val mainProbe = TestProbe()
      val probe1 = TestProbe()
      val probe2 = TestProbe()
      val myDeviceAgent1 = system.actorOf(Props(new MyDeviceAgent()), "myDeviceAgent1")
      val myDeviceAgent2 = system.actorOf(Props(new MyDeviceAgent()), "myDeviceAgent2")
      //  (myDeviceAgent1, "out1") --> (myDeviceAgent2, "in2")
      (myDeviceAgent1, "out1") >>+ (myDeviceAgent2, "in2")
      (myDeviceAgent2, "in2") <<+ (myDeviceAgent1, "out1")
      
      mainProbe.send(myDeviceAgent1, probe1.ref)
      mainProbe.send(myDeviceAgent2, probe2.ref)
      
      mainProbe.send(myDeviceAgent1, ("out1", "message to in2") )
      probe2.expectMsg(("in2", "message to in2"))
      
    }
  }
}