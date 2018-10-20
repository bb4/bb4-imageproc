package com.barrybecker4.java2d

import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.Frame
import java.awt.Graphics
import java.awt.Image
import java.awt.MediaTracker
import java.awt.Toolkit
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferedImage
import java.net.URL


object Utilities {
  private val sComponent = new Component() {}
  private val sTracker = new MediaTracker(sComponent)
  private var sID = 0
  val DEFAULT_IMAGE_DIR = "/com/barrybecker4/java2d/images/"

  /** @param image image to load
    * @return true when the image has been loaded.
    */
  def waitForImage(image: Image): Boolean = {
    var id = 0
    sComponent synchronized {
      sID += 1
      id = sID
    }
    sTracker.addImage(image, id)
    try sTracker.waitForID(id)
    catch {
      case ie: InterruptedException =>
        return false
    }
    !sTracker.isErrorID(id)
  }

  def blockingLoad(path: String): Image = {
    val image = Toolkit.getDefaultToolkit.getImage(path)
    if (!waitForImage(image)) return null
    image
  }

  def blockingLoad(url: URL): Image = {
    val image = Toolkit.getDefaultToolkit.getImage(url)
    if (!waitForImage(image)) return null
    image
  }

  def makeBufferedImage(image: Image): BufferedImage = {
    if (image == null) {
      println("Warning image is null")
      return null
    }
    makeBufferedImage(image, BufferedImage.TYPE_INT_ARGB)
  }

  def makeBufferedImage(image: Image, imageType: Int): BufferedImage = {
    if (!waitForImage(image)) return null
    val bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), imageType)
    val g2 = bufferedImage.createGraphics
    g2.drawImage(image, null, null)
    bufferedImage
  }

  def getBufferedImage(path: String): BufferedImage = {
    val image = Utilities.blockingLoad(path)
    Utilities.makeBufferedImage(image)
  }

  def getNonClearingFrame(name: String, c: Component): Frame = {
    val f = new Frame(name) {
      override def update(g: Graphics): Unit = { paint(g) }
    }
    sizeContainerToComponent(f, c)
    centerFrame(f)
    f.setLayout(new BorderLayout)
    f.add(c, BorderLayout.CENTER)
    f.addWindowListener(new WindowAdapter() {
      override def windowClosing(e: WindowEvent): Unit = { f.dispose() }
    })
    f
  }

  def sizeContainerToComponent(container: Container, component: Component): Unit = {
    if (!container.isDisplayable) container.addNotify()
    val insets = container.getInsets
    val size = component.getPreferredSize
    val width = insets.left + insets.right + size.width
    val height = insets.top + insets.bottom + size.height
    container.setSize(width, height)
  }

  def centerFrame(f: Frame): Unit = {
    val screen = Toolkit.getDefaultToolkit.getScreenSize
    val d = f.getSize
    val x = (screen.width - d.width) >> 1
    val y = (screen.height - d.height) >> 1
    f.setLocation(x, y)
  }
}