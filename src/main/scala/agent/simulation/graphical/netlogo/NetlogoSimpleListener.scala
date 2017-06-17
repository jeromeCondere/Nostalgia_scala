package agent.simulation.graphical.netlogo
import org.nlogo.api.NetLogoListener
import org.nlogo.core.CompilerException

class NetlogoSimpleListener extends NetLogoListener {
      final def U = {}
      def buttonPressed(buttonName: String) = U
      def buttonStopped(buttonName: String) = U
      def chooserChanged(name: String, value: AnyRef, valueChanged: Boolean) = U
      def codeTabCompiled(text: String, errorMsg: CompilerException) = U
      def commandEntered(owner: String, text: String, agentType: Char, errorMsg: CompilerException) = U
      def inputBoxChanged(name: String, value: AnyRef, valueChanged: Boolean) = U
      def modelOpened(name: String) = U
      def possibleViewUpdate() = U
      def switchChanged(name: String, value: Boolean, valueChanged: Boolean) = U
      def tickCounterChanged(ticks: Double) = U
      def sliderChanged(name: String, value: Double, min: Double, increment: Double, max: Double, valueChanged: Boolean, buttonReleased: Boolean) = U
}