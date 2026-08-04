/** Copyright by Barry G. Becker, 2011-2026. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.java2d.imageproc

/**
  * Pure 8-bit channel lookup-table builders used by [[ProcessingOperators]] color ops.
  * Kept package-visible so characterization tests can pin the formulas.
  */
private[imageproc] object ColorLookupTables {

  def brighten(i: Int): Short = (128 + i / 2).toShort

  /** Gamma-style brighten via sqrt; callers of exact equality should use a tolerance. */
  def betterBrighten(i: Int): Short = (Math.sqrt(i.toDouble / 255.0) * 255.0).toShort

  def posterize(i: Int): Short = (i - (i % 32)).toShort

  def invert(i: Int): Short = (255 - i).toShort

  def identity(i: Int): Short = i.toShort

  def zero(i: Int): Short = 0.toShort

  def table(f: Int => Short): Array[Short] =
    Array.tabulate(256)(f)

  def rgbWithAlpha(rgb: Array[Short], alpha: Array[Short] = table(identity)): Array[Array[Short]] =
    Array(rgb, rgb, rgb, alpha)
}
