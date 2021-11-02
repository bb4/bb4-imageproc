package com.barrybecker4.java2d.examples

import com.barrybecker4.java2d.Utilities
import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.font.TextLayout
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D
import java.io.IOException


/**
  * The image is loaded either from this
  * default filename or the first command-
  * line argument.
  * The second command-line argument specifies
  * what string will be displayed. The third
  * specifies at what point in the string the
  * background color will change.
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object ShowOff extends App {
    var filename = Utilities.DEFAULT_IMAGE_DIR + "Raphael.jpg"
    var message = "Java2D"
    var split = 4
    if (args.length > 0) filename = args(0)
    if (args.length > 1) message = args(1)
    if (args.length > 2) split = args(2).toInt
    val f = new ApplicationFrame("ShowOff v1.0")
    f.getContentPane.setLayout(new BorderLayout)
    val showOff = new ShowOff(filename, message, split)
    f.getContentPane.add(showOff, BorderLayout.CENTER)
    f.center()
    f.setResizable(false)
}

/** @param filename the file name
  * @param message  message to display
  * @param split  number of splits
  */
class ShowOff(val filename: String, var message: String, var split: Int) extends Component {
  val img: Image = Utilities.blockingLoad(getClass.getResource(filename))
  private var mImage = Utilities.makeBufferedImage(img)
  private var mLayout: TextLayout = _

  // Create a font.
  private var mFont = new Font("Serif", Font.PLAIN, 116)
  // Save the message and split.
  // Set our size to match the image's size.
  setSize(mImage.getWidth, mImage.getHeight)

  override def paint(g: Graphics): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    // Turn on anti-aliasing.
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    drawBackground(g2)
    drawImageMosaic(g2)
    drawText(g2)
  }

  // Draw circles of different colors.
  protected def drawBackground(g2: Graphics2D): Unit = {
    val side: Float = 45
    val width = getSize.width
    val height = getSize.height
    val colors = Array(Color.yellow, Color.cyan, Color.orange, Color.pink, Color.magenta, Color.lightGray)
    var y: Float = 0
    while (y < height) {
      var x: Float = 0
      while (x < width) {
        val ellipse = new Ellipse2D.Float(x, y, side, side)
        val index = (x + y) / side % colors.length
        g2.setPaint(colors(index.toInt))
        g2.fill(ellipse)
        x += side
      }
      y += side
    }
  }

  /** Break the image up into tiles. Draw each tile with its own transparency, allowing
    * the background to show through to varying degrees.
    */
  protected def drawImageMosaic(g2: Graphics2D): Unit = {
    val side = 36
    val width = mImage.getWidth
    val height = mImage.getHeight
    var y = 0
    while (y < height) {
      var x = 0
      while (x < width) { // Calculate an appropriate transparency value.
        val xBias = x.toFloat / width.toFloat
        val yBias = y.toFloat / height.toFloat
        val alpha = 1.0f - Math.abs(xBias - yBias)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha))
        // Draw the subimage.
        val w = Math.min(side, width - x)
        val h = Math.min(side, height - y)
        val tile = mImage.getSubimage(x, y, w, h)
        g2.drawImage(tile, x, y, null)
        x += side
      }
      y += side
    }
    // Reset the composite.
    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER))
  }

  protected def drawText(g2: Graphics2D): Unit = { // Find the bounds of the entire string.
    val frc = g2.getFontRenderContext
    mLayout = new TextLayout(message, mFont, frc)
    // Find the dimensions of this component.
    val width = getSize.width
    val height = getSize.height
    // Place the first full string, horizontally centered,
    //   at the bottom of the component.
    val bounds = mLayout.getBounds
    val x = (width - bounds.getWidth) / 2
    val y = height - bounds.getHeight
    drawString(g2, x, y, 0)
    // Now draw a second version, anchored to the right side
    //   of the component and rotated by -PI / 2.
    drawString(g2, width - bounds.getHeight, y, -Math.PI / 2)
  }

  protected def drawString(g2: Graphics2D, x: Double, y: Double, theta: Double): Unit = {
    // Transform to the requested location.
    g2.translate(x, y)
    // Rotate by the requested angle.
    g2.rotate(theta)
    // Draw the first part of the string.
    val first = message.substring(0, split)
    val width = drawBoxedString(g2, first, Color.white, Color.red, 0)
    // Draw the second part of the string.
    val second = message.substring(split)
    drawBoxedString(g2, second, Color.blue, Color.white, width)
    // Undo the transformations.
    g2.rotate(-theta)
    g2.translate(-x, -y)
  }

  protected def drawBoxedString(g2: Graphics2D, s: String, c1: Color, c2: Color, x: Double): Float = {
    // Calculate the width of the string.
    val frc = g2.getFontRenderContext
    val subLayout = new TextLayout(s, mFont, frc)
    val advance = subLayout.getAdvance
    // Fill the background rectangle with a gradient.
    val gradient = new GradientPaint(x.toFloat, 0, c1, (x + advance).toFloat, 0, c2)
    g2.setPaint(gradient)
    val bounds = mLayout.getBounds
    val back = new Rectangle2D.Double(x, 0, advance, bounds.getHeight)
    g2.fill(back)
    // Draw the string over the gradient rectangle.
    g2.setPaint(Color.white)
    g2.setFont(mFont)
    g2.drawString(s, x.toFloat, -bounds.getY.toFloat)
    advance
  }
}