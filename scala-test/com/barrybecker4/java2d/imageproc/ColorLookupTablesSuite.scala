package com.barrybecker4.java2d.imageproc

import org.scalatest.funsuite.AnyFunSuite

class ColorLookupTablesSuite extends AnyFunSuite {

  test("brighten maps endpoints and midpoint with integer halving") {
    assertResult(128.toShort)(ColorLookupTables.brighten(0))
    assertResult(255.toShort)(ColorLookupTables.brighten(255)) // 128 + 255/2 = 128 + 127
    assertResult(192.toShort)(ColorLookupTables.brighten(128)) // 128 + 64
  }

  test("posterize snaps to 32-level bands") {
    assertResult(0.toShort)(ColorLookupTables.posterize(0))
    assertResult(0.toShort)(ColorLookupTables.posterize(31))
    assertResult(32.toShort)(ColorLookupTables.posterize(32))
    assertResult(224.toShort)(ColorLookupTables.posterize(255))
  }

  test("invert and identity/zero are exact complements") {
    assertResult(255.toShort)(ColorLookupTables.invert(0))
    assertResult(0.toShort)(ColorLookupTables.invert(255))
    assertResult(100.toShort)(ColorLookupTables.identity(100))
    assertResult(0.toShort)(ColorLookupTables.zero(200))
  }

  test("betterBrighten matches sqrt formula within float-to-short rounding") {
    def expected(i: Int): Short = (Math.sqrt(i.toDouble / 255.0) * 255.0).toShort
    for i <- 0 until 256 do
      assert(ColorLookupTables.betterBrighten(i) == expected(i), s"mismatch at i=$i")
  }

  test("table builds 256 entries from formula") {
    val t = ColorLookupTables.table(ColorLookupTables.invert)
    assertResult(256)(t.length)
    assertResult(255.toShort)(t(0))
    assertResult(0.toShort)(t(255))
  }

  test("rgbWithAlpha replicates channel and keeps alpha identity by default") {
    val rgb = ColorLookupTables.table(ColorLookupTables.posterize)
    val channels = ColorLookupTables.rgbWithAlpha(rgb)
    assertResult(4)(channels.length)
    assert(channels(0) eq rgb)
    assert(channels(1) eq rgb)
    assert(channels(2) eq rgb)
    assertResult(127.toShort)(channels(3)(127))
  }
}
