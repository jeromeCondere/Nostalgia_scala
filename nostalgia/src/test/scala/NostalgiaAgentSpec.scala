import org.scalatest._
import agent._
import akka.actor._
import akka.testkit._
import scala.concurrent.duration._
import scala.concurrent.Future


class NostalgiaAgentSpec extends TestKit(ActorSystem("NostalgiaAgentSpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
  
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
 //  "A NostalgiaAgent" must {
 // }
}