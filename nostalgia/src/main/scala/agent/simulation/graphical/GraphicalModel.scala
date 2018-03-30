package agent.simulation.graphical
import agent.simulation.Model

// TODO: verify if pos is (Float, Float)
case class GraphicalParam(pos:(Int, Int), dim:(Int, Int) = (0,0))

class GraphicalModel(val params:GraphicalParam, x:Any*) extends Model(x) {
  
}

object GraphicalModel {
  def apply(params:GraphicalParam, x:Any*) = new GraphicalModel(params, x)
}