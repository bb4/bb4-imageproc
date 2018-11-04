package com.barrybecker4.java2d.spaceinvaders.entity

import com.barrybecker4.java2d.spaceinvaders.{SpaceInvadersGame, GameConstants}
import GameConstants._


/**
  * An entity which represents one of our space invader aliens.
  *
  * @param game The game in which this entity is being created
  * @param ref  The sprite which should be displayed for this alien
  * @author Kevin Glass
  */
class AlienEntity(var game: SpaceInvadersGame, ref: String, initialX: Double, initialY: Double)
  extends Entity(ref, initialX, initialY) {

  /** The speed at which the alien moves horizontally */
  private val moveSpeed = 75
  dx = -moveSpeed

  /** Request that this alien moved based on time elapsed
    * @param delta The time that has elapsed since last move
    */
  override def move(delta: Long): Unit = { // if we have reached the left hand side of the screen and
    // are moving left or right then request a logic update
    if ((dx < 0) && (x < 10)) game.updateLogic()
    if ((dx > 0) && (x > SCREEN_WIDTH - 50)) game.updateLogic()
    // proceed with normal move
    super.move(delta)
  }

  /** Update the game logic related to aliens */
  override def doLogic(): Unit = { // swap over horizontal movement and move down the
    // screen a bit
    dx = -dx
    y += 10.0
    // if we've reached the bottom of the screen then the player dies
    if (y > SCREEN_HEIGHT - 30) game.notifyDeath()
  }

  /** Notification that this alien has collided with another entity
    * @param other The other entity
    */
  override  def collidedWith(other: Entity): Unit = {
    // collisions with aliens are handled elsewhere
  }
}