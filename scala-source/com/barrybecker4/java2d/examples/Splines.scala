package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.geom.CubicCurve2D
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.geom.QuadCurve2D
import java.awt.geom.Rectangle2D


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object Splines extends App {
  new Splines
}

class Splines() extends ApplicationFrame("Splines v1.0") with MouseListener with MouseMotionListener {
  setSize(300, 300)
  //center();
  protected var mPoints: Array[Point2D]  = new Array[Point2D](9)
  // Cubic curve.
  mPoints(0) = new Point2D.Double(50, 75)
  mPoints(1) = new Point2D.Double(100, 100)
  mPoints(2) = new Point2D.Double(200, 50)
  mPoints(3) = new Point2D.Double(250, 75)
  // Quad curve.
  mPoints(4) = new Point2D.Double(50, 175)
  mPoints(5) = new Point2D.Double(150, 150)
  mPoints(6) = new Point2D.Double(250, 175)
  // Line.
  mPoints(7) = new Point2D.Double(50, 275)
  mPoints(8) = new Point2D.Double(250, 275)
  protected var mSelectedPoint: Point2D = _
  // Listen for mouse events.
  addMouseListener(this)
  addMouseMotionListener(this)

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    // Draw the tangents.
    var tangent1 = new Line2D.Double(mPoints(0), mPoints(1))
    var tangent2 = new Line2D.Double(mPoints(2), mPoints(3))
    g2.setPaint(Color.gray)
    g2.draw(tangent1)
    g2.draw(tangent2)
    // Draw the cubic curve.
    val c = new CubicCurve2D.Float
    c.setCurve(mPoints, 0)
    g2.setPaint(Color.black)
    g2.draw(c)
    tangent1 = new Line2D.Double(mPoints(4), mPoints(5))
    tangent2 = new Line2D.Double(mPoints(5), mPoints(6))
    g2.setPaint(Color.gray)
    g2.draw(tangent1)
    g2.draw(tangent2)
    // Draw the quadratic curve.
    val q = new QuadCurve2D.Float
    q.setCurve(mPoints, 4)
    g2.setPaint(Color.black)
    g2.draw(q)
    // Draw the line.
    val l = new Line2D.Float
    l.setLine(mPoints(7), mPoints(8))
    g2.setPaint(Color.black)
    g2.draw(l)
    var i = 0
    while (i < mPoints.length) { // If the point is selected, use the selected color.
      if (mPoints(i) eq mSelectedPoint) g2.setPaint(Color.red)
      else g2.setPaint(Color.blue)
      // Draw the point.
      g2.fill(getControlPoint(mPoints(i)))
      i += 1
    }
  }

  protected def getControlPoint(p: Point2D): Shape = { // Create a small square around the given point.
    val side = 4
    new Rectangle2D.Double(p.getX - side / 2, p.getY - side / 2, side, side)
  }

  override def mouseClicked(me: MouseEvent): Unit = {}

  override def mousePressed(me: MouseEvent): Unit = {
    mSelectedPoint = null
    var i = 0
    while (i < mPoints.length) {
      val s = getControlPoint(mPoints(i))
      if (s.contains(me.getPoint)) {
        mSelectedPoint = mPoints(i)
      }
      i += 1
    }
    repaint()
  }

  override def mouseReleased(me: MouseEvent): Unit = {}
  override def mouseMoved(me: MouseEvent): Unit = {}

  override def mouseDragged(me: MouseEvent): Unit = {
    if (mSelectedPoint != null) {
      mSelectedPoint.setLocation(me.getPoint)
      repaint()
    }
  }

  override def mouseEntered(me: MouseEvent): Unit = {}
  override def mouseExited(me: MouseEvent): Unit = {}
}