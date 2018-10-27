package com.barrybecker4.java2d.print

import java.awt.{Component, Graphics, Graphics2D}
import java.awt.print.{PageFormat, Printable}
import javax.swing.JComponent


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
class ComponentPrintable(var mComponent: Component) extends Printable {
  override def print(g: Graphics, pageFormat: PageFormat, pageIndex: Int): Int = {
    if (pageIndex > 0) return Printable.NO_SUCH_PAGE
    val g2 = g.asInstanceOf[Graphics2D]
    g2.translate(pageFormat.getImageableX, pageFormat.getImageableY)
    val wasBuffered = disableDoubleBuffering(mComponent)
    mComponent.paint(g2)
    restoreDoubleBuffering(mComponent, wasBuffered)
    Printable.PAGE_EXISTS
  }

  private def disableDoubleBuffering(c: Component): Boolean = {
    if (!c.isInstanceOf[JComponent]) return false
    val jc = c.asInstanceOf[JComponent]
    val wasBuffered = jc.isDoubleBuffered
    jc.setDoubleBuffered(false)
    wasBuffered
  }

  private def restoreDoubleBuffering(c: Component, wasBuffered: Boolean): Unit = {
    c match {
      case component: JComponent => component.setDoubleBuffered(wasBuffered)
      case _ =>
    }
  }
}