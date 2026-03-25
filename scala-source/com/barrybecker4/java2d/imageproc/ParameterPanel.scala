/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import com.barrybecker4.optimization.parameter.ParameterChangeListener
import com.barrybecker4.optimization.parameter.types.Parameter
import javax.swing.{BoxLayout, JPanel, JScrollPane}
import scala.collection.mutable.ArrayBuffer


/**
  * Auto create a panel of sliders and drop-downs (etc) for manipulating a set of parameters.
  * @param parameters optional initial parameters; use `None` when no controls are needed yet.
  * @author Barry Becker
  */
class ParameterPanel(parameters: Option[Seq[Parameter]] = None) extends JScrollPane with ParameterChangeListener {

  private var currentParameters: Seq[Parameter] = parameters.getOrElse(Seq.empty)

  private val changeListeners = ArrayBuffer[ParameterChangeListener]()
  private val viewPanel: JPanel = new JPanel
  viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS))
  if currentParameters.nonEmpty then initializeUI()
  this.setViewportView(viewPanel)

  def updateParameters(params: Seq[Parameter]): Unit = {
    assert(
      currentParameters.isEmpty || params.size == currentParameters.size,
      s"old param size = ${currentParameters.size} new param size = ${params.size}"
    )
    currentParameters = params
    initializeUI()
  }

  private def initializeUI(): Unit = {
    viewPanel.removeAll()
    for (param <- currentParameters) {
      viewPanel.add(param.createWidget(this))
    }
  }

  private[imageproc] def addParameterChangeListener(listener: ParameterChangeListener): Unit =
    changeListeners.append(listener)

  override def parameterChanged(param: Parameter): Unit = {
    changeListeners.foreach(_.parameterChanged(param))
  }
}
