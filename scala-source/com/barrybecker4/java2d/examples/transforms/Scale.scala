package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform

object Scale extends App {
    new Scale().getFrame
}

class Scale extends Transformers {
  override def getTransform: AffineTransform = AffineTransform.getScaleInstance(3, 3)
}