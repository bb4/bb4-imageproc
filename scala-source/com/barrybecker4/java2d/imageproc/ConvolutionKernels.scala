/** Copyright by Barry G. Becker, 2011-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

/**
  * Fixed convolution kernels used by [[ProcessingOperators]] blur / edge / sharpen ops.
  */
private[imageproc] object ConvolutionKernels {

  private val ninth: Float = 1.0f / 9.0f

  val blur: Array[Float] =
    Array(ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth, ninth)

  val edge: Array[Float] =
    Array(0f, -0.8f, 0f, -0.8f, 4.0f, -0.8f, 0f, -0.8f, 0f)

  val sharpen: Array[Float] =
    Array(0f, -1f, 0f, -1f, 5f, -1f, 0f, -1f, 0f)
}
