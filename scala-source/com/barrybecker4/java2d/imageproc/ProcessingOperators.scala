/** Copyright by Barry G. Becker, 2011-2018. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

import java.awt.image.{ConvolveOp, Kernel, LookupOp, ShortLookupTable}
import com.barrybecker4.optimization.parameter.types._
import com.jhlabs.image._


/**
  * A set of  available image processing operations.
  * @author Barry Becker
  */
object ProcessingOperators {

  private def createCausticsOp: MetaImageOp = {
    val params = Seq(
      new DoubleParameter(0.0, 0, 10.0, "time", None),
      new DoubleParameter(32.0, 0.1, 100.0, "scale", None),
      new IntegerParameter(10, 1, 50, "brightness", None),
      new DoubleParameter(0.0, 0.0, 10.0, "turbulence", None),
      new DoubleParameter(0.0, 0.0, 1.0, "dispersion", None),
      new DoubleParameter(1.0, 0.1, 2.0, "amount", None)
    )
    new MetaImageOp(classOf[CausticsFilter], params)
  }

  private def createCellularOp: MetaImageOp = {
    val specValues: Array[Int] = Array(CellularFilter.GridType.RANDOM.ordinal)
    val specValueProbs: Array[Double] = Array(0.6)

    val params = Seq(
      new BooleanParameter(true, "useColor", None),
      IntegerParameter.createDiscreteParameter(CellularFilter.GridType.RANDOM.ordinal,
      0, CellularFilter.GridType.values.length, "gridType", specValues, specValueProbs),
      new IntegerParameter(1, 1, 20, "turbulence", None),
      new DoubleParameter(0.0, 0.0, 1.0, "F1", None),
      new DoubleParameter(0.0, 0.0, 1.0, "F2", None),
      new DoubleParameter(0.0, 0.0, 1.0, "randomness", None),
      new DoubleParameter(.5, 0.0, 1.0, "amount", None),
      new DoubleParameter(1.0, 0.0, 2.0, "gradientCoefficient", None),
      new DoubleParameter(1.0, 1.0, 30.0, "stretch", None),
      new DoubleParameter(0.0, 0.0, Math.PI, "angle", None), // in radians
      new DoubleParameter(1.0, 0.0, 5.0, "angleCoefficient", None),
      new IntegerParameter(1, 1, 6, "distancePower", None),
      new DoubleParameter(16.0, 0.1, 64.0, "scale", None)
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
      new DoubleParameter(0.8, 0.1, 3.0, "amount", None),
      new DoubleParameter(32, 4, 128, "scale", None),
      DoubleParameter.createUniformParameter(1.0, 1.0, 8.0, "stretch", sv, svp),
      DoubleParameter.createUniformParameter(0.0, 0.0, Math.PI, "angle", sv, svp),
      new DoubleParameter(1.0, 0.0, 5.0, "H", None),
      new DoubleParameter(2.0, 0.1, 4.0, "lacunarity", None),
      new DoubleParameter(0.5, 0.1, 2.0, "gain", None),
      DoubleParameter.createGaussianParameter(0.5,
      0.0, 2.0, "bias", 0.24, 0.2),
      new DoubleParameter(4.0, 0.1, 16.0, "octaves", None)
    )
    new MetaImageOp(classOf[FBMFilter], params)
  }

  private def createContourOp: MetaImageOp = {
    val params = Seq(
      new DoubleParameter(5.0, 0.1, 10.0, "levels", None),
      new DoubleParameter(1.0, 0.1, 10.0, "scale", None),
      new DoubleParameter(0.0, 0.0, 2.0, "offset", None),
      new IntegerParameter(0xff2200aa,
      0xff000000, 0xffffffff, "contourColor", None)
    )
    new MetaImageOp(classOf[ContourFilter], params)
  }

  private def createKaleidoscopeOp: MetaImageOp = {
    val sv: Array[Double] = Array(0.0)
    val svp: Array[Double] = Array(0.3)

    val params = Seq(
      new IntegerParameter(3, 1, 6, "sides", None),
      DoubleParameter.createUniformParameter(0.0, 0.0, 500.0, "radius", sv, svp),
      new DoubleParameter(0, 0.0, 2 * Math.PI, "angle", None),
      new DoubleParameter(0, 0.0, Math.PI, "angle2", None),
      DoubleParameter.createGaussianParameter(0.5, 0.1, 0.9, "centreX", 0.5, .2),
      DoubleParameter.createGaussianParameter(0.5, 0.1, 0.9, "centreY", 0.5, 0.2)
    )
    new MetaImageOp(classOf[KaleidoscopeFilter], params)
  }
}

class ProcessingOperators() {
  createOps()
  private var mOps: Map[String, MetaImageOp] = _

  def getOperation(key: String): MetaImageOp = mOps(key)

  /*** @return a sorted list of the operators. */
  def getSortedKeys(): java.awt.List = {
    val list = new java.awt.List()
    for (item <- mOps.keySet.toSeq.sorted)
      list.add(item)
    list
  }

  private def createOps(): Unit = {
    mOps = Map[String, MetaImageOp]()
    createConvolutions()
    createColorOps()
    createJHLabsOps()
  }

  private def createConvolutions(): Unit = {
    val ninth: Float = 1.0f / 9.0f
    val blurKernel: Array[Float] = Array(ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth)
    mOps += "Blur" -> new MetaImageOp(new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, null))
    val edge: Array[Float] = Array(0f, -0.8f, 0f, -0.8f, 4.0f, -0.8f, 0f, -0.8f, 0f)
    mOps += "Edge detector" -> new MetaImageOp(new ConvolveOp(new Kernel(3, 3, edge), ConvolveOp.EDGE_NO_OP, null))
    val sharp: Array[Float] = Array(0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f)
    mOps += "Sharpen" -> new MetaImageOp(new ConvolveOp(new Kernel(3, 3, sharp)))
  }

  private def createColorOps(): Unit = {
    mOps += "Grayscale" -> new MetaImageOp(new GrayscaleFilter)
    val brighten: Array[Short] = new Array[Short](256)
    val betterBrighten: Array[Short] = new Array[Short](256)
    val posterize: Array[Short] = new Array[Short](256)
    val invert: Array[Short] = new Array[Short](256)
    val straight: Array[Short] = new Array[Short](256)
    val zero: Array[Short] = new Array[Short](256)
    var i: Int = 0
    while (i < 256) {
      brighten(i) = (128 + i / 2).toShort
      betterBrighten(i) = (Math.sqrt(i.toDouble / 255.0) * 255.0).toShort
      posterize(i) = (i - (i % 32)).toShort
      invert(i) = (255 - i).toShort
      straight(i) = i.toShort
      zero(i) = 0.toShort
        i += 1
    }
    val brightenTable: Array[Array[Short]] = Array(brighten, brighten, brighten, straight)
    val betterBrightenTable: Array[Array[Short]] = Array(betterBrighten, betterBrighten, betterBrighten, straight)
    val posterizeTable: Array[Array[Short]] = Array(posterize, posterize, posterize, straight)
    val invertTable: Array[Array[Short]] = Array(invert, invert, invert, straight)
    mOps += "Brighten" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, brightenTable), null))
    mOps += "Better Brighten" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, betterBrightenTable), null))
    mOps += "Posterize" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, posterizeTable), null))
    mOps += "Invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, invertTable), null))
    val redOnly: Array[Array[Short]] = Array(invert, straight, straight, straight)
    val greenOnly: Array[Array[Short]] = Array(straight, invert, straight, straight)
    val blueOnly: Array[Array[Short]] = Array(straight, straight, invert, straight)
    mOps += "Red invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, redOnly), null))
    mOps += "Green invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, greenOnly), null))
    mOps += "Blue invert" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, blueOnly), null))
    // did not have 4th arg initially
    val redRemove: Array[Array[Short]] = Array(zero, straight, straight, straight)
    val greenRemove: Array[Array[Short]] = Array(straight, zero, straight, straight)
    val blueRemove: Array[Array[Short]] = Array(straight, straight, zero, straight)
    mOps += "Red remove" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, redRemove), null))
    mOps += "Green remove" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, greenRemove), null))
    mOps += "Blue remove" -> new MetaImageOp(new LookupOp(new ShortLookupTable(0, blueRemove), null))
  }

  private def createJHLabsOps(): Unit = {

    mOps += "Caustics" -> ProcessingOperators.createCausticsOp
    var params: Seq[AbstractParameter] = Seq(
      DoubleParameter.createGaussianParameter(1.0, 0.2, 1.8,
        "height", 0.5, 0.2)
    )
    mOps += "Bumps" -> new MetaImageOp(classOf[BumpFilter], params)
    mOps += "Cellular" -> ProcessingOperators.createCellularOp
    mOps += "Contour" -> ProcessingOperators.createContourOp
    params = Seq(
      new BooleanParameter(false, "fadeEdges", None),
      new DoubleParameter(0.4, 0.1, 2.0, "edgeThickness", None),
      new IntegerParameter(0xff2200aa, 0xff000000, 0xffffffff, "edgeColor", None)
    )
    mOps += "Crystallize" -> new MetaImageOp(classOf[CrystallizeFilter], params)
    params = Seq(
      new BooleanParameter(true, "emboss", None),
      new DoubleParameter(2.0, 0.0, Math.PI, "azimuth", None),
      new DoubleParameter(0.4, 0.0, Math.PI / 2.0, "elevation", None),
      new DoubleParameter(0.5, 0.1, 2.5, "bumpHeight", None)
    )
    mOps += "Emboss" -> new MetaImageOp(classOf[EmbossFilter], params)
    mOps += "Equalize" -> new MetaImageOp(new EqualizeFilter)
    mOps += "Fractal Noise" -> ProcessingOperators.createFractalOp
    params = Seq(
      new BooleanParameter(true, "useImageColors", None),
      new DoubleParameter(0.9, 0.01, 2.0, "turbulence", None),
      new DoubleParameter(1.0, 0.01, 3.0, "scaling", None),
      new BooleanParameter(true, "useImageColors", None)
    )
    mOps += "Plasma" -> new MetaImageOp(classOf[PlasmaFilter], params)
    params = Seq(
      StringParameter(PolarFilter.PolarMappingType.RECT_TO_POLAR.ordinal,
        PolarFilter.PolarMappingType.values.map(_.toString).toIndexedSeq, "type", None),
      StringParameter(EdgeAction.WRAP.ordinal, EdgeAction.values.map(_.toString).toIndexedSeq, "edgeAction", None)
    )
    mOps += "Polar" -> new MetaImageOp(classOf[PolarFilter], params)
    params = Seq(
      StringParameter(RippleFilter.RippleType.SINE.ordinal,
        RippleFilter.RippleType.values.map(_.toString).toIndexedSeq, "waveType", None),
      new DoubleParameter(5.0, 0.0, 10.0, "xAmplitude", None),
      new DoubleParameter(0.0, 0.0, 10.0, "yAmplitude", None),
      new DoubleParameter(16, 1, 64, "xWavelength", None),
      new DoubleParameter(16, 1, 64, "yWavelength", None)
    )
    mOps += "Ripple" -> new MetaImageOp(classOf[RippleFilter], params)
    params = Seq(
      StringParameter(EdgeAction.WRAP.ordinal, EdgeAction.values.map(_.toString).toIndexedSeq, "edgeAction", None),
      new DoubleParameter(2.0, 0.5, 6.0, "scale", None)
    )
    mOps += "Diffuse" -> new MetaImageOp(classOf[DiffuseFilter], params)
    params = Seq(
      new DoubleParameter(1.0, 0.1, 5.0, "redGamma", None),
      new DoubleParameter(1.0, 0.1, 5.0, "greenGamma", None),
      new DoubleParameter(1.0, 0.1, 5.0, "blueGamma", None)
    )
    mOps += "Gamma" -> new MetaImageOp(classOf[GammaFilter], params)
    params = Seq(
      StringParameter(LightFilter.BumpShapeType.NONE.ordinal,
        LightFilter.BumpShapeType.values.map(_.toString).toIndexedSeq, "bumpShape", None),
      new DoubleParameter(.5, 0.1, 2.0, "bumpHeight", None),
      new DoubleParameter(0.0, 0.0, 3.0, "bumpSoftness", None),
      new DoubleParameter(10000.0, 10.0, 10000.0, "viewDistance", None)
    )
    mOps += "Light" -> new MetaImageOp(classOf[LightFilter], params)
    params = Seq(
      new DoubleParameter(1.0, 0.8, 5.0, "amount", None),
      new DoubleParameter(1.0, 0.5, 16.0, "turbulence", None),
      new DoubleParameter(6.0, 1.0, 100.0, "xScale", None),
      new DoubleParameter(6.0, 1.0, 100.0, "yScale", None)
    )
    mOps += "Marble" -> new MetaImageOp(classOf[MarbleFilter], params)
    params = Seq(
      new DoubleParameter(1.0, 0.5, 10.0, "turbulence", None),
      new DoubleParameter(0.5, 0.1, 5.0, "turbulenceFactor", None),
      new DoubleParameter(32.0, 8.0, 128.0, "scale", None),
      new DoubleParameter(0.0, 0.0, Math.PI, "angle", None),
      new DoubleParameter(1.0, 0.5, 10.0, "stretch", None),
      new DoubleParameter(1.0, 0.5, 6.0, "brightness", None)
    )
    mOps += "MarbleTexture" -> new MetaImageOp(classOf[MarbleTexFilter], params)
    params = Seq(
      new BooleanParameter(true, "useOpacity", None),
      new DoubleParameter(1.0, 0.1, 1.0, "opacity", None),
      new DoubleParameter(0.5, 0.4, 0.9, "centreY", None)
    )
    mOps += "Mirror" -> new MetaImageOp(classOf[MirrorFilter], params)
    params = Seq(
      new BooleanParameter(false, "raysOnly", None),
      new DoubleParameter(0.5, 0.1, 1.0, "opacity", None),
      new DoubleParameter(0.5, 0.1, 1.0, "threshold", None),
      new DoubleParameter(0.5, 0.0, 1.0, "strength", None)
    )
    mOps += "Rays" -> new MetaImageOp(classOf[RaysFilter], params)
    params = Seq(
      new DoubleParameter(0.5, 0.2, 2.0, "amount", None)
    )
    mOps += "Saturation" -> new MetaImageOp(classOf[SaturationFilter], params)
    params = Seq(
      new BooleanParameter(false, "shadowOnly", None),
      new BooleanParameter(false, "addMargins", None),
      new DoubleParameter(0.5, 0.0, 1.0, "opacity", None),
      new DoubleParameter(5.0, 0.0, 10.0, "radius", None),
      new DoubleParameter(Math.PI * 6 / 4, 0.0, 2 * Math.PI, "angle", None),
      new DoubleParameter(5.0, 1.0, 10.0, "distance", None),
      new IntegerParameter(0xff220066, 0xff000000, 0xffffffff, "shadowColor", None)
    )
    mOps += "Shadow" -> new MetaImageOp(classOf[ShadowFilter], params)
    mOps += "Kaleidoscope" -> ProcessingOperators.createKaleidoscopeOp
    params = Seq(
      new IntegerParameter(127, 0, 127, "lowerThreshold", None),
      new IntegerParameter(127, 127, 255, "upperThreshold", None)
    )
    mOps += "Threshold" -> new MetaImageOp(classOf[ThresholdFilter], params)
    params = Seq(
      new IntegerParameter(40, 8, 1000, "width", None),
      new IntegerParameter(40, 8, 1000, "height", None)
    )
    mOps += "Scale" -> new MetaImageOp(classOf[ScaleFilter], params)
    /*
            mOps.put("Shine", new ShineFilter());
            mOps.put("Gain", new GainFilter());
            mOps.put("Glint", new GlintFilter());
            mOps.put("Glow", new GlowFilter());
            mOps.put("Lens Blur", new LensBlurFilter());
            SwimFilter swimFilter = new SwimFilter();
            swimFilter.setAmount(2.0f);
            mOps.put("Swim", swimFilter);
            WaterFilter waterDrop = new WaterFilter();
            waterDrop.setCentreX((float)Math.random());
            waterDrop.setCentreY((float)Math.random());
            waterDrop.setRadius(10.0f + (float)(100.0 * Math.random()));
            mOps.put("Water Drop", waterDrop);
            mOps.put("Median", new MedianFilter());

            //  float distance, float angle, float rotation, float zoom
            mOps.put("Motion blur", new MotionBlurFilter(2.0f, 3.0f, 0.0f, 0.0f));
            mOps.put("Pointillize", new PointillizeFilter());
            mOps.put("Quantize", new QuantizeFilter());

            mOps.put("Blur (smart)", new SmartBlurFilter());
            mOps.put("Smear", new SmearFilter());
            mOps.put("Sparkle", new SparkleFilter());
            mOps.put("Chrome", new ChromeFilter());

            TwirlFilter twirl = new TwirlFilter();
            twirl.setAngle(1.0f);
            twirl.setRadius(200.0f);
            mOps.put("Twirl", twirl);

            // secondary
            mOps.put("Weave", new WeaveFilter());
            mOps.put("Wood", new WoodFilter());
            mOps.put("Life", new LifeFilter());
             *//* tricky
            params = new ArrayList<>();
            float x[] = {0f, 0.1f, 0.8f, 1f};
            float y[] = {0f, 0.01f, .95f, 1f};
            CurvesFilter.Curve c =
                    new CurvesFilter.Curve(x, y);
            curvesFilter.setCurve(c);
            mOps.put("Curves", new MetaImageOp(CurvesFilter.class, params));
            params = new ArrayList<>();
            mOps.put("Diffusion", new MetaImageOp(DiffusionFilter.class, params));

             //mOps.put("JavaLnf", new JavaLnFFilter());
             //mOps.put("Shatter", new ShatterFilter());
             //mOps.put("Sky", new SkyFilter());   // npe
             //mOps.put("Scratch", new ScratchFilter());   white
             //mOps.put("Shade", new ShadeFilter());
             //mOps.put("Field Warp", new FieldWarpFilter());
             // mOps.put("Skeleton", new SkeletonFilter());
           */
  }
}