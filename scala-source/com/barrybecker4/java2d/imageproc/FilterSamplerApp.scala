/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import scala.compiletime.uninitialized

import java.awt.{BorderLayout, Button, Dimension, FileDialog, Font, Frame, GridLayout, Label, List, Panel}
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.image.BufferedImageOp
import java.awt.Checkbox
import com.barrybecker4.java2d.Utilities
import com.barrybecker4.java2d.ui.SplitImageComponent
import com.barrybecker4.optimization.parameter.ParameterChangeListener
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.ui.application.ApplicationFrame


/**
  * Allows you to test filters and modify their parameters on the fly.
  * Based on the Sampler program that comes with Java2D by Knudsen.
  */
object FilterSamplerApp:
  def main(args: Array[String]): Unit =
    val imageFile =
      if args.nonEmpty then args(0)
      else Utilities.DEFAULT_IMAGE_DIR + "EtholWithRoses.small.jpg"
    new FilterSamplerApp(imageFile)

class FilterSamplerApp(val imageFile: String) extends ApplicationFrame("Filter Sampler")
  with ItemListener with ActionListener with ParameterChangeListener:

  private var imageFrame: Frame = uninitialized
  private var splitImageComponent: SplitImageComponent = uninitialized
  private var accumulateCheckbox: Checkbox = uninitialized
  private var statusLabel = new Label("")
  private var paramPanel: ParameterPanel = uninitialized
  private var filterList: java.awt.List = uninitialized
  private val operations = new ProcessingOperators()
  createImageFrame(imageFile)
  initializeUI()

  private def selectedOperationKey: Option[String] =
    Option(filterList.getSelectedItem).collect { case s: String => s }

  private def withUiLocked(body: => Unit): Unit =
    filterList.setEnabled(false)
    accumulateCheckbox.setEnabled(false)
    try body
    finally
      filterList.setEnabled(true)
      accumulateCheckbox.setEnabled(true)

  private def createImageFrame(imageFile: String): Unit = {
    splitImageComponent = new SplitImageComponent(imageFile)
    splitImageComponent.setPreferredSize(new Dimension(600, 700))
    splitImageComponent.setSplitX(40)
    imageFrame = new Frame(imageFile)
    imageFrame.setLayout(new BorderLayout)
    imageFrame.add(splitImageComponent, BorderLayout.CENTER)
    Utilities.sizeContainerToComponent(imageFrame, splitImageComponent)
    Utilities.centerFrame(imageFrame)
    imageFrame.setVisible(true)
  }

  override def createUI(): Unit = {
    super.createUI()
    setFont(new Font("Serif", Font.PLAIN, 12))
    setLayout(new BorderLayout)
    this.setMinimumSize(new Dimension(300, 500))
    accumulateCheckbox = new Checkbox("Accumulate", false)
    statusLabel = new Label("")
  }

  protected def initializeUI(): Unit = {
    val pt = imageFrame.getLocation
    setLocation(pt.x - getSize.width, pt.y)
    filterList = new List()
    for (item <- operations.getSortedKeys)
      filterList.add(item)
    filterList.addItemListener(this)
    val loadButton = new Button("Load...")
    loadButton.addActionListener(this)
    val bottom = new Panel(new GridLayout(2, 1))
    val topBottom = new Panel()
    topBottom.add(accumulateCheckbox)
    topBottom.add(loadButton)
    bottom.add(topBottom)
    bottom.add(statusLabel)
    paramPanel = new ParameterPanel(None)
    add(paramPanel, BorderLayout.CENTER)
    add(filterList, BorderLayout.WEST)
    add(bottom, BorderLayout.SOUTH)
    this.pack()
  }

  override def itemStateChanged(ie: ItemEvent): Unit = {
    if (ie.getStateChange != ItemEvent.SELECTED) return
    selectedOperationKey.foreach { key =>
      val metaOp = operations.getOperation(key)
      val op = metaOp.getInstance
      val previous = if (accumulateCheckbox.getState) imageFrame.getTitle + " + " else ""
      imageFrame.setTitle(previous + key)
      statusLabel.setText("Performing " + key + "...")
      withUiLocked:
        applyImageOperator(op)
      statusLabel.setText("Performing " + key + "...done.")
      replaceParameterUI(metaOp)
    }
  }

  private def replaceParameterUI(metaOp: MetaImageOp): Unit = {
    this.remove(paramPanel)
    paramPanel = new ParameterPanel(Some(metaOp.getBaseParameters))
    paramPanel.addParameterChangeListener(this)
    this.add(paramPanel, BorderLayout.CENTER)
    this.pack()
  }

  /** Called whenever one of the UI parameter widgets was changed by the user.
    * @param param changed parameter
    */
  override def parameterChanged(param: Parameter): Unit = {
    selectedOperationKey.foreach { key =>
      val metaOp = operations.getOperation(key)
      metaOp.updateParameter(param)
      val op = metaOp.getInstance
      applyImageOperator(op)
    }
  }

  private def applyImageOperator(op: BufferedImageOp): Unit = {
    val second = splitImageComponent.getSecondImage
    val source =
      if (second == null || !accumulateCheckbox.getState) splitImageComponent.getImage
      else second
    val destination = op.filter(source, null)
    splitImageComponent.setSecondImage(destination)
    splitImageComponent.setSize(splitImageComponent.getPreferredSize)
    imageFrame.setSize(imageFrame.getPreferredSize)
  }

  /** Called when the load button is pressed.
    * @param ae action event
    */
  override def actionPerformed(ae: ActionEvent): Unit = {
    val fd = new FileDialog(this.imageFrame)
    fd.setVisible(true)
    if (fd.getFile == null) return
    val path = fd.getDirectory + fd.getFile
    splitImageComponent.setImage(path)
    splitImageComponent.setSecondImage(null)
    Utilities.sizeContainerToComponent(imageFrame, splitImageComponent)
    imageFrame.validate()
    imageFrame.repaint()
  }
