package netlogo.slider
import akka.actor._
import agent.simulation.graphical.netlogo._
import agent.simulation.graphical.netlogo.component._
import agent.simulation.graphical._
import agent._
/*
 * A simple slider example:
 * Every time the slider is moved the value is printed
 */
class mySliderAgent(sliderModel: SliderModel) extends NetlogoSliderAgent(sliderModel)()() with Simple {
  override def sliderHandle(value: Double, min: Double, increment: Double, max: Double, buttonReleased: Boolean) = {
    println("value: "+value)
  }
  override def setup = {}
}

object SliderNetlogo extends App {
  val graphicalParams = GraphicalParam((0,0))
  val sliderModel = SliderModel(graphicalParams, "my_slider")
  
  val system = ActorSystem("mySystem")
  val myNetlogo = system.actorOf(Props(new mySliderAgent(sliderModel)), "mySliderAgent")
 
  myNetlogo ! Run
}