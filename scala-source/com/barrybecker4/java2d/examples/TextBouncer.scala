package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.animation.AnimationComponent
import com.barrybecker4.ui.animation.AnimationFrame
import java.awt._
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.ItemEvent
import java.awt.font.FontRenderContext
import java.awt.geom.Rectangle2D
import java.util.Random
import TextBouncerConstants.{ANTIALIASING, AXES, GRADIENT, ROTATE, SHEAR, SHEAR_SCALE}
import com.barrybecker4.common.app.AppContext
import com.barrybecker4.ui.util.Log

object TextBouncerConstants {
  val ANTIALIASING = 0
  val GRADIENT = 1
  val SHEAR = 2
  val ROTATE = 3
  val AXES = 5
  val SHEAR_SCALE = 0.02f
}

/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object TextBouncer extends App {

  var s = "Firenze"
  val size = 64
  if (args.length > 0)
    s = args(0)
  val controls = new Panel
  val choice = new Choice
  val ge = GraphicsEnvironment.getLocalGraphicsEnvironment
  val allFonts = ge.getAllFonts

  for (allFont <- allFonts) {
    choice.addItem(allFont.getName)
  }
  val defaultFont = new Font(allFonts(0).getName, Font.PLAIN, size)
  val bouncer = new TextBouncer(s, defaultFont)
  val f = new AnimationFrame(bouncer)
  f.setFont(new Font("Serif", Font.PLAIN, 12))
  controls.add(bouncer.createCheckbox("Antialiasing", ANTIALIASING))
  controls.add(bouncer.createCheckbox("Gradient", GRADIENT))
  controls.add(bouncer.createCheckbox("Shear", SHEAR))
  controls.add(bouncer.createCheckbox("Rotate", ROTATE))
  controls.add(bouncer.createCheckbox("Axes", AXES))
  val fontControls = new Panel
  choice.addItemListener((ie: ItemEvent) => {
    val font = new Font(choice.getSelectedItem, Font.PLAIN, size)
    bouncer.setFont(font)
  })
  fontControls.add(choice)
  val allControls = new Panel(new GridLayout(2, 1))
  allControls.add(controls)
  allControls.add(fontControls)
  f.add(allControls, BorderLayout.NORTH)
  bouncer.setPaused(false)
}

class TextBouncer(var mString: String, val f: Font) extends AnimationComponent {

  AppContext.initialize("ENGLISH",
    scala.List[String]("com.barrybecker4.ui.message", "com.barrybecker4.java2d.examples.message"), new Log())

  // Make sure points are within range.
  private var mAntialiasing = false
  private var mGradient = false
  private var mShear = false
  private var mRotate = false
  private var mAxes = false
  private var mDeltaX: Float = 0
  private var mDeltaY: Float = 0
  private var mX: Float = 0
  private var mY: Float = 0
  private var mWidth: Float = 0
  private var mHeight: Float = 0
  private var mTheta: Float = 0
  private var mShearX: Float = 0
  private var mShearY: Float = 0
  private var mShearDeltaX: Float = 0
  private var mShearDeltaY: Float = 0
  val frc = new FontRenderContext(null, true, false)
  addComponentListener(new BouncerComponentAdapter)
  setFont(f)
  val strBounds: Rectangle2D = getFont.getStringBounds(mString, frc)
  mWidth = strBounds.getWidth.toFloat
  mHeight = strBounds.getHeight.toFloat

  reset()

  protected def reset(): Unit = {
    val random = new Random
    mX = random.nextFloat * 500
    mY = random.nextFloat * 500
    mDeltaX = 0.01f + random.nextFloat / 2.0f
    mDeltaY = 0.01f + random.nextFloat / 2.0f
    mShearX = random.nextFloat / 7
    mShearY = random.nextFloat / 7
    mShearDeltaX = SHEAR_SCALE
    mShearDeltaY = SHEAR_SCALE
  }

  override def getFileNameBase: String = null

  def setSwitch(item: Int, value: Boolean): Unit = {
    item match {
      case ANTIALIASING =>
        mAntialiasing = value
      case GRADIENT =>
        mGradient = value
      case SHEAR =>
        mShear = value
      case ROTATE =>
        mRotate = value
      case AXES =>
        mAxes = value
      case _ =>
    }
  }

  protected def createCheckbox(label: String, item: Int): Checkbox = {
    val check = new Checkbox(label)
    check.addItemListener((ie: ItemEvent) => {
        setSwitch(item, ie.getStateChange == ItemEvent.SELECTED)
    })
    check
  }

  override def timeStep: Double = {
    val d: Dimension = getSize
    if (mX + mDeltaX < 0) mDeltaX = -mDeltaX
    else if (mX + mWidth + mDeltaX >= d.width) mDeltaX = -mDeltaX
    if (mY + mDeltaY < 0) mDeltaY = -mDeltaY
    else if (mY + mHeight + mDeltaY >= d.height) mDeltaY = -mDeltaY
    mX += mDeltaX
    mY += mDeltaY
    mTheta += (Math.PI / 384).toFloat
    if (mTheta > (2 * Math.PI)) mTheta -= (2 * Math.PI).toFloat
    val shearThresh = 10.0 * SHEAR_SCALE
    if (mShearX + mShearDeltaX > shearThresh) mShearDeltaX = -mShearDeltaX
    else if (mShearX + mShearDeltaX < -shearThresh) mShearDeltaX = -mShearDeltaX
    if (mShearY + mShearDeltaY > .5) mShearDeltaY = -mShearDeltaY
    else if (mShearY + mShearDeltaY < -shearThresh) mShearDeltaY = -mShearDeltaY
    mShearX += mShearDeltaX
    mShearY += mShearDeltaY
    0
  }

  override def paint(g: Graphics): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    setAntialiasing(g2)
    setTransform(g2)
    setPaint(g2)
    // Draw the string.
    g2.setFont(getFont)
    g2.drawString(mString, mX, mY + mHeight)
    drawAxes(g2)
  }

  protected def setAntialiasing(g2: Graphics2D): Unit = {
    if (!mAntialiasing) return
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
  }

  protected def setTransform(g2: Graphics2D): Unit = {
    val d: Dimension = getSize
    val cx: Float = d.width / 2.0f
    val cy: Float = d.height / 2.0f
    g2.translate(cx, cy)
    if (mShear) g2.shear(mShearX, mShearY)
    if (mRotate) g2.rotate(mTheta)
    g2.translate(-cx, -cy)
  }

  protected def setPaint(g2: Graphics2D): Unit = {
    if (mGradient) {
      val gp = new GradientPaint(0, 0, Color.blue, 50, 25, Color.green, true)
      g2.setPaint(gp)
    }
    else g2.setPaint(Color.orange)
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
      val w: Float = d.width
      val h: Float = d.height
      mX = if (mX < 0) 0 else if (mX + mWidth >= w) w - mWidth - 1f else mX
      mY = if (mY < 0) 0 else if (mY + mHeight >= h) h - mHeight - 1f else mY
    }
  }

}