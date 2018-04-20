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
import scala.concurrent.Future


class BehaviorAgentSpec extends TestKit(ActorSystem("BehaviorAgentSpec")) with ImplicitSender
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
}