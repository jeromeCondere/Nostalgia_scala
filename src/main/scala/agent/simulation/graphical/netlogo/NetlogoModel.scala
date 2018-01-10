package agent.simulation.graphical.netlogo
import agent.simulation.graphical._
/** Model he the agent rely on to run
 *  @constructor
 *  @param params graphical params ((xpos, ypos), (width, height))
 *  @param path path to .nlogo file
 **/
class NetlogoModel(params: GraphicalParam, val path: String, x: Any*) extends GraphicalModel(params, path, x) {
  
}
object NetlogoModel{
  def apply(params: GraphicalParam, path: String, x:Any*) = new NetlogoModel(params, path, x)
}