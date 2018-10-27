package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform

object TranslatedRotation extends App {
    new TranslatedRotation().getFrame
}

class TranslatedRotation extends Transformers {
  override def getTransform: AffineTransform = {
    val cm = 72 / 2.54f
    AffineTransform.getRotateInstance(-Math.PI / 6, 3 * cm, 2 * cm)
  }
}