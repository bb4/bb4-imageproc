package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{CubicCurve2D, Line2D, Point2D, QuadCurve2D}

class CubicCurve(pts: Array[Point2D]) extends Curve(pts) {

  assert(pts.length >= 4)
  val cubicCurve = new CubicCurve2D.Float

  override protected def getShape: Shape = cubicCurve

  def paint(g2: Graphics2D, selectedPoint: Point2D): Unit = {

    cubicCurve.setCurve(pts, 0)
    val tangent1 = new Line2D.Double(pts(0), pts(1))
    val tangent2 = new Line2D.Double(pts(2), pts(3))
    g2.setPaint(Color.gray)
    g2.draw(tangent1)
    g2.draw(tangent2)

    drawCurve(g2, selectedPoint)
  }
}
