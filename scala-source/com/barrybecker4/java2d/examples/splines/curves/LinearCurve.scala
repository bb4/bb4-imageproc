package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{Line2D, Point2D}

class LinearCurve(pts: Array[Point2D]) extends Curve(pts) {

  val line = new Line2D.Float

  override protected def getShape: Shape = line

  override def paint(g2: Graphics2D, selectedPoint: Point2D): Unit = {
    line.setLine(pts(0), pts(1))
    drawCurve(g2, selectedPoint)
  }
}
