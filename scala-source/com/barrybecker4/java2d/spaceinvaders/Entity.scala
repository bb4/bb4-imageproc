package com.barrybecker4.java2d.spaceinvaders

import java.awt.Graphics
import java.awt.Rectangle


/**
  * An entity represents any element that appears in the game. The
  * entity is responsible for resolving collisions and movement
  * based on a set of properties defined either by subclass or externally.
  *
  * Note that doubles are used for positions. This may seem strange
  * given that pixels locations are integers. However, using double means
  * that an entity can move a partial pixel. It doesn't of course mean that
  * they will be display half way through a pixel but allows us not lose
  * accuracy as we move.
  *
  * @param ref The reference to the image to be displayed for this entity
  * @param x   The initial x location of this entity
  * @param y   The initial y location of this entity
  * @author Kevin Glass
  */
abstract class Entity(val ref: String, var x: Double, var y: Double) {
  this.sprite = SpriteStore.get.getSprite(ref)
  /** The sprite that represents this entity */
  protected var sprite: Sprite = _
  /** The current speed of this entity horizontally (pixels/sec) */
  protected var dx: Double = 0
  /** The current speed of this entity vertically (pixels/sec) */
  protected var dy: Double = 0
  /** The rectangle used for this entity during collisions  resolution */
  private val me = new Rectangle
  /** The rectangle used for other entities during collision resolution */
  private val him = new Rectangle

  /** Request that this entity move itself based on a certain ammount
    * of time passing.
    * @param delta The amount of time that has passed in milliseconds
    */
  def move(delta: Long): Unit = { // update the location of the entity based on move speeds
    x += (delta * dx) / 1000
    y += (delta * dy) / 1000
  }

  /** @param dx The horizontal speed of this entity (pixels/sec) */
  def setHorizontalMovement(dx: Double): Unit =
    this.dx = dx

  /** @param dy The vertical speed of this entity (pixels/sec) */
  def setVerticalMovement(dy: Double): Unit =
    this.dy = dy

  /** @return The horizontal speed of this entity (pixels/sec)*/
  def getHorizontalMovement: Double = dx

  /** @return The vertical speed of this entity (pixels/sec)  */
  def getVerticalMovement: Double = dy

  /** @param g The graphics context on which to draw*/
  def draw(g: Graphics): Unit =
    sprite.draw(g, x.toInt, y.toInt)

  /** Do the logic associated with this entity. This method
    * will be called periodically based on game events.
    */
  def doLogic(): Unit = {}

  def getX: Int = x.toInt
  def getY: Int = y.toInt

  /** Check if this entity collised with another.
    * @param other The other entity to check collision against
    * @return True if the entities collide with each other
    */
  def collidesWith(other: Entity): Boolean = {
    me.setBounds(x.toInt, y.toInt, sprite.getWidth, sprite.getHeight)
    him.setBounds(other.x.toInt, other.y.toInt, other.sprite.getWidth, other.sprite.getHeight)
    me.intersects(him)
  }

  /** Notification that this entity collided with another.
    * @param other The entity with which this entity collided.
    */
  def collidedWith(other: Entity): Unit
}