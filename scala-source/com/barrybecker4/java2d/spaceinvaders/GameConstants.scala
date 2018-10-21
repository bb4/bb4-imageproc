package com.barrybecker4.java2d.spaceinvaders

object GameConstants {

  val SCREEN_WIDTH = 800
  val SCREEN_HEIGHT = 600

  /** The speed at which the player's ship should move (pixels/sec) */
  val MOVE_SPEED = 300

  /** The interval between our players shot (ms) */
  val FIRING_INTERVAL = 500

  /** The amount to speed the aliens everytime one of their number is killed */
  val SPEEDUP_FACTOR = 1.05

}
