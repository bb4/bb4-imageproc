package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object SmoothMove extends App {
    new SmoothMove
}

class SmoothMove() extends ApplicationFrame("SmoothMove v1.0") with MouseMotionListener {
  addMouseMotionListener(this)
  setVisible(true)
  private var mX = 0
  private var mY = 0
  private var mImage: Image = _

  override def mouseMoved(me: MouseEvent): Unit = {
    mX = me.getPoint.getX.toInt
    mY = me.getPoint.getY.toInt
    repaint()
  }

  override def mouseDragged(me: MouseEvent): Unit = {
    mouseMoved(me)
  }

  override def update(g: Graphics): Unit = {
    paint(g)
  }

  override def paint(g: Graphics): Unit = { // Clear the offscreen image.
    val d = getSize
    checkOffscreenImage()
    val offG = mImage.getGraphics
    offG.setColor(getBackground)
    offG.fillRect(0, 0, d.width, d.height)
    // Draw into the offscreen image.
    paintOffscreen(mImage.getGraphics)
    // Put the offscreen image on the screen.
    g.drawImage(mImage, 0, 0, null)
  }

  private def checkOffscreenImage(): Unit = {
    val d = getSize
    if (mImage == null || mImage.getWidth(null) != d.width || mImage.getHeight(null) != d.height)
      mImage = createImage(d.width, d.height)
  }

  def paintOffscreen(g: Graphics): Unit = {
    val s = 100
    g.setColor(Color.blue)
    g.fillRect(mX - s / 2, mY - s / 2, s, s)
  }
}