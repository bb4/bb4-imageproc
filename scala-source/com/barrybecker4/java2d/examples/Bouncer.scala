package com.barrybecker4.java2d.examples

import java.awt.{BasicStroke, BorderLayout, Checkbox, Color, Dimension, Font, GradientPaint, Graphics, Graphics2D, Panel, RenderingHints, Shape}
import com.barrybecker4.ui.animation.AnimationComponent
import com.barrybecker4.ui.animation.AnimationFrame
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ItemEvent
import java.awt.geom.{GeneralPath, Path2D}
import java.util.Random

import com.barrybecker4.common.app.AppContext
import com.barrybecker4.ui.util.Log


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object Bouncer {
  val ANTIALIASING = 0
  val GRADIENT = 1
  val OUTLINE = 2
  val TRANSFORM = 3
  val DOTTED = 4
  val AXES = 5
  val CLIP = 6
  val SPEED = 3
  val NUM_BALLS = 20
  val GRAVITY = .3f

  def main(args: Array[String]): Unit = {
    val bouncer = new Bouncer
    val f = new AnimationFrame(bouncer)
    bouncer.setFont(new Font("Serif", Font.PLAIN, 12))
    bouncer.setPaused(false)
    val controls = new Panel
    controls.add(bouncer.createCheckbox("Anti.", Bouncer.ANTIALIASING))
    controls.add(bouncer.createCheckbox("Trans.", Bouncer.TRANSFORM))
    controls.add(bouncer.createCheckbox("Gradient", Bouncer.GRADIENT))
    controls.add(bouncer.createCheckbox("Outline", Bouncer.OUTLINE))
    controls.add(bouncer.createCheckbox("Dotted", Bouncer.DOTTED))
    controls.add(bouncer.createCheckbox("Axes", Bouncer.AXES))
    controls.add(bouncer.createCheckbox("Clip", Bouncer.CLIP))
    f.getContentPane.add(controls, BorderLayout.NORTH)
  }
}

class Bouncer() extends AnimationComponent {

  private var mN = Bouncer.NUM_BALLS //38
  private var mPoints = new Array[Float](mN + 3)
  private var mDeltas = new Array[Float](mN + 3)
  val random = new Random
  var i = 0
  while (i < mN) {
    mPoints(i) = random.nextFloat * 500
    mDeltas(i) = random.nextFloat * Bouncer.SPEED
    i += 1
  }
  // Make sure points are within range.
  addComponentListener(new BouncerComponentAdapter)
  // Tweakable variables
  private var mAntialiasing = false
  private var mGradient = false
  private var mOutline = false
  private var mTransform = false
  private var mDotted = false
  private var mAxes = false
  private var mClip = false
  private var mTheta = .0
  private var mClipShape: Shape = _

  AppContext.initialize("ENGLISH",
    List[String]("com.barrybecker4.ui.message", "com.barrybecker4.java2d.examples.message"), new Log())

  def setSwitch(item: Int, value: Boolean): Unit = {
    item match {
      case Bouncer.ANTIALIASING =>
        mAntialiasing = value
      case Bouncer.GRADIENT =>
        mGradient = value
      case Bouncer.OUTLINE =>
        mOutline = value
      case Bouncer.TRANSFORM =>
        mTransform = value
      case Bouncer.DOTTED =>
        mDotted = value
      case Bouncer.AXES =>
        mAxes = value
      case Bouncer.CLIP =>
        mClip = value
      case _ => throw new UnsupportedOperationException("Unsupported switch: " + item)
    }
  }

  protected def createCheckbox(label: String, item: Int): Checkbox = {
    val check = new Checkbox(label)
    check.addItemListener((ie: ItemEvent) => setSwitch(item, ie.getStateChange == ItemEvent.SELECTED))
    check
  }

  override def getFileNameBase = "D:/f"

  override def timeStep: Double = {
    val d: Dimension = getSize
    var i = 0
    while (i < mN) {
      val xAxis = (i % 2) == 0
      if (!xAxis) mDeltas(i) += Bouncer.GRAVITY
      val value = mPoints(i) + mDeltas(i)
      val limit = if (xAxis) d.width
      else d.height
      if (value < 0 || value > limit) {
        mDeltas(i) = -mDeltas(i)
        if (value < 0) mPoints(i) = -value
        else if (value > limit) mPoints(i) = limit - (value - limit)
      }
      else mPoints(i) = value
      i += 1
    }
    mTheta += Math.PI / 192
    if (mTheta > (2 * Math.PI)) mTheta -= (2 * Math.PI)
    0
  }

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    setAntialiasing(g2)
    setClip(g2)
    setTransform(g2)
    val shape = createShape
    setPaint(g2)
    // Fill the shape.
    g2.fill(shape)
    // Maybe draw the outline.
    if (mOutline) {
      setStroke(g2)
      g2.setPaint(Color.blue)
      g2.draw(shape)
    }
    drawAxes(g2)
  }

  protected def setAntialiasing(g2: Graphics2D): Unit = {
    if (!mAntialiasing) return
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  }

  protected def setClip(g2: Graphics2D): Unit = {
    if (!mClip) return
    if (mClipShape == null) {
      val d: Dimension = getSize
      val frc = g2.getFontRenderContext
      val font = new Font("Serif", Font.PLAIN, 144)
      val s = "Spoon!"
      val gv = font.createGlyphVector(frc, s)
      val bounds = font.getStringBounds(s, frc)
      mClipShape = gv.getOutline((d.width - bounds.getWidth.toFloat) / 2, (d.height + bounds.getHeight.toFloat) / 2)
    }
    g2.clip(mClipShape)
  }

  protected def setTransform(g2: Graphics2D): Unit = {
    if (!mTransform) return
    val d: Dimension = getSize
    g2.rotate(mTheta, d.width / 2, d.height / 2)
  }

  protected def createShape: Shape = {
    val path = new GeneralPath(Path2D.WIND_EVEN_ODD, mPoints.length)
    path.moveTo(mPoints(0), mPoints(1))
    var i = 2
    while (i < mN) {
      path.curveTo(mPoints(i), mPoints(i + 1), mPoints(i + 2), mPoints(i + 3), mPoints(i + 4), mPoints(i + 5))
      i += 6
    }
    path.closePath()
    path
  }

  protected def setPaint(g2: Graphics2D): Unit = {
    if (mGradient) {
      val gp = new GradientPaint(0, 0, Color.yellow, 50, 25, Color.red, true)
      g2.setPaint(gp)
    }
    else g2.setPaint(Color.orange)
  }

  protected def setStroke(g2: Graphics2D): Unit = {
    if (!mDotted) return
    // Create a dotted stroke.
    val stroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10, Array[Float](4, 4), 0)
    g2.setStroke(stroke)
  }

  protected def drawAxes(g2: Graphics2D): Unit = {
    if (!mAxes) return
    g2.setPaint(getForeground)
    g2.setStroke(new BasicStroke)
    val d: Dimension = getSize
    val side = 20
    val arrow = 4
    val w = d.width / 2
    val h = d.height / 2
    g2.drawLine(w - side, h, w + side, h)
    g2.drawLine(w + side - arrow, h - arrow, w + side, h)
    g2.drawLine(w, h - side, w, h + side)
    g2.drawLine(w + arrow, h + side - arrow, w, h + side)
  }

  private class BouncerComponentAdapter extends ComponentAdapter {
    override def componentResized(ce: ComponentEvent): Unit = {
      val d: Dimension = getSize
      var i = 0
      while (i < mN) {
        val limit = if ((i % 2) == 0) d.width
        else d.height
        if (mPoints(i) < 0) mPoints(i) = 0
        else if (mPoints(i) >= limit) mPoints(i) = limit - 1
        i += 1
      }
    }
  }
}