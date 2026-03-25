package com.barrybecker4.java2d.ui

import scala.compiletime.uninitialized
import scala.util.{Left, Right}

import com.barrybecker4.java2d.Utilities
import javax.swing._
import java.awt._
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import java.awt.geom.Line2D
import java.awt.image.BufferedImage


/**
  * Shows an image that can be split down the middle according to where the user clicks.
  * There are left and right images shown on either side of the split.
  * @author Barry Becker
  */
class SplitImageComponent private (source: Either[String, BufferedImage]) extends JPanel:
  private var mImage: BufferedImage = uninitialized
  private var mSecondImage: BufferedImage = uninitialized
  private var mSplitX = 0

  source match
    case Left(path)   => setImageFromResource(path)
    case Right(image) => mImage = image
  init()

  def this(path: String) = this(Left(path))

  def this(image: BufferedImage) = this(Right(image))

  def setImage(path: String): Unit = {
    val image = Utilities.blockingLoad(path)
    mImage = Utilities.makeBufferedImage(image)
  }

  private def setImageFromResource(path: String): Unit =
    val image = Utilities.blockingLoad(getClass.getResource(path))
    mImage = Utilities.makeBufferedImage(image)

  def setImage(image: BufferedImage): Unit = {
    mImage = image
  }

  def setSecondImage(image: BufferedImage): Unit = {
    mSecondImage = image
    repaint()
  }

  def getImage: BufferedImage = mImage
  def getSecondImage: BufferedImage = mSecondImage

  private def init(): Unit = {
    setBackground(Color.white)
    addMouseListener(new MouseAdapter() {
      override def mousePressed(me: MouseEvent): Unit = {
        setSplitX(me.getX)
      }
    })
    addMouseMotionListener(new MouseMotionAdapter() {
      override def mouseDragged(me: MouseEvent): Unit = {
        setSplitX(me.getX)
      }
    })
  }

  def setSplitX(pos: Int): Unit = {
    mSplitX = pos
    repaint()
  }

  def getSplitX: Int = mSplitX

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    val g2 = g.asInstanceOf[Graphics2D]
    val width: Int = getSize().width
    val height: Int = getSize().height
    val splitX = getSplitX
    clear(g2)
    if (splitX != 0 && mSecondImage != null) {
      val firstClip = new Rectangle(splitX, 0, width - splitX, height)
      g2.setClip(firstClip)
    }
    g2.drawImage(getImage, 0, 0, null)
    if (splitX == 0 || mSecondImage == null) return
    val secondClip = new Rectangle(0, 0, splitX, height)
    g2.setClip(secondClip)
    g2.drawImage(mSecondImage, 0, 0, null)
    val splitLine = new Line2D.Float(splitX.toFloat, 0f, splitX.toFloat, height.toFloat)
    g2.setClip(null)
    g2.setColor(Color.white)
    g2.draw(splitLine)
  }

  private def clear(g2: Graphics2D): Unit = {
    val width = getSize().width
    val height = getSize().height
    val clear = new Rectangle(0, 0, width, height)
    g2.setPaint(getBackground)
    g2.fill(clear)
  }

  override def getPreferredSize: Dimension = {
    var width = 100
    var height = 100
    if (getImage != null) {
      width = getImage.getWidth
      height = getImage.getHeight
    }
    if (mSecondImage != null) {
      width = Math.max(width, mSecondImage.getWidth)
      height = Math.max(height, mSecondImage.getHeight)
    }
    new Dimension(width, height)
  }
