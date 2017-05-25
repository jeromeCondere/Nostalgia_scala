package agent.simulation.graphical.netlogo
import agent.simulation.graphical._

class NetlogoModel(params: GraphicalParam, val path: String, x: Any*) extends GraphicalModel(params, path, x) {
  
}
object NetlogoModel{
  def apply(params: GraphicalParam, path: String, x:Any*) = new NetlogoModel(params, path, x)
}