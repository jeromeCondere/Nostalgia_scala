package agent.simulation
import agent.simulation.Model
import agent.NostalgiaAgent

abstract class SimulationAgent[+A <: Model](model: A) extends NostalgiaAgent  {
 
  def run
  
}