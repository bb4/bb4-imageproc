package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{Line2D, Point2D, QuadCurve2D}

class QuadraticCurve(pts: Array[Point2D]) extends Curve(pts) {

  assert(pts.length >= 3)
  val qCurve = new QuadCurve2D.Float

  override protected def getShape: Shape = qCurve

  def paint(g2: Graphics2D, selectedPoint: Option[Point2D]): Unit = {

    qCurve.setCurve(pts, 0)
    val tangent1 = new Line2D.Double(pts(0), pts(1))
    val tangent2 = new Line2D.Double(pts(1), pts(2))
    g2.setPaint(Color.gray)
    g2.draw(tangent1)
    g2.draw(tangent2)

    drawCurve(g2, selectedPoint)
  }
}
