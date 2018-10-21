package com.barrybecker4.java2d.examples

import com.barrybecker4.java2d.Utilities
import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.geom.Rectangle2D
import java.awt.geom.RoundRectangle2D
import java.io.IOException


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object TexturePaintFill extends App {
  val f = new TexturePaintFill("roa2.jpg")
  f.setTitle("TexturePaintFill v1.0")
  f.setSize(200, 200)
}

class TexturePaintFill @throws[IOException]
(val filename: String) extends ApplicationFrame {
  val img: Image = Utilities.blockingLoad(getClass.getResource(Utilities.DEFAULT_IMAGE_DIR + filename))
  private val mImage = Utilities.makeBufferedImage(img)

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    val d: Dimension = getSize
    val r = new RoundRectangle2D.Float(25, 35, d.width - 50, d.height - 50, 55, 55)
    if (mImage != null) {
      // Create a texture rectangle with the same size as the texture image.
      val tr = new Rectangle2D.Double(0, 0, mImage.getWidth, mImage.getHeight)
      val tp = new TexturePaint(mImage, tr)
      g2.setPaint(tp)
      g2.fill(r)
    } else println("still creating image...")
  }
}