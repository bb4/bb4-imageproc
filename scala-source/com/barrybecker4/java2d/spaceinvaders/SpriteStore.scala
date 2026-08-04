package com.barrybecker4.java2d.spaceinvaders

import java.awt.GraphicsEnvironment
import java.awt.Transparency
import java.awt.image.BufferedImage
import java.io.IOException
import javax.imageio.ImageIO


/**
  * A resource manager for sprites in the game. Its often quite important
  * how and where you get your game resources from. In most cases
  * it makes sense to have a central resource loader that goes away, gets
  * your resources and caches them for future use.
  * @author Kevin Glass
  */
object SpriteStore {

  /** The cached sprite map, from reference to sprite instance */
  private var sprites: Map[String, Sprite] = Map()

  /** Retrieve a sprite from the store
    * @param ref The reference to the image to use for the sprite
    * @return A sprite instance containing an accelerate image of the request reference
    */
  def getSprite(ref: String): Sprite =
    sprites.getOrElse(ref, {
      val sourceImage = loadSourceImage(ref)
      val gc = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDefaultConfiguration
      val image = gc.createCompatibleImage(sourceImage.getWidth, sourceImage.getHeight, Transparency.BITMASK)
      image.getGraphics.drawImage(sourceImage, 0, 0, null)
      val sprite = new Sprite(image)
      sprites += ref -> sprite
      sprite
    })

  private def loadSourceImage(ref: String): BufferedImage = {
    try {
      val url = this.getClass.getResource(ref)
      if (url == null) fail("Can't find ref: " + ref)
      val sourceImage = ImageIO.read(url)
      if (sourceImage == null) fail("Failed to load: " + ref)
      sourceImage
    } catch {
      case _: IOException => fail("Failed to load: " + ref)
    }
  }

  /** Utility method to handle resource loading failure
    * @param message The message to display on failure
    */
  private def fail(message: String): Nothing = {
    System.err.println(message)
    System.exit(0)
    throw new IllegalStateException(message) // unreachable; System.exit does not return
  }
}
