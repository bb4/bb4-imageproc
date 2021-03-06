package com.barrybecker4.java2d.examples.splines

import java.awt._
import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}
import java.awt.geom._
import com.barrybecker4.java2d.examples.splines.curves.{CubicCurve, LinearCurve, QuadraticCurve}
import com.barrybecker4.ui.application.ApplicationFrame


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object Splines extends App {
  new Splines
}

class Splines() extends ApplicationFrame("Splines v1.0")
  with MouseListener with MouseMotionListener {

  setSize(500, 400)

  val curves = Seq(
    new CubicCurve(Array(
      new Point2D.Double(50, 75), new Point2D.Double(100, 100),
      new Point2D.Double(200, 50), new Point2D.Double(250, 75),
      new Point2D.Double(300, 90),
      new Point2D.Double(350, 60), new Point2D.Double(390, 75))
    ),
    new QuadraticCurve(Array(
      new Point2D.Double(50, 175), new Point2D.Double(150, 150), new Point2D.Double(250, 175),
      new Point2D.Double(300, 185), new Point2D.Double(350, 165)
    )),
    new LinearCurve(Array(
      new Point2D.Double(50, 275), new Point2D.Double(250, 275), new Point2D.Double(320, 295)
    ))
  )
  repaint()

  private var selectedPoint: Option[Point2D] = None
  // Listen for mouse events.
  addMouseListener(this)
  addMouseMotionListener(this)


  override def paint(g: Graphics): Unit = {
    super.paint(g)
    if (curves != null) {
      val g2 = g.asInstanceOf[Graphics2D]
      curves.foreach(c => c.paint(g2, selectedPoint))
    }
  }


  override def mouseClicked(me: MouseEvent): Unit = {}

  override def mousePressed(me: MouseEvent): Unit = {
    selectedPoint = None
    val currentPt = me.getPoint

    for (c <- curves) {
      val selPt = c.contains(currentPt)
      if (selPt.isDefined) {
        selectedPoint = selPt
      }
    }
    repaint()
  }

  override def mouseReleased(me: MouseEvent): Unit = {}
  override def mouseMoved(me: MouseEvent): Unit = {}

  override def mouseDragged(me: MouseEvent): Unit = {
    if (selectedPoint.isDefined) {
      selectedPoint.get.setLocation(me.getPoint)
      repaint()
    }
  }

  override def mouseEntered(me: MouseEvent): Unit = {}
  override def mouseExited(me: MouseEvent): Unit = {}
}
