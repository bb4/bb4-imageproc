package com.barrybecker4.java2d.examples

import com.barrybecker4.java2d.Utilities
import com.barrybecker4.java2d.ui.SplitImageComponent
import com.barrybecker4.ui.application.ApplicationFrame
import java.awt._
import java.awt.image.BandCombineOp
import java.awt.image.BufferedImage
import java.awt.image.Raster
import java.awt.image.WritableRaster


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object Bandito {
  def main(args: Array[String]): Unit = { // Create a frame window to hold everything.
    val f = new ApplicationFrame("Bandito v1.0")
    // Create a SplitImageComponent with the source image.
    val filename = Utilities.DEFAULT_IMAGE_DIR + "EtholWithRoses.small.jpg"
    val sic = new SplitImageComponent(filename)
    // Create a BandCombineOp.
    val matrix = Array(
      Array[Float](-1, 0, 0, 0, 255),
      Array[Float](0, 1, 0, 0, 0),
      Array[Float](0, 0, 1, 0, 0),
      Array[Float](0, 0, 0, 1, 0)
    )
    val op = new BandCombineOp(matrix, null)
    // Process the source image raster.
    val sourceImage = sic.getImage
    val source = sourceImage.getRaster
    val destination = op.filter(source, null)
    // Create a destination image using the processed
    //   raster and the same color model as the source image.
    val destinationImage = new BufferedImage(sourceImage.getColorModel, destination, false, null)
    sic.setSecondImage(destinationImage)
    // Set up the frame window.
    f.getContentPane.setLayout(new BorderLayout)
    f.getContentPane.add(sic, BorderLayout.CENTER)
    f.pack()
  }
}

class Bandito private() {
}