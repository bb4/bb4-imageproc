package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform


object Shear extends App {
  new Shear().getFrame
}

class Shear extends Transformers {
  override def getTransform: AffineTransform = {
    val at = AffineTransform.getTranslateInstance(150, 0)
    at.shear(-.5, 0)
    at
  }
}