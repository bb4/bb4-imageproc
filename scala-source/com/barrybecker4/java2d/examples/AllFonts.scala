package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import javax.swing._
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import scala.collection.mutable.ArrayBuffer


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  * It's interesting to note that very few fonts support all unicode characters
  * (and if they do, they probably take up a lot of memory).
  * About 10% support japanese characters and about 10% support Vietnamese characters for example, but
  * not the same 10%. The fonts that I found that support German, Japanese, and Vietnamese characters are:
  * *Dialog
  * DialogInput
  * Serif      (fonts have a small line trailing from the edges of letters and symbols)
  * *SansSerif  (fonts without the small trailing lines)
  *
  * In my opinion, of these, SansSerif or Dialog look the best.
  */
object AllFonts {
  private val ROW_HEIGHT = 30
  private val FONT_SIZE = 20
  private val ENGLISH = "The quick brown fox jumped over the lazy dog."
  private val JAPANESE = "\u56f2\u7881\u30b2\u30fc\u30e0\u306e\u60c5\u5831"
  private val GERMAN = "ungefähr Ausführungen"
  private val VIETNAMESE = "m\u00F4 ph\u1ECFng"
  private val SPECIAL_CHARS = "\u56f2\u7881äü\u00F4\u1ECF"
  private val SEPARATOR = "  |  "
  private val MULTI_LINGUAL_STRING = Seq(ENGLISH, JAPANESE, GERMAN, VIETNAMESE).mkString(SEPARATOR)

  private def showAllFonts(): Unit = {
    val fonts = GraphicsEnvironment.getLocalGraphicsEnvironment.getAllFonts
    for (font <- fonts)
      println(font.getFontName + SEPARATOR + font.getFamily + SEPARATOR + font.getName)
  }

  def main(args: Array[String]): Unit = {
    showAllFonts()
    val f = new AllFonts.FontsFrame
    f.setSize(new Dimension(1200, 800))
    f.setVisible(true)
  }

  private class FontsFrame() extends ApplicationFrame {
    val fontsPanel = new AllFonts.FontsPanel
    fontsPanel.setPreferredSize(new Dimension(1100, 14000))
    val pane = new JScrollPane(fontsPanel)
    this.getContentPane.add(pane)
  }

  private class FontsPanel extends JPanel {
    override def paint(g: Graphics): Unit = {
      super.paint(g)
      val g2 = g.asInstanceOf[Graphics2D]
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      drawAllFonts(g2)
    }

    private def drawAllFonts(g2: Graphics2D): Unit = {
      val fonts = getAllFonts
      var i = 0
      for (font <- fonts) {
        i += 1
        drawFontMessage(i, font, g2)
      }
    }

    /** get a list of all fonts, with the ones that support japanese and vietnamese characters first */
    private def getAllFonts = {
      val multiLingualFonts = new ArrayBuffer[Font]()
      val otherFonts = new ArrayBuffer[Font]()
      val allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment.getAvailableFontFamilyNames
      for (fontName <- allFonts) {
        val font = new Font(fontName, Font.PLAIN, FONT_SIZE)
        if (supportsChars(font, SPECIAL_CHARS)) multiLingualFonts.append(font)
        else otherFonts.append(font)
      }
      val combinedList = new ArrayBuffer[Font]()
      combinedList.append(multiLingualFonts: _*)
      combinedList.append(otherFonts: _*)
      combinedList
    }

    private def supportsChars(font: Font, specialChars: String): Boolean = {
      var i = 0
      while (i < specialChars.length) {
        if (!font.canDisplay(specialChars.charAt(i))) return false
        i += 1
      }
      true
    }

    private def drawFontMessage(i: Int, font: Font, g2: Graphics2D): Unit = {
      g2.setFont(font)
      val yPos = 25 + ROW_HEIGHT * i
      g2.drawString(font.getName, 40, yPos)
      g2.drawString(MULTI_LINGUAL_STRING, 500, yPos)
    }
  }
}
