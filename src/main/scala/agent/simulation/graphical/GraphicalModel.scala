package agent.simulation.graphical
import agent.simulation.Model

case class GraphicalParam(pos:(Float, Float), dim:(Int, Int))

class GraphicalModel(val params:GraphicalParam, x:Any*) extends Model(x) {
  
}

object GraphicalModel {
  def apply(params:GraphicalParam, x:Any*) = new GraphicalModel(params, x)
}