package com.barrybecker4.java2d.imageproc

import org.scalatest.funsuite.AnyFunSuite
import com.barrybecker4.common.testsupport.strip


class ProcessingOperatorsSuite extends AnyFunSuite {

  private val ops = new ProcessingOperators()

  test("initialize ops") {
    assertResult(
      strip("""Better Brighten, Blue invert,
        | Blue remove, Blur, Brighten, Bumps, Caustics, Cellular, Contour, Crystallize, Diffuse,
        | Edge detector, Emboss, Equalize, Fractal Noise, Gamma, Grayscale, Green invert, Green remove,
        | Invert, Kaleidoscope, Light, Marble, MarbleTexture, Mirror, Plasma, Polar, Posterize, Rays,
        | Red invert, Red remove, Ripple, Saturation, Scale, Shadow, Sharpen, Threshold""", "")) {
      ops.getSortedKeys.getItems.mkString(", ")
    }
  }

  test("get specific op (when invalid)") {
    assertThrows[NoSuchElementException] { ops.getOperation("foo")}
  }

  test("get specific op (when valid)") {
    assertResult("Stylize/Drop Shadow...") { ops.getOperation("Shadow").getInstance.toString }
  }

  test("spot-check dynamic ops are registered with parameters") {
    assert(ops.getOperation("Blur").getBaseParameters.isEmpty)
    assert(ops.getOperation("Plasma").getBaseParameters.nonEmpty)
    assert(ops.getOperation("Cellular").getBaseParameters.size > 5)
    assert(ops.getOperation("Marble").getBaseParameters.nonEmpty)
  }
}
