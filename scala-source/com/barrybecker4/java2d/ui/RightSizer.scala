package com.barrybecker4.java2d.ui

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.image.ImageObserver
import java.net.URL


/**
  * Shows an image in a frame of the same size.
  */
object RightSizer {
  val DEFAULT_IMAGE_URL = "http://barrybecker4.com/familyPictures/family_portrait_s.JPG"

  @throws[Exception]
  def main(args: Array[String]): Unit = {
    var url = DEFAULT_IMAGE_URL
    if (args.length > 0) url = args(0)
    new RightSizer(new URL(url))
  }
}

class RightSizer(val url: URL) extends ApplicationFrame("RightSizer v1.0") {

  private var image  = Toolkit.getDefaultToolkit.getImage(url)
  rightSize()

  /** Set the frame size to the same size as the image (once it has loaded). */
  private def rightSize(): Unit = {
    val width = image.getWidth(this)
    val height = image.getHeight(this)
    if (width == -1 || height == -1) return
    addNotify()   // needed?
    val insets = getInsets
    setSize(width + insets.left + insets.right, height + insets.top + insets.bottom)
    center()
    setVisible(true)
  }

  override def imageUpdate(img: Image, infoflags: Int, x: Int, y: Int, width: Int, height: Int): Boolean = {
    if ((infoflags & ImageObserver.ERROR) != 0) {
      println("Error loading image!")
      System.exit(-1)
    }
    if ((infoflags & ImageObserver.WIDTH) != 0 && (infoflags & ImageObserver.HEIGHT) != 0) rightSize()
    if ((infoflags & ImageObserver.SOMEBITS) != 0) repaint()
    if ((infoflags & ImageObserver.ALLBITS) != 0) {
      rightSize()
      repaint()
      return false
    }
    true
  }

  override def update(g: Graphics): Unit = paint(g)

  override def paint(g: Graphics): Unit = {
    val insets = getInsets
    g.drawImage(image, insets.left, insets.top, this)
  }
}