package agent.simulation.graphical.netlogo.util
import java.awt.Frame
import java.awt.EventQueue.isDispatchThread
import java.awt.image.BufferedImage
import java.net.URI
import java.nio.file.Paths

import org.nlogo.api.{ ModelType, Version}
import org.nlogo.window.{ Event, FileController, InvalidVersionException,
  ReconfigureWorkspaceUI }
import org.nlogo.workspace.OpenModelFromSource
import org.nlogo.fileformat

import scala.concurrent.{ Future, Promise }
import scala.util.Try

class InterfaceComponent(frame: Frame) extends org.nlogo.lite.InterfaceComponent(frame) {
  /**
	Open model from string
  */
  @throws(classOf[InvalidVersionException])
  def openFromSource(name: String, source: String) {
    iP.reset()


    val uri =  Paths.get("file.nlogo").toUri
    val controller = new FileController(this, workspace)
    val converter = fileformat.converter(workspace.getExtensionManager, workspace.getCompilationEnvironment,
      workspace, fileformat.defaultAutoConvertables) _
    val loader = fileformat.standardLoader(workspace.compiler.utilities)
    val modelOpt = OpenModelFromSource(uri, source, controller, loader, converter(workspace.world.program.dialect), Version)
	modelOpt.foreach(model => ReconfigureWorkspaceUI(this, uri, ModelType.Library, model, workspace))
  }

}
