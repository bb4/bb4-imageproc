package com.barrybecker4.java2d.examples.splines.curves

import java.awt.{Color, Graphics2D, Shape}
import java.awt.geom.{Line2D, Point2D, Rectangle2D}


abstract class Curve(pts: Array[Point2D]) {

  /** @return a small square around the given point */
  protected def getControlPoint(p: Point2D): Shape = {
    val side = 4
    new Rectangle2D.Double(p.getX - side / 2, p.getY - side / 2, side, side)
  }

  def paint(g2: Graphics2D, selectedPoint: Option[Point2D]): Unit

  def contains(point: Point2D): Option[Point2D] = {
    val local = getControlPoint(point)
    for (p <- pts) {
      if (local.contains(p))
        return Some(p)
    }
    None
  }

  /** Draw all the points in the curve with the actively selected one highlighted */
  protected def drawPoints(g2: Graphics2D,
                           selectedPoint: Option[Point2D]): Unit = {
    var i = 0
    while (i < pts.length) { // If the point is selected, use the selected color.
      if (selectedPoint.isDefined && (pts(i) eq selectedPoint.get))
        g2.setPaint(Color.red)
      else g2.setPaint(Color.blue)
      g2.fill(getControlPoint(pts(i)))
      i += 1
    }
  }
}
