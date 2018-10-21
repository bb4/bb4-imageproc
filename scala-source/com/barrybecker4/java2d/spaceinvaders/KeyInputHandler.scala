package com.barrybecker4.java2d.spaceinvaders

import java.awt.event.{KeyAdapter, KeyEvent}

/**
  * Handles keyboard input from the user.
  * Both dynamic input during game play, i.e. left/right and shoot, and more static type input
  * (i.e. press any key to continue)
  */
private class KeyInputHandler extends KeyAdapter {

  /** True if we're holding up game play until a key has been pressed */
  var waitingForKeyPress = true
  var started = false

  /** True if the left cursor key is currently pressed */
  private var leftPressed = false
  /** True if the right cursor key is currently pressed */
  private var rightPressed = false
  /** True if we are firing */
  private var firePressed = false

  def reset(): Unit = {
    waitingForKeyPress = true
    leftPressed = false
    rightPressed = false
    firePressed = false
  }

  def isStarted: Boolean = {
    started = waitingForKeyPress && !started
    started
  }

  def isLeftPressed: Boolean = leftPressed && !rightPressed
  def isRightPressed: Boolean = rightPressed && !leftPressed
  def isFirePressed: Boolean = firePressed

  override def keyPressed(e: KeyEvent): Unit = update(e.getKeyCode, pressed = true)
  override def keyReleased(e: KeyEvent): Unit = update(e.getKeyCode, pressed = false)

  private def update(keyCode: Int, pressed: Boolean): Unit = {
    if (!waitingForKeyPress) {
      if (keyCode == KeyEvent.VK_LEFT) leftPressed = pressed
      if (keyCode == KeyEvent.VK_RIGHT) rightPressed = pressed
      if (keyCode == KeyEvent.VK_SPACE) firePressed = pressed
    }
  }

  /** Notification from AWT that a key has been typed. Note that
    * typing a key means to both press and then release it.
    * @param e The details of the key that was typed.
    */
  override def keyTyped(e: KeyEvent): Unit = { // if we're waiting for a "any key" type then
    waitingForKeyPress = false
    // if we hit escape, then quit the game
    if (e.getKeyChar == 27) System.exit(0)
  }
}