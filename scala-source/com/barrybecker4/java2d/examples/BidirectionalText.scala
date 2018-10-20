package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object BidirectionalText {
  def main(args: Array[String]): Unit = {
    new BidirectionalText("BidirectionalText v1.0")
  }
}

class BidirectionalText(title: String) extends ApplicationFrame(title) {
  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    val font = new Font("Lucida Sans Regular", Font.PLAIN, 32)
    g2.setFont(font)
    g2.drawString("Please \u062e\u0644\u0639 slowly.", 40, 80)
  }
}