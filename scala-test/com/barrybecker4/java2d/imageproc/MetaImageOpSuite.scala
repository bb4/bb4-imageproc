package com.barrybecker4.java2d.imageproc

import java.awt.RenderingHints
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.image.{BufferedImage, BufferedImageOp, ColorModel}

import com.barrybecker4.optimization.parameter.types.DoubleParameter
import com.jhlabs.image.{BumpFilter, GrayscaleFilter}
import org.scalatest.funsuite.AnyFunSuite

class MetaImageOpSuite extends AnyFunSuite:

  class StubBufferedOp extends BufferedImageOp:
    override def filter(src: BufferedImage, dest: BufferedImage): BufferedImage = null
    override def getBounds2D(src: BufferedImage): Rectangle2D = null
    override def createCompatibleDestImage(src: BufferedImage, destCM: ColorModel): BufferedImage = null
    override def getPoint2D(srcPt: Point2D, dstPt: Point2D): Point2D = null
    override def getRenderingHints: RenderingHints = null

  test("construction with static op") {
    val bop = new StubBufferedOp()
    val op = new MetaImageOp(bop)
    assert(op ne null)
    assertResult(bop)(op.getInstance)
  }

  test("copy yields distinct MetaImageOp") {
    val bop = new StubBufferedOp()
    val op = new MetaImageOp(bop)
    val op2 = op.copy
    assert(op2 ne null)
    assert(op2 ne op)
  }

  private def bumpHeightParams(height: Double) =
    Seq(DoubleParameter(height, 0.2, 1.8, "height", None))

  test("getInstance applies DoubleParameter to JHLabs BumpFilter via reflection") {
    val bump = new BumpFilter()
    val meta = new MetaImageOp(bump, bumpHeightParams(1.25))
    assertResult(bump)(meta.getInstance)
    assert(meta.getLastUsedParameters.exists(_.name == "height"))
    assert(Math.abs(meta.getLastUsedParameters.find(_.name == "height").get.getValue - 1.25) < 1e-9)
  }

  test("updateParameter then getInstance updates last-used height param") {
    val bump = new BumpFilter()
    val meta = new MetaImageOp(bump, bumpHeightParams(1.0))
    meta.getInstance
    meta.updateParameter(DoubleParameter(1.6, 0.2, 1.8, "height", None))
    meta.getInstance
    assert(Math.abs(meta.getLastUsedParameters.find(_.name == "height").get.getValue - 1.6) < 1e-9)
  }

  test("updateParameter rejects unknown name") {
    val bump = new BumpFilter()
    val meta = new MetaImageOp(bump, Seq(DoubleParameter(1.0, 0.2, 1.8, "height", None)))
    intercept[IllegalArgumentException] {
      meta.updateParameter(DoubleParameter(1.0, 0.0, 1.0, "nope", None))
    }
  }

  test("secondary constructor instantiates JHLabs filter class") {
    val meta = new MetaImageOp(classOf[GrayscaleFilter], Seq())
    assert(meta.getInstance.isInstanceOf[GrayscaleFilter])
  }
