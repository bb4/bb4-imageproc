package com.barrybecker4.java2d.examples.transforms

import java.awt.geom.AffineTransform


object Rotation extends App {
  new Rotation().getFrame
}

class Rotation extends Transformers {
  override def getTransform: AffineTransform = AffineTransform.getRotateInstance(-Math.PI / 6)
}