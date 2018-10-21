package com.barrybecker4.java2d.spaceinvaders

import java.awt.Canvas
import java.awt.Color
import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferStrategy
import com.barrybecker4.java2d.spaceinvaders.entity.{AlienEntity, Entity, ShipEntity, ShotEntity}
import javax.swing.JFrame
import javax.swing.JPanel
import com.barrybecker4.java2d.spaceinvaders.GameConstants._

/**
  * This class with act as a manager for the display mediator for the game logic.
  *
  * Display management will consist of a loop that cycles over all entities in the game asking them to move, and then
  * drawing them in the appropriate place. The KeyInputHandler class allows the player to control the main ship.
  *
  * As a mediator it will be informed when entities within our game detect events (e.g. alien killed, played died)
  * and will take appropriate game actions.
  *
  * @author Kevin Glass
  * @author Barry Becker (ported it to Scala)
  */
object Game extends App {
  val g = new Game
}

class Game() extends Canvas {
  val container = new JFrame("Space Invaders 101")

  /** The strategy that allows us to use accelerate page flipping */
  private var bufStrategy: BufferStrategy = _
  /** The list of all the entities that exist in our game */
  private var entities: Seq[Entity] = Seq()
  /** The list of entities that need to be removed from the game this loop */
  private var removeList: Set[Entity] = Set()
  /** The entity representing the player */
  private var ship: Entity = _
  /** The time at which last fired a shot */
  private var lastFire: Long = 0
  /** The number of aliens left on the screen */
  private var alienCount = 0
  /** The message to display which waiting for a key press */
  private var message = ""
  /** Handlers user key input */
  private val keyHandler = new KeyInputHandler()
  /** True if game logic needs to be applied this loop, normally as a result of a game event */
  private var logicRequiredThisLoop = false

  // get hold the content of the frame and set up the resolution of the game
  val panel: JPanel = container.getContentPane.asInstanceOf[JPanel]
  panel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT))
  panel.setLayout(null)
  // setup our canvas size and put it into the content of the frame
  setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)
  panel.add(this)
  // Tell AWT not to bother repainting our canvas since we're going to do that ourselves in accelerated mode.
  setIgnoreRepaint(true)
  container.pack()
  container.setResizable(false)
  container.setVisible(true)

  // Add a listener to respond to the user closing the window. If they do we'd like to exit the game.
  container.addWindowListener(new WindowAdapter() {
    override def windowClosing(e: WindowEvent): Unit = {
      System.exit(0)
    }
  })
  // add a key input system (defined below) to our canvas
  // so we can respond to key pressed
  addKeyListener(keyHandler)
  // request the focus so key events come to us
  requestFocus()
  // create the buffering strategy which will allow AWT
  // to manage our accelerated graphics
  createBufferStrategy(2)
  bufStrategy = getBufferStrategy
  initEntities()  // so we see something initially
  gameLoop()  // loop forever


  /** Initialise the starting state of the entities (ship and aliens).
    * EAah entity will be added to the overall list of entities in the game.
    */
  private def initEntities(): Unit = { // create the player ship and place it roughly in the center of the screen
    ship = new ShipEntity(this, "sprites/ship.gif", 370, SCREEN_HEIGHT - 50)
    entities = Seq(ship)
    // create a block of aliens (5 rows, by 12 aliens, spaced evenly)
    alienCount = 0
    var row = 0
    while (row < ALIEN_ROWS) {
      var x = 0
      while (x < ALIEN_COLS) {
        val alien = new AlienEntity(this, "sprites/alien.gif", 100 + (x * 50), 50 + row * 30)
        entities :+= alien
        alienCount += 1
        x += 1
      }
      row += 1
    }
  }

  /** Notification from a game entity that the logic of the game
    * should be run at the next opportunity (normally as a result of some game event)
    */
  def updateLogic(): Unit =
    logicRequiredThisLoop = true

  /** Remove an entity from the game. The entity removed will no longer move or be drawn.
    * @param entity The entity that should be removed
    */
  def removeEntity(entity: Entity): Unit =
    removeList += entity

  /** Notification that the player has died. */
  def notifyDeath(): Unit = {
    message = "Oh no! They got you, try again?"
    keyHandler.reset()
  }

  /** Notification that the player has won since all the aliensare dead. */
  def notifyWin(): Unit = {
    message = "Well done! You Win!"
    keyHandler.reset()
  }

  /** Notification that an alien has been killed */
  def notifyAlienKilled(): Unit = {
    // reduce the alien count, if there are none left, the player has won!
    alienCount -= 1
    if (alienCount == 0) notifyWin()
    // if there are still some aliens left then they all need to get faster, so speed up all the existing aliens
    for(e <- entities)
      e.setHorizontalMovement(e.getHorizontalMovement * SPEEDUP_FACTOR)
  }

  /** Attempt to fire a shot from the player. Its called "try"
    * since we must first check that the player can fire at this
    * point, i.e. has he/she waited long enough between shots
    */
  def tryToFire(): Unit = { // check that we have waiting long enough to fire
    if (System.currentTimeMillis - lastFire < FIRING_INTERVAL) return
    // if we waited long enough, create the shot entity, and record the time.
    lastFire = System.currentTimeMillis
    val shot = new ShotEntity(this, "sprites/shot.gif", ship.getX + 10, ship.getY - 30)
    entities :+= shot
  }

  /** The main game loop. Play is responsible for the following activities:
    * - Working out the speed of the game loop to update moves
    * - Moving the game entities
    * - Drawing the screen contents (entities, text)
    * - Updating game events
    * - Checking Input
    */
  def gameLoop(): Unit = {
    var lastLoopTime = System.currentTimeMillis
    // keep looping round til the game ends
    while (true) {
      // The time delta will be used to calculate how far the entities should move this iteration
      val delta = System.currentTimeMillis - lastLoopTime
      lastLoopTime = System.currentTimeMillis

      if (keyHandler.isStarted)
        initEntities()
      gameStep(delta)
    }
  }

  def gameStep(timeStep: Long): Unit = {

    // Get hold of a graphics context for the accelerated
    // surface and blank it out
    val g = bufStrategy.getDrawGraphics.asInstanceOf[Graphics2D]
    g.setColor(Color.black)
    g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)
    // cycle round asking each entity to move itself
    if (!keyHandler.waitingForKeyPress)
      for (e <- entities) e.move(timeStep)

    for (e <- entities) e.draw(g)

    checkForCollisions()

    // remove any entity that has been marked for clear up
    entities = entities.filter(e => !removeList.contains(e))

    removeList = Set()
    // If a game event has indicated that game logic should be resolved, cycle round every entity requesting that
    // their personal logic should be considered.
    if (logicRequiredThisLoop) {
      entities.foreach(e => e.doLogic())
      logicRequiredThisLoop = false
    }
    // if we're waiting for an "any key" press then draw the current message
    if (keyHandler.waitingForKeyPress) {
      g.setColor(Color.white)
      g.drawString(message, (SCREEN_WIDTH - g.getFontMetrics.stringWidth(message)) / 2, 250)
      g.drawString("Press any key", (SCREEN_WIDTH - g.getFontMetrics.stringWidth("Press any key")) / 2, 300)
    }
    // finally, we've completed drawing so clear up the graphics and flip the buffer over
    g.dispose()
    bufStrategy.show()
    // Resolve the movement of the ship. Move to left or right if needed.
    ship.setHorizontalMovement(0)
    if (keyHandler.isLeftPressed) ship.setHorizontalMovement(-MOVE_SPEED)
    else if (keyHandler.isRightPressed) ship.setHorizontalMovement(MOVE_SPEED)
    // if we're pressing fire, attempt to fire
    if (keyHandler.isFirePressed) tryToFire()
  }

  /** If the bullet or ship collide with any aliens, notify both entities that the collision has occurred. */
  private def checkForCollisions(): Unit = {
    val (aliens, friendlies) = entities.partition(_.isInstanceOf[AlienEntity])

    friendlies.foreach(me => {
      aliens.foreach(him => {
        if (me.collidesWith(him)) {
          me.collidedWith(him)
          him.collidedWith(me)
        }
      })
    })
  }
}