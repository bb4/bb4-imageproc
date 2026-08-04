package com.barrybecker4.java2d.imageproc

import java.awt.RenderingHints
import java.awt.geom.{Point2D, Rectangle2D}
import java.awt.image.{BufferedImage, BufferedImageOp, ColorModel}

import com.barrybecker4.optimization.parameter.types.{BooleanParameter, DoubleParameter}
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

  test("setterNameFor capitalizes first letter") {
    assertResult("setHeight")(MetaImageOp.setterNameFor("height"))
    assertResult("setX")(MetaImageOp.setterNameFor("x"))
  }

  test("alternateBoxedOrPrimitive swaps boxed and primitive") {
    assertResult(classOf[java.lang.Integer])(MetaImageOp.alternateBoxedOrPrimitive(java.lang.Integer.TYPE))
    assertResult(java.lang.Integer.TYPE)(MetaImageOp.alternateBoxedOrPrimitive(classOf[java.lang.Integer]))
    assertResult(classOf[java.lang.Double])(MetaImageOp.alternateBoxedOrPrimitive(java.lang.Double.TYPE))
    assertResult(java.lang.Boolean.TYPE)(MetaImageOp.alternateBoxedOrPrimitive(classOf[java.lang.Boolean]))
  }

  test("coerceArgument boxes numeric and boolean parameters") {
    val d = DoubleParameter(1.5, 0.0, 2.0, "amount", None)
    assertResult(1.5f)(MetaImageOp.coerceArgument(d, java.lang.Float.TYPE).asInstanceOf[java.lang.Float].floatValue())
    assertResult(1)(MetaImageOp.coerceArgument(d, java.lang.Integer.TYPE).asInstanceOf[Integer].intValue())
    assertResult(1.5)(MetaImageOp.coerceArgument(d, java.lang.Double.TYPE).asInstanceOf[java.lang.Double].doubleValue())

    val b = BooleanParameter(true, "flag", None)
    assertResult(true)(MetaImageOp.coerceArgument(b, java.lang.Boolean.TYPE).asInstanceOf[java.lang.Boolean].booleanValue())
  }

  test("coerceArgument rejects unsupported types") {
    val d = DoubleParameter(1.0, 0.0, 2.0, "amount", None)
    intercept[IllegalArgumentException] {
      MetaImageOp.coerceArgument(d, classOf[java.lang.Long])
    }
  }
