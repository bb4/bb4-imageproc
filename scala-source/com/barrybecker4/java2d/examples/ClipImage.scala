package com.barrybecker4.java2d.examples

import com.barrybecker4.java2d.Utilities
import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.io.IOException


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object ClipImage {
  @throws[Exception]
  def main(args: Array[String]): Unit = {
    val frame = new ClipImage("ClipImage v1.0")
    frame.setSize(400, 250)
  }
}

class ClipImage @throws[IOException]
(title: String) extends ApplicationFrame(title) {
  val filename: String = Utilities.DEFAULT_IMAGE_DIR + "roa2.jpg"
  val img: Image = Utilities.blockingLoad(getClass.getResource(filename))
  final private[examples] var image = Utilities.makeBufferedImage(img)
  private var mClippingShape: Shape = _

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    val d = getSize
    g2.rotate(-Math.PI / 12, d.width / 2, d.height / 2)
    g2.scale(3.0, 3.0)
    g2.clip(getClippingShape(g2))
    g2.drawImage(image, 0, 0, null)
  }

  private def getClippingShape(g2: Graphics2D): Shape = {
    if (mClippingShape != null)
      return mClippingShape
    val s = "bella"
    val font = new Font("Serif", Font.BOLD, 32)
    val frc = g2.getFontRenderContext
    val gv = font.createGlyphVector(frc, s)
    mClippingShape = gv.getOutline(10, 40)
    mClippingShape
  }
}