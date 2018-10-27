package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform


object Transrotate extends App {
  new Transrotate().getFrame
}

class Transrotate extends Transformers {
  override def getTransform: AffineTransform = {
    val at = new AffineTransform
    at.setToTranslation(100, 0)
    at.rotate(Math.PI / 6)
    at
  }
}