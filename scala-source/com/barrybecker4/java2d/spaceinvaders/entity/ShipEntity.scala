package com.barrybecker4.java2d.spaceinvaders.entity

import com.barrybecker4.java2d.spaceinvaders.SpaceInvadersGame
import com.barrybecker4.java2d.spaceinvaders.GameConstants._

/**
  * The entity that represents the players ship
  *
  * @param game The game in which the ship is being created
  * @param ref  The reference to the sprite to show for the ship
  * @param x    The initial x location of the player's ship
  * @param y    The initial y location of the player's ship
  * @author Kevin Glass
  */
class ShipEntity(var game: SpaceInvadersGame, ref: String, x: Int, y: Int) extends Entity(ref, x, y) {

  /** Request that the ship move itself based on an elapsed amount of time
    * @param delta The time that has elapsed since last move (ms)
    */
  override def move(delta: Long): Unit = { // if we're moving left and have reached the left hand side
    // of the screen, don't move
    if ((dx < 0) && (x < 10)) return
    // if we're moving right and have reached the right hand side
    if ((dx > 0) && (x > SCREEN_WIDTH - 50)) return
    super.move(delta)
  }

  /** Notification that the player's ship has collided with something
    * if its an alien, notify the game that the player is dead
    * @param other The entity with which the ship has collided
    */
  override def collidedWith(other: Entity): Unit =
    if (other.isInstanceOf[AlienEntity]) game.notifyDeath()
}