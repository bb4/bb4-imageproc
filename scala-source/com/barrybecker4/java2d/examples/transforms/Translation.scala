package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform


object Translation extends App {
  new Translation().getFrame
}

class Translation extends Transformers {
  override def getTransform: AffineTransform = AffineTransform.getTranslateInstance(150, 0)
}