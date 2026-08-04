package com.barrybecker4.java2d.imageproc

import org.scalatest.funsuite.AnyFunSuite

class ConvolutionKernelsSuite extends AnyFunSuite {

  test("blur kernel is 3x3 uniform ninths") {
    assertResult(9)(ConvolutionKernels.blur.length)
    val ninth = 1.0f / 9.0f
    ConvolutionKernels.blur.foreach(v => assert(math.abs(v - ninth) < 1e-7f))
  }

  test("edge and sharpen kernels keep known center weights") {
    assertResult(4.0f)(ConvolutionKernels.edge(4))
    assertResult(5.0f)(ConvolutionKernels.sharpen(4))
    assertResult(0f)(ConvolutionKernels.edge(0))
    assertResult(-1f)(ConvolutionKernels.sharpen(1))
  }

  test("all kernels are length 9") {
    assertResult(9)(ConvolutionKernels.edge.length)
    assertResult(9)(ConvolutionKernels.sharpen.length)
  }
}
