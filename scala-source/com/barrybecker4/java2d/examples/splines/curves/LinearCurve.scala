package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{Line2D, Point2D}

/** A piece-wise linear curve composed of N line segments defined by pts */
class LinearCurve(pts: Array[Point2D]) extends Curve(pts) {

  assert(pts.length >= 2)

  override def paint(g2: Graphics2D, selectedPoint: Option[Point2D]): Unit = {
    g2.setPaint(Color.black)
    for (i <- 0 until pts.length - 1) {
      val pt1 = pts(i)
      val pt2 = pts(i + 1)
      g2.drawLine(pt1.getX.toInt, pt1.getY.toInt, pt2.getX.toInt, pt2.getY.toInt)
    }

    drawPoints(g2, selectedPoint)
  }
}
