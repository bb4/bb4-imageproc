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
  def getSprite(ref: String): Sprite = { // if we've already got the sprite in the cache
    // then just return the existing version
    if (sprites.contains(ref)) return sprites(ref)
    // otherwise, go away and grab the sprite from the resource
    // loader
    var sourceImage: BufferedImage = null
    try {
      // The ClassLoader.getResource() ensures we get the sprite from the appropriate place.
      // This helps with deploying the game with things like webstart. You could also do a file look up here.
      val url = this.getClass.getResource(ref)
      if (url == null) fail("Can't find ref: " + ref)
      // use ImageIO to read the image in
      sourceImage = ImageIO.read(url)
    } catch {
      case e: IOException =>
        fail("Failed to load: " + ref)
    }
    // create an accelerated image of the right size to store our sprite in
    val gc = GraphicsEnvironment.getLocalGraphicsEnvironment.getDefaultScreenDevice.getDefaultConfiguration
    val image = gc.createCompatibleImage(sourceImage.getWidth, sourceImage.getHeight, Transparency.BITMASK)
    // draw our source image into the accelerated image
    image.getGraphics.drawImage(sourceImage, 0, 0, null)
    // create a sprite, add it the cache then return it
    val sprite = new Sprite(image)
    sprites += ref -> sprite
    sprite
  }

  /** Utility method to handle resource loading failure
    * @param message The message to display on failure
    */
  private def fail(message: String): Unit = { // we're pretty dramatic here, if a resource isn't available
    // we dump the message and exit the game
    System.err.println(message)
    System.exit(0)
  }
}