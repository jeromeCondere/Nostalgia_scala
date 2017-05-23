import org.scalatest._
import behavior._
import behavior.proxy.BehaviorProxy
import akka.actor._
import akka.testkit._
import scala.concurrent.duration._

class BehaviorSpec extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
 
// the supervisor is the implicit sender   
implicit val systemSupervisor = self

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A behavior (general)" must {
  
    "run the code properly" in {
      var a = 2
      val beRef = system.actorOf(Props(OneShotBehavior{
        a = a + 2
      } ), "runBehavior")
      assert(a==2)
      
      beRef ! Setup()
      beRef ! Run
      awaitCond(a == 4, 70 millis)
      expectMsg(Finished)
     }
     
   "init properly" in {
      class MyBehavior(toRun:() =>Unit) extends OneShotBehavior(toRun)
      {
        
        var a =2
        override protected def init = a+=2
      }
      val beRef = TestActorRef (new MyBehavior(doNothing()), "initBehavior" )
      val be = beRef.underlyingActor
      beRef ! Setup()
      assert(be.a==4)
      awaitCond(be.a == 4, 70 millis)
      expectNoMsg(150 millis)
  }
   
   "init only once" in {
     
      class MyBehavior(toRun:() =>Unit) extends OneShotBehavior(toRun)
      {
        var b =2
        override protected def init = b+=4
      }

      var beRef = TestActorRef( new MyBehavior(()=>{}) , "initBehavior2")
      val be = beRef.underlyingActor
      assert(be.b==2)
      beRef ! Setup()
      awaitCond(be.b == 6, 50 millis)
      beRef ! Setup() //setting up twice doesn't change the value
      awaitCond(be.b == 6, 100 millis)
    }
}

"A TimerBehavior" must {
  
  "execute after a determinded duration" in {
    var a =2
    var beRef = TestActorRef(TimerBehavior(100 millis){
      a+=3
    })
    
    beRef ! Setup()
    beRef ! Run
    awaitCond(a==5, 190 millis) // we give +90 millis to check if a has been updated
    expectMsg(Finished)
  }
}

"A TickerBehavior" must {
  "exec repeatedly" in {
    var e = 0
    var beRef = TestActorRef(TickerBehavior(50 millis){
      e+=2
    }, "tickerBehavior")
    beRef ! Setup()
    beRef ! Run
    // we test every 40 millis if the condition holds
    awaitCond(e==20, 1 seconds, 40 millis)
    beRef ! Stop
    expectMsg(Dead)
  }
   "send a Dead message after death to supervisor"  in {
     var beRef = TestActorRef(TickerBehavior(50 millis){
      
    },"deadTickerBehavior")
      beRef ! Setup()
      beRef ! Run
      beRef ! Stop
      expectMsg(Dead)
  }
  "send a finished message" in {
    var b =0
    class MyTickerBehavior(period:FiniteDuration)(toRun:() =>Unit) extends TickerBehavior(period:FiniteDuration)(toRun)
      {
        override protected def stopTicker: Boolean = {
          b == 6
        }
      }
   var beRef =  TestActorRef(new MyTickerBehavior(20 millis)(()=>{
     b+=2
   }) ,"finishedTickerBehavior");
   
   beRef ! Setup()
   beRef ! Run
   
   awaitCond(b==6, 1 seconds, 40 millis)
   expectMsg(Finished)
  }
}

"A ParallelBehavior" must {
  
  "init all behaviors correctly" in {
    var a1 = 4
    var a2 = 6
    class MyBehavior(toRun:() =>Unit) extends OneShotBehavior(toRun)
    {
      override protected def init = a1+=2
       
    }
    class MyBehaviorBis(toRun:() =>Unit) extends OneShotBehavior(toRun)
    {
      override protected def init = a2+=3
    }
  
    val bp1 = BehaviorProxy{new MyBehavior(doNothing())}
    val bp2 = BehaviorProxy{new MyBehaviorBis(doNothing())}
    val listBp = List(bp1,bp2)
    var beRef = TestActorRef(new ParralelBehavior(listBp),"parrallelBehavior")
    
    beRef ! Setup()
    awaitCond(a1==6 && a2==9, 2 seconds)
  }
  
  "launch several behaviors asynchronously" in {
    var a1 = 7
    var a2 = 9
    val bp1 = BehaviorProxy{OneShotBehavior{
      a1+=3
      a2+=1    
    }}

    val bp2 = BehaviorProxy{OneShotBehavior{
      a1+=7
      a2+=4    
    }}
    val listBp = List(bp1,bp2)
    var beRef = TestActorRef(ParralelBehavior(listBp),"parrallelBehavior2")
    
    beRef ! Setup()
    beRef ! Run
    awaitCond(a1==17 && a2==14, 2 seconds)
    expectMsg(Finished)
  }
  
  "receive a Finished message from every agent stopped"  in {
    
    val bp1 = BehaviorProxy{OneShotBehavior{
      var a = 1  
      for ( i<-1 to 100)
            a+=45
    }}

    val bp2 = BehaviorProxy{OneShotBehavior{
      var a = 1 
    }}
    val listBp = List(bp1,bp2)
    var beRef = TestActorRef(ParralelBehavior(listBp),"parrallelBehavior3")
    
    beRef ! Setup()
    beRef ! Run
    
    expectMsg(Finished)
    expectNoMsg(100 millis)
  }  
}

}