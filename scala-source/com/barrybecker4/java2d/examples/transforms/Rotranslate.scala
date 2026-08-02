package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform


object Rotranslate {
  def main(args: Array[String]): Unit = {
    new Rotranslate().getFrame
  }
}

class Rotranslate extends Transformers {
  override def getTransform: AffineTransform = {
    val at = new AffineTransform
    at.setToRotation(Math.PI / 6)
    at.translate(100, 0)
    at
  }
}