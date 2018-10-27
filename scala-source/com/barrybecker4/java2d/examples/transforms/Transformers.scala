package com.barrybecker4.java2d.examples.transforms

import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import java.awt.geom.Rectangle2D


abstract class Transformers() extends Component {

  private[transforms] val mLength = 54
  private[transforms] val mArrowLength = 4
  private[transforms] val mTickSize = 4
  private[transforms] var mAxes = createAxes
  private[transforms] var mShape = createShape

  protected def createAxes: Shape = {
    val path = new GeneralPath
    // Axes.
    path.moveTo(-mLength, 0)
    path.lineTo(mLength, 0)
    path.moveTo(0, -mLength)
    path.lineTo(0, mLength)

    // Arrows.
    path.moveTo(mLength - mArrowLength, -mArrowLength)
    path.lineTo(mLength, 0)
    path.lineTo(mLength - mArrowLength, mArrowLength)
    path.moveTo(-mArrowLength, mLength - mArrowLength)
    path.lineTo(0, mLength)
    path.lineTo(mArrowLength, mLength - mArrowLength)

    // Half-centimeter tick marks
    val cm = 72 / 2.54f
    val lengthCentimeter = mLength / cm
    var i: Float = 0.5f
    while (i < lengthCentimeter) {
      val tick = i * cm
      path.moveTo(tick, -mTickSize / 2)
      path.lineTo(tick, mTickSize / 2)
      path.moveTo(-tick, -mTickSize / 2)
      path.lineTo(-tick, mTickSize / 2)
      path.moveTo(-mTickSize / 2, tick)
      path.lineTo(mTickSize / 2, tick)
      path.moveTo(-mTickSize / 2, -tick)
      path.lineTo(mTickSize / 2, -tick)
      i += 1.0f
    }

    // Full-centimeter tick marks
    i = 1.0f
    while (i < lengthCentimeter) {
      val tick = i * cm
      path.moveTo(tick, -mTickSize)
      path.lineTo(tick, mTickSize)
      path.moveTo(-tick, -mTickSize)
      path.lineTo(-tick, mTickSize)
      path.moveTo(-mTickSize, tick)
      path.lineTo(mTickSize, tick)
      path.moveTo(-mTickSize, -tick)
      path.lineTo(mTickSize, -tick)
      i += 1.0f
    }
    path
  }

  protected def createShape: Shape = {
    val cm = 72 / 2.54f
    new Rectangle2D.Float(cm, cm, 2 * cm, cm)
  }

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    // Use antialiasing.
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    // Move the origin to 75, 75.
    val at = AffineTransform.getTranslateInstance(75, 75)
    g2.transform(at)
    // Draw the shapes in their original locations.
    g2.setPaint(Color.black)
    g2.draw(mAxes)
    g2.draw(mShape)
    // Transform the Graphics2D.
    g2.transform(getTransform)
    // Draw the shapes in their new locations, but dashed.
    val stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, Array[Float](3, 1), 0)
    g2.setStroke(stroke)
    g2.draw(mAxes)
    g2.draw(mShape)
  }

  def getTransform: AffineTransform

  def getFrame: Frame = {
    val f = new ApplicationFrame("...more than meets the eye")
    f.getContentPane.setLayout(new BorderLayout)
    f.getContentPane.add(this, BorderLayout.CENTER)
    f.setSize(400, 400)
    f.center()
    f
  }
}