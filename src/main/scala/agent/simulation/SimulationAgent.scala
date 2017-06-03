package agent.simulation
import agent.NostalgiaAgent

abstract class SimulationAgent[+A <: Model](model: A) extends NostalgiaAgent  {
 
  def run
  
}