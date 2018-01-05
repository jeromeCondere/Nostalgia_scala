package agent.simulation
import agent.NostalgiaAgent
/**
 * An agent that runs a simulation
 */
abstract class SimulationAgent[+A <: Model](model: A) extends NostalgiaAgent  {
 
  def run
  
}