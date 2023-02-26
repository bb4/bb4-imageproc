/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import java.awt.{BorderLayout, Button, Dimension, FileDialog, Font, Frame, GridLayout, Label, List, Panel, Point}
import java.awt.event.{ActionEvent, ActionListener, ItemEvent, ItemListener}
import java.awt.image.BufferedImageOp
import java.awt.Checkbox
import com.barrybecker4.java2d.Utilities
import com.barrybecker4.java2d.ui.SplitImageComponent
import com.barrybecker4.optimization.parameter.ParameterChangeListener
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.ui.application.ApplicationFrame


/*
@main def main(filename: String) =
  var imageFile = Utilities.DEFAULT_IMAGE_DIR + "EtholWithRoses.small.jpg"
  if (filename != null) imageFile = filename
  new FilterSamplerApp(imageFile)
*/
/**
  * Allows you to test filters and modify their parameters on the fly.
  * Based on the Sampler program that comes with Java2D by Knudsen.
  */
object FilterSamplerApp extends App {
  var imageFile = Utilities.DEFAULT_IMAGE_DIR + "EtholWithRoses.small.jpg"
  //if (args.length > 0) imageFile = args(0)
  new FilterSamplerApp(imageFile)
}

class FilterSamplerApp(val imageFile: String) extends ApplicationFrame("Filter Sampler")
  with ItemListener with ActionListener with ParameterChangeListener {

  private var imageFrame: Frame = _
  private var splitImageComponent: SplitImageComponent = _
  private var accumulateCheckbox: Checkbox = _
  private var statusLabel = new Label("")
  private var paramPanel: ParameterPanel = _
  private var filterList: java.awt.List = _
  private var operations = new ProcessingOperators()
  createImageFrame(imageFile)
  initializeUI()

  /** The image to be manipulated goes in a separate frame.
    * @param imageFile image to load
    */
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
    // Set our location to the left of the image frame.
    this.setMinimumSize(new Dimension(300, 500))
    accumulateCheckbox = new Checkbox("Accumulate", false)
    statusLabel = new Label("")
  }

  protected def initializeUI(): Unit = {
    val pt = imageFrame.getLocation
    setLocation(pt.x - getSize.width, pt.y)
    filterList = operations.getSortedKeys
    // When an item is selected, do the corresponding transformation.
    filterList.addItemListener(this)
    val loadButton = new Button("Load...")
    loadButton.addActionListener(this)
    val bottom = new Panel(new GridLayout(2, 1))
    val topBottom = new Panel()
    topBottom.add(accumulateCheckbox)
    topBottom.add(loadButton)
    bottom.add(topBottom)
    bottom.add(statusLabel)
    // add placeholder param panel
    paramPanel = new ParameterPanel(null)
    add(paramPanel, BorderLayout.CENTER)
    add(filterList, BorderLayout.WEST)
    add(bottom, BorderLayout.SOUTH)
    this.pack()
  }

  /**
    * Called when an item in the list of transformations is called.
    *
    * @param ie item event
    */
  override def itemStateChanged(ie: ItemEvent): Unit = {
    if (ie.getStateChange != ItemEvent.SELECTED) return
    val key = filterList.getSelectedItem
    val metaOp = operations.getOperation(key)
    val op = metaOp.getInstance
    var previous = imageFrame.getTitle + " + "
    if (!accumulateCheckbox.getState) previous = ""
    imageFrame.setTitle(previous + key)
    statusLabel.setText("Performing " + key + "...")
    // don't allow doing anything while processing
    filterList.setEnabled(false)
    accumulateCheckbox.setEnabled(false)
    applyImageOperator(op)
    filterList.setEnabled(true)
    accumulateCheckbox.setEnabled(true)
    statusLabel.setText("Performing " + key + "...done.")
    replaceParameterUI(metaOp)
  }

  private def replaceParameterUI(metaOp: MetaImageOp): Unit = { // now show ui for modifying the parameters for this op
    this.remove(paramPanel)
    paramPanel = new ParameterPanel(metaOp.getBaseParameters)
    // We will get called whenever a parameter is tweaked
    paramPanel.addParameterChangeListener(this)
    this.add(paramPanel, BorderLayout.CENTER)
    this.pack()
  }

  /** Called whenever one of the UI parameter widgets was changed by the user.
    * @param param changed parameter
    */
  override def parameterChanged(param: Parameter): Unit = {
    // we could use param.getName() to get the filter, but its just the currently selected one.
    val key = filterList.getSelectedItem
    val metaOp = operations.getOperation(key)
    metaOp.updateParameter(param)

    val op = metaOp.getInstance
    applyImageOperator(op)
  }

  private def applyImageOperator(op: BufferedImageOp): Unit = {
    var source = splitImageComponent.getSecondImage
    if (source == null || !accumulateCheckbox.getState) source = splitImageComponent.getImage
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
}