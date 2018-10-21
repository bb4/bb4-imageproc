package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object XorRectangles extends App {

  val f = new ApplicationFrame("XOR Rectangles") {

    override def paint(g: Graphics): Unit = {
      val g2 = g.asInstanceOf[Graphics2D]
      // Set XOR mode, using white as the XOR color.
      g2.setXORMode(Color.white)

      // Paint a red rectangle.
      val r = new Rectangle2D.Double(50, 50, 150, 100)
      g2.setPaint(Color.red)
      g2.fill(r)
      // Shift the coordinate space.
      g2.transform(AffineTransform.getTranslateInstance(25, 25))
      // Draw a blue rectangle.
      g2.setPaint(Color.blue)
      g2.fill(r)
    }
  }

  f.setSize(300, 200)
  f.center()
  f.setVisible(true)
  f.repaint()
}
