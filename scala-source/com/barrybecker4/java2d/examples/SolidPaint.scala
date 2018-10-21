package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.geom.Arc2D


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object SolidPaint extends App {
  val f = new SolidPaint
  f.setTitle("SolidPaint v1.0")
  f.setSize(200, 200)
}

class SolidPaint extends ApplicationFrame {
  override def paint(g: Graphics): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    val pie = new Arc2D.Float(0, 50, 150, 150, -30, 90, Arc2D.PIE)
    g2.setPaint(Color.blue)
    g2.fill(pie)
  }
}