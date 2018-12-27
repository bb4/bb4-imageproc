package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{CubicCurve2D, Line2D, Point2D, QuadCurve2D}

/**
  * A piece-wise cubic curve. The first 3 points define the first cubic segment.
  * Every 3 points after that define an additional segment joint with the one before it.
  */
class CubicCurve(pts: Array[Point2D]) extends Curve(pts) {

  assert(pts.length >= 4 && pts.length % 3 == 1)
  private val cubicCurves = Array.fill((pts.length - 1) / 3)(new CubicCurve2D.Float)

  def paint(g2: Graphics2D, selectedPoint: Option[Point2D]): Unit = {

    for (i <- cubicCurves.indices) {
      val cCurve = cubicCurves(i)
      val offset = i * 3
      cCurve.setCurve(pts, offset)
      val tangent1 = new Line2D.Double(pts(offset), pts(offset + 1))
      val tangent2 = new Line2D.Double(pts(offset + 2), pts(offset + 3))
      g2.setPaint(Color.gray)
      g2.draw(tangent1)
      g2.draw(tangent2)

      g2.setPaint(Color.black)
      g2.draw(cCurve)
      drawPoints(g2, selectedPoint)
    }
  }
}
