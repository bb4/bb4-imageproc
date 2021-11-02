package com.barrybecker4.java2d.imageproc

import java.awt.RenderingHints
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.image.{BufferedImage, BufferedImageOp, ColorModel}
import org.scalatest.funsuite.AnyFunSuite


/**
  * @author Barry Becker
  */

class MetaImageOpSuite extends  AnyFunSuite {

  class StubBufferedOp extends BufferedImageOp {
    override def filter(src: BufferedImage, dest: BufferedImage): BufferedImage = null
    override def getBounds2D(src: BufferedImage): Rectangle2D = null
    override def createCompatibleDestImage(src: BufferedImage, destCM: ColorModel): BufferedImage = null
    override def getPoint2D(srcPt: Point2D, dstPt: Point2D): Point2D = null
    override def getRenderingHints: RenderingHints = null
  }

  def testConstruction(): Unit = {
    val bop = new StubBufferedOp()
    val op = new MetaImageOp(bop)
    assert(op != null)
  }

  def testCopy(): Unit = {
    val bop = new StubBufferedOp()
    val op = new MetaImageOp(bop)
    val op2 = op.copy
    assert(op2 != null)
    assert(op2 != op, "Unexpectedly same")
  }
}
