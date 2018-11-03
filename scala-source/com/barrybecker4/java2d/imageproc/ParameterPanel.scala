/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import com.barrybecker4.optimization.parameter.ParameterChangeListener
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.optimization.parameter.ui.ParameterWidget
import javax.swing.{BoxLayout, JPanel, JScrollPane}
import scala.collection.mutable.ArrayBuffer


/**
  * Auto create a panel of sliders and drop-downs (etc) for manipulating a set of parameters.
  * @author Barry Becker
  */
class ParameterPanel(var parameters: Seq[Parameter]) extends JScrollPane with ParameterChangeListener {

  /** called when a parameter changes */
  private val changeListeners = ArrayBuffer[ParameterChangeListener]()
  private val viewPanel: JPanel = new JPanel
  viewPanel.setLayout(new BoxLayout(viewPanel, BoxLayout.Y_AXIS))
  if (parameters != null) initializeUI()
  this.setViewportView(viewPanel)

  /** @param params set of parameters that match the number and type of the original */
  def updateParameters(params: Seq[Parameter]): Unit = {
    if (params == null) return
    assert(parameters == null || params.size == parameters.size,
      "old param size = " + parameters.size + " new param size = " + params.size)
    this.parameters = params
    this.initializeUI()
  }

  /** Add a unique UI element for manipulating each individual parameter.  */
  private def initializeUI(): Unit = {
    viewPanel.removeAll()
    for (param <- parameters) {
      viewPanel.add(param.createWidget(this))
    }
  }

  private[imageproc] def addParameterChangeListener(listener: ParameterChangeListener): Unit =
    changeListeners.append(listener)

  /** Call parameterChange listeners if a parameter actually changed.
    * @param param the swing component that was activated.
    */
  override def parameterChanged(param: Parameter): Unit = {
    for (listener <- changeListeners) {
      listener.parameterChanged(param)
    }
  }
}