package com.barrybecker4.java2d.spaceinvaders

import java.awt.event.{KeyAdapter, KeyEvent}

/** A class to handle keyboard input from the user. The class
  * handles both dynamic input during game play, i.e. left/right
  * and shoot, and more static type input (i.e. press any key to
  * continue)
  *
  * This has been implemented as an inner class more through
  * habbit then anything else. Its perfectly normal to implement
  * this as separate class if slight less convenient.
  */
private class KeyInputHandler extends KeyAdapter {
  /** The number of key presses we've had while waiting for an "any key" press */
  var pressCount = 0
  /** True if we're holding up game play until a key has been pressed */
  var waitingForKeyPress = true

  /** True if the left cursor key is currently pressed */
  private var leftPressed = false
  /** True if the right cursor key is currently pressed */
  private var rightPressed = false
  /** True if we are firing */
  private var firePressed = false

  def reset(): Unit = {
    leftPressed = false
    rightPressed = false
    firePressed = false
  }

  def isLeftPressed: Boolean = leftPressed && !rightPressed
  def isRightPressed: Boolean = rightPressed && !leftPressed
  def isFirePressed: Boolean = firePressed
  def keyPressedToStart: Boolean = {
    waitingForKeyPress && pressCount == 1
  }

  /** Notification from AWT that a key has been pressed. Note that
    * a key being pressed is equal to being pushed down but *NOT*
    * released. Thats where keyTyped() comes in.
    * @param e The details of the key that was pressed
    */
  override def keyPressed(e: KeyEvent): Unit = { // if we're waiting for an "any key" typed then we don't
    // want to do anything with just a "press"
    if (waitingForKeyPress) return
    if (e.getKeyCode == KeyEvent.VK_LEFT) leftPressed = true
    if (e.getKeyCode == KeyEvent.VK_RIGHT) rightPressed = true
    if (e.getKeyCode == KeyEvent.VK_SPACE) firePressed = true
  }

  /** Notification from AWT that a key has been released.
    * @param e The details of the key that was released
    */
  override def keyReleased(e: KeyEvent): Unit = { // want to do anything with just a "released"
    if (waitingForKeyPress) return
    if (e.getKeyCode == KeyEvent.VK_LEFT) leftPressed = false
    if (e.getKeyCode == KeyEvent.VK_RIGHT) rightPressed = false
    if (e.getKeyCode == KeyEvent.VK_SPACE) firePressed = false
  }

  /** Notification from AWT that a key has been typed. Note that
    * typing a key means to both press and then release it.
    * @param e The details of the key that was typed.
    */
  override def keyTyped(e: KeyEvent): Unit = { // if we're waiting for a "any key" type then

    // Check if we've received any recently.
    // We may have had a keyType() event from the user releasing the shoot or move keys,
    // hence the use of the "pressCount" counter.
    pressCount += 1

    // if we hit escape, then quit the game
    if (e.getKeyChar == 27) System.exit(0)
  }
}