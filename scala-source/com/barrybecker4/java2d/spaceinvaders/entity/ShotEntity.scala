package com.barrybecker4.java2d.spaceinvaders.entity

import com.barrybecker4.java2d.spaceinvaders.Game
import ShotEntity.MOVE_SPEED

object ShotEntity {
  /** The vertical speed at which the players shot moves */
  private val MOVE_SPEED = -300
}
/**
  * An entity representing a shot fired by the player's ship
  *
  * @param game The game in which the shot has been created
  * @param sprite The sprite representing this shot
  * @param x      The initial x location of the shot
  * @param y      The initial y location of the shot
  * @author Kevin Glass
  */
class ShotEntity(/** The game in which this entity exists */
                 var game: Game, sprite: String, x: Int, y: Int) extends Entity(sprite, x, y) {


  /** True if this shot has been "used". i.e. it hit something */
  private var used = false

  dy = MOVE_SPEED

  /** Request that this shot moved based on time elapsed
    * @param delta The time that has elapsed since last move
    */
  override def move(delta: Long): Unit = { // proceed with normal move
    super.move(delta)
    // if we shot off the screen, remove ourselves
    if (y < -100) game.removeEntity(this)
  }

  /** Notification that this shot has collided with anotherentity
    * @param other The other entity with which we've collided
    */
  override def collidedWith(other: Entity): Unit = { // prevents double kills, if we've already hit something,
    // don't collide
    if (used) return
    // if we've hit an alien, kill it!
    if (other.isInstanceOf[AlienEntity]) { // remove the affected entities
      game.removeEntity(this)
      game.removeEntity(other)
      // notify the game that the alien has been killed
      game.notifyAlienKilled()
      used = true
    }
  }
}