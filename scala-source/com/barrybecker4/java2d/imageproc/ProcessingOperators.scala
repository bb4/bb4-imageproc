/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import java.awt.image.{ConvolveOp, Kernel, LookupOp, ShortLookupTable}

import com.barrybecker4.optimization.parameter.types.*
import com.jhlabs.image.*

import scala.collection.mutable

/**
  * A set of  available image processing operations.
  * @author Barry Becker
  */
object ProcessingOperators {

  private type Registry = mutable.Map[String, MetaImageOp]

  /** Builds the operator registry (single place initialization runs). */
  private[imageproc] def buildRegistry(): Map[String, MetaImageOp] = {
    val b = mutable.Map.empty[String, MetaImageOp]
    registerConvolutions(b)
    registerColorOps(b)
    registerJHLabsPart1(b)
    registerJHLabsPart2(b)
    registerJHLabsPart3(b)
    b.toMap
  }

  private def createCausticsOp: MetaImageOp = {
    val params = Seq(
      DoubleParameter(0.0, 0, 10.0, "time", None),
      DoubleParameter(32.0, 0.1, 100.0, "scale", None),
      IntegerParameter(10, 1, 50, "brightness", None),
      DoubleParameter(0.0, 0.0, 10.0, "turbulence", None),
      DoubleParameter(0.0, 0.0, 1.0, "dispersion", None),
      DoubleParameter(1.0, 0.1, 2.0, "amount", None)
    )
    new MetaImageOp(classOf[CausticsFilter], params)
  }

  private def createCellularOp: MetaImageOp = {
    val specValues: Array[Int] = Array(CellularFilter.GridType.RANDOM.ordinal)
    val specValueProbs: Array[Double] = Array(0.6)

    val params = Seq(
      BooleanParameter(true, "useColor", None),
      IntegerParameter.createDiscreteParameter(CellularFilter.GridType.RANDOM.ordinal,
        0, CellularFilter.GridType.values.length, "gridType", specValues, specValueProbs),
      IntegerParameter(1, 1, 20, "turbulence", None),
      DoubleParameter(0.0, 0.0, 1.0, "F1", None),
      DoubleParameter(0.0, 0.0, 1.0, "F2", None),
      DoubleParameter(0.0, 0.0, 1.0, "randomness", None),
      DoubleParameter(.5, 0.0, 1.0, "amount", None),
      DoubleParameter(1.0, 0.0, 2.0, "gradientCoefficient", None),
      DoubleParameter(1.0, 1.0, 30.0, "stretch", None),
      DoubleParameter(0.0, 0.0, Math.PI, "angle", None),
      DoubleParameter(1.0, 0.0, 5.0, "angleCoefficient", None),
      IntegerParameter(1, 1, 6, "distancePower", None),
      DoubleParameter(16.0, 0.1, 64.0, "scale", None)
    )
    new MetaImageOp(classOf[CellularFilter], params)
  }

  private def createFractalOp: MetaImageOp = {
    val specValues: Array[Int] = Array(
      OperationType.REPLACE.ordinal,
      OperationType.NORMAL.ordinal,
      OperationType.MIN.ordinal, OperationType.MAX.ordinal,
      OperationType.ADD.ordinal, OperationType.SUBTRACT.ordinal, OperationType.MULTIPLY.ordinal,
      OperationType.HUE.ordinal, OperationType.SATURATION.ordinal, OperationType.VALUE.ordinal,
      OperationType.COLOR.ordinal,
      OperationType.SCREEN.ordinal,
      OperationType.AVERAGE.ordinal, OperationType.CLEAR.ordinal, OperationType.EXCHANGE.ordinal,
      OperationType.DISSOLVE.ordinal, OperationType.DST_IN.ordinal, OperationType.ALPHA.ordinal,
      OperationType.ALPHA_TO_GRAY.ordinal)

    val specValueProbs: Array[Double] =
      Array(0.010, 0.001, 0.050, 0.050, 0.060, 0.060, 0.120, 0.010, 0.040,
        0.001, 0.060, 0.060, 0.140, 0.000, 0.001, 0.040, 0.010, 0.010, 0.001)

    val sv: Array[Double] = Array(0.0)
    val svp: Array[Double] = Array(0.2)

    val params = Seq(
      StringParameter(FBMFilter.BasisType.CELLULAR.ordinal, FBMFilter.BasisType.values.map(_.toString).toIndexedSeq, "basisType", None),
      IntegerParameter.createDiscreteParameter(OperationType.MULTIPLY.ordinal,
        0, OperationType.values.length, "operation", specValues, specValueProbs),
      DoubleParameter(0.8, 0.1, 3.0, "amount", None),
      DoubleParameter(32, 4, 128, "scale", None),
      DoubleParameter.createUniformParameter(1.0, 1.0, 8.0, "stretch", sv, svp),
      DoubleParameter.createUniformParameter(0.0, 0.0, Math.PI, "angle", sv, svp),
      DoubleParameter(1.0, 0.0, 5.0, "H", None),
      DoubleParameter(2.0, 0.1, 4.0, "lacunarity", None),
      DoubleParameter(0.5, 0.1, 2.0, "gain", None),
      DoubleParameter.createGaussianParameter(0.5,
      0.0, 2.0, "bias", 0.24, 0.2),
      DoubleParameter(4.0, 0.1, 16.0, "octaves", None)
    )
    new MetaImageOp(classOf[FBMFilter], params)
  }

  private def createContourOp: MetaImageOp = {
    val params = Seq(
      DoubleParameter(5.0, 0.1, 10.0, "levels", None),
      DoubleParameter(1.0, 0.1, 10.0, "scale", None),
      DoubleParameter(0.0, 0.0, 2.0, "offset", None),
      IntegerParameter(0xff2200aa,
      0xff000000, 0xffffffff, "contourColor", None)
    )
    new MetaImageOp(classOf[ContourFilter], params)
  }

  private def createKaleidoscopeOp: MetaImageOp = {
    val sv: Array[Double] = Array(0.0)
    val svp: Array[Double] = Array(0.3)

    val params = Seq(
      IntegerParameter(3, 1, 6, "sides", None),
      DoubleParameter.createUniformParameter(0.0, 0.0, 500.0, "radius", sv, svp),
      DoubleParameter(0, 0.0, 2 * Math.PI, "angle", None),
      DoubleParameter(0, 0.0, Math.PI, "angle2", None),
      DoubleParameter.createGaussianParameter(
        0.5, 0.1, 0.9, "centreX", 0.5, .2),
      DoubleParameter.createGaussianParameter(
        0.5, 0.1, 0.9, "centreY", 0.5, 0.2)
    )
    new MetaImageOp(classOf[KaleidoscopeFilter], params)
  }

  private def registerConvolutions(b: Registry): Unit = {
    val ninth: Float = 1.0f / 9.0f
    val blurKernel: Array[Float] = Array(ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth)
    b += "Blur" -> new MetaImageOp(new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null))
    val edge: Array[Float] = Array(0f, -0.8f, 0f, -0.8f, 4.0f, -0.8f, 0f, -0.8f, 0f)
    b += "Edge detector" -> new MetaImageOp(new ConvolveOp(new Kernel(3, 3, edge), ConvolveOp.EDGE_NO_OP, null))
    val sharp: Array[Float] = Array(0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f)
    b += "Sharpen" -> new MetaImageOp(new ConvolveOp(new Kernel(3, 3, sharp)))
  }

  private def registerColorOps(b: Registry): Unit = {
    val brighten = ColorLookupTables.table(ColorLookupTables.brighten)
    val betterBrighten = ColorLookupTables.table(ColorLookupTables.betterBrighten)
    val posterize = ColorLookupTables.table(ColorLookupTables.posterize)
    val invert = ColorLookupTables.table(ColorLookupTables.invert)
    val straight = ColorLookupTables.table(ColorLookupTables.identity)
    val zero = ColorLookupTables.table(ColorLookupTables.zero)
    b += "Grayscale" -> new MetaImageOp(new GrayscaleFilter)
    b += "Brighten" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, ColorLookupTables.rgbWithAlpha(brighten)), null))
    b += "Better Brighten" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, ColorLookupTables.rgbWithAlpha(betterBrighten)), null))
    b += "Posterize" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, ColorLookupTables.rgbWithAlpha(posterize)), null))
    b += "Invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, ColorLookupTables.rgbWithAlpha(invert)), null))
    b += "Red invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, Array(invert, straight, straight, straight)), null))
    b += "Green invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, Array(straight, invert, straight, straight)), null))
    b += "Blue invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, Array(straight, straight, invert, straight)), null))
    b += "Red remove" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, Array(zero, straight, straight, straight)), null))
    b += "Green remove" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, Array(straight, zero, straight, straight)), null))
    b += "Blue remove" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, Array(straight, straight, zero, straight)), null))
  }

  private def registerJHLabsPart1(b: Registry): Unit = {
    b += "Caustics" -> createCausticsOp
    var params: Seq[AbstractParameter] = Seq(
      DoubleParameter.createGaussianParameter(1.0, 0.2, 1.8,
        "height", 0.5, 0.2)
    )
    b += "Bumps" -> new MetaImageOp(classOf[BumpFilter], params)
    b += "Cellular" -> createCellularOp
    b += "Contour" -> createContourOp
    params = Seq(
      BooleanParameter(false, "fadeEdges", None),
      DoubleParameter(0.4, 0.1, 2.0, "edgeThickness", None),
      IntegerParameter(0xff2200aa, 0xff000000, 0xffffffff, "edgeColor", None)
    )
    b += "Crystallize" -> new MetaImageOp(classOf[CrystallizeFilter], params)
    params = Seq(
      BooleanParameter(true, "emboss", None),
      DoubleParameter(2.0, 0.0, Math.PI, "azimuth", None),
      DoubleParameter(0.4, 0.0, Math.PI / 2.0, "elevation", None),
      DoubleParameter(0.5, 0.1, 2.5, "bumpHeight", None)
    )
    b += "Emboss" -> new MetaImageOp(classOf[EmbossFilter], params)
    b += "Equalize" -> new MetaImageOp(new EqualizeFilter)
    b += "Fractal Noise" -> createFractalOp
    params = Seq(
      BooleanParameter(true, "useImageColors", None),
      DoubleParameter(0.9, 0.01, 2.0, "turbulence", None),
      DoubleParameter(1.0, 0.01, 3.0, "scaling", None)
    )
    b += "Plasma" -> new MetaImageOp(classOf[PlasmaFilter], params)
    params = Seq(
      StringParameter(PolarFilter.PolarMappingType.RECT_TO_POLAR.ordinal,
        PolarFilter.PolarMappingType.values.map(_.toString).toIndexedSeq, "type", None),
      StringParameter(EdgeAction.WRAP.ordinal, EdgeAction.values.map(_.toString).toIndexedSeq, "edgeAction", None)
    )
    b += "Polar" -> new MetaImageOp(classOf[PolarFilter], params)
    params = Seq(
      StringParameter(RippleFilter.RippleType.SINE.ordinal,
        RippleFilter.RippleType.values.map(_.toString).toIndexedSeq, "waveType", None),
      DoubleParameter(5.0, 0.0, 10.0, "xAmplitude", None),
      DoubleParameter(0.0, 0.0, 10.0, "yAmplitude", None),
      DoubleParameter(16, 1, 64, "xWavelength", None),
      DoubleParameter(16, 1, 64, "yWavelength", None)
    )
    b += "Ripple" -> new MetaImageOp(classOf[RippleFilter], params)
    params = Seq(
      StringParameter(EdgeAction.WRAP.ordinal, EdgeAction.values.map(_.toString).toIndexedSeq, "edgeAction", None),
      DoubleParameter(2.0, 0.5, 6.0, "scale", None)
    )
    b += "Diffuse" -> new MetaImageOp(classOf[DiffuseFilter], params)
    params = Seq(
      DoubleParameter(1.0, 0.1, 5.0, "redGamma", None),
      DoubleParameter(1.0, 0.1, 5.0, "greenGamma", None),
      DoubleParameter(1.0, 0.1, 5.0, "blueGamma", None)
    )
    b += "Gamma" -> new MetaImageOp(classOf[GammaFilter], params)
  }

  private def registerJHLabsPart2(b: Registry): Unit = {
    var params: Seq[AbstractParameter] = Seq(
      StringParameter(LightFilter.BumpShapeType.NONE.ordinal,
        LightFilter.BumpShapeType.values.map(_.toString).toIndexedSeq, "bumpShape", None),
      DoubleParameter(.5, 0.1, 2.0, "bumpHeight", None),
      DoubleParameter(0.0, 0.0, 3.0, "bumpSoftness", None),
      DoubleParameter(10000.0, 10.0, 10000.0, "viewDistance", None)
    )
    b += "Light" -> new MetaImageOp(classOf[LightFilter], params)
    params = Seq(
      DoubleParameter(1.0, 0.8, 5.0, "amount", None),
      DoubleParameter(1.0, 0.5, 16.0, "turbulence", None),
      DoubleParameter(6.0, 1.0, 100.0, "xScale", None),
      DoubleParameter(6.0, 1.0, 100.0, "yScale", None)
    )
    b += "Marble" -> new MetaImageOp(classOf[MarbleFilter], params)
    params = Seq(
      DoubleParameter(1.0, 0.5, 10.0, "turbulence", None),
      DoubleParameter(0.5, 0.1, 5.0, "turbulenceFactor", None),
      DoubleParameter(32.0, 8.0, 128.0, "scale", None),
      DoubleParameter(0.0, 0.0, Math.PI, "angle", None),
      DoubleParameter(1.0, 0.5, 10.0, "stretch", None),
      DoubleParameter(1.0, 0.5, 6.0, "brightness", None)
    )
    b += "MarbleTexture" -> new MetaImageOp(classOf[MarbleTexFilter], params)
    params = Seq(
      BooleanParameter(true, "useOpacity", None),
      DoubleParameter(1.0, 0.1, 1.0, "opacity", None),
      DoubleParameter(0.5, 0.4, 0.9, "centreY", None)
    )
    b += "Mirror" -> new MetaImageOp(classOf[MirrorFilter], params)
    params = Seq(
      BooleanParameter(false, "raysOnly", None),
      DoubleParameter(0.5, 0.1, 1.0, "opacity", None),
      DoubleParameter(0.5, 0.1, 1.0, "threshold", None),
      DoubleParameter(0.5, 0.0, 1.0, "strength", None)
    )
    b += "Rays" -> new MetaImageOp(classOf[RaysFilter], params)
    params = Seq(
      DoubleParameter(0.5, 0.2, 2.0, "amount", None)
    )
    b += "Saturation" -> new MetaImageOp(classOf[SaturationFilter], params)
  }

  private def registerJHLabsPart3(b: Registry): Unit = {
    var params: Seq[AbstractParameter] = Seq(
      BooleanParameter(false, "shadowOnly", None),
      BooleanParameter(false, "addMargins", None),
      DoubleParameter(0.5, 0.0, 1.0, "opacity", None),
      DoubleParameter(5.0, 0.0, 10.0, "radius", None),
      DoubleParameter(Math.PI * 6 / 4, 0.0, 2 * Math.PI, "angle", None),
      DoubleParameter(5.0, 1.0, 10.0, "distance", None),
      IntegerParameter(0xff220066, 0xff000000, 0xffffffff, "shadowColor", None)
    )
    b += "Shadow" -> new MetaImageOp(classOf[ShadowFilter], params)
    b += "Kaleidoscope" -> createKaleidoscopeOp
    params = Seq(
      IntegerParameter(127, 0, 127, "lowerThreshold", None),
      IntegerParameter(127, 127, 255, "upperThreshold", None)
    )
    b += "Threshold" -> new MetaImageOp(classOf[ThresholdFilter], params)
    params = Seq(
      IntegerParameter(40, 8, 1000, "width", None),
      IntegerParameter(40, 8, 1000, "height", None)
    )
    b += "Scale" -> new MetaImageOp(classOf[ScaleFilter], params)
  }
}

class ProcessingOperators() {

  private val mOps: Map[String, MetaImageOp] = ProcessingOperators.buildRegistry()

  def getOperation(key: String): MetaImageOp = mOps(key)

  /** @return sorted operator names (no AWT dependency; safe in headless CI). */
  def getSortedKeys: Seq[String] = mOps.keySet.toSeq.sorted
}
