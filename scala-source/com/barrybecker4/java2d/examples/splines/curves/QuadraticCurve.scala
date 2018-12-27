package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{Line2D, Point2D, QuadCurve2D}

/**
  * A piece-wise quadradic curve. The first 3 points define the first quadratic segment.
  * Every 2 points after that define an additional segment joint with the one before it.
  * @param pts points that define the cubic curve
  */
class QuadraticCurve(pts: Array[Point2D]) extends Curve(pts) {

  assert(pts.length >= 3 && pts.length % 2 == 1)
  private val qCurves = Array.fill((pts.length - 1) / 2)(new QuadCurve2D.Float)

  def paint(g2: Graphics2D, selectedPoint: Option[Point2D]): Unit = {

    for (i <- qCurves.indices) {
      val qCurve = qCurves(i)
      val offset = i * 2
      qCurve.setCurve(pts, offset)
      val tangent1 = new Line2D.Double(pts(offset), pts(offset + 1))
      val tangent2 = new Line2D.Double(pts(offset + 1), pts(offset + 2))
      g2.setPaint(Color.gray)
      g2.draw(tangent1)
      g2.draw(tangent2)

      g2.setPaint(Color.black)
      g2.draw(qCurve)
      drawPoints(g2, selectedPoint)
    }
  }
}
