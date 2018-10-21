package com.barrybecker4.java2d.spaceinvaders

import java.awt.Graphics
import java.awt.Image


/**
  * A sprite to be displayed on the screen. Note that a sprite
  * contains no state information, i.e. its just the image and
  * not the location. This allows us to use a single sprite in
  * lots of different places without having to store multiple
  * copies of the image.
  * @param image The image that is this sprite
  * @author Kevin Glass
  */
class Sprite(var image: Image) {

  /** @return The width in pixels of this sprite*/
  def getWidth: Int = image.getWidth(null)

  /** @return The height in pixels of this sprite*/
  def getHeight: Int = image.getHeight(null)

  /** Draw the sprite onto the graphics context provided
    * @param g The graphics context on which to draw the sprite
    * @param x The x location at which to draw the sprite
    * @param y The y location at which to draw the sprite
    */
  def draw(g: Graphics, x: Int, y: Int): Unit = {
    g.drawImage(image, x, y, null)
  }
}