package com.barrybecker4.java2d.spaceinvaders

import scala.compiletime.uninitialized

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
  * This class will act as a manager for the display mediator for the game logic.
  *
  * Display management will consist of a loop that cycles over all entities in the game asking them to move, and then
  * drawing them in the appropriate place. The KeyInputHandler class allows the player to control the main ship.
  *
  * As a mediator it will be informed when entities within our game detect events (e.g. alien killed, player died)
  * and will take appropriate game actions.
  *
  * @author Kevin Glass
  * @author Barry Becker (ported it to Scala)
  */
object SpaceInvadersGame:
  def main(args: Array[String]): Unit =
    new SpaceInvadersGame

class SpaceInvadersGame() extends Canvas {
  val container = new JFrame("Space Invaders 101")

  private var bufStrategy: BufferStrategy = uninitialized
  private var entities: Seq[Entity] = Seq()
  private var removeList: Set[Entity] = Set()
  private var ship: Entity = uninitialized
  private var lastFire: Long = 0
  private var alienCount = 0
  private var message = ""
  private val keyHandler = new KeyInputHandler()
  private var logicRequiredThisLoop = false

  val panel: JPanel = container.getContentPane.asInstanceOf[JPanel]
  panel.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT))
  panel.setLayout(null)
  setBounds(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)
  panel.add(this)
  setIgnoreRepaint(true)
  container.pack()
  container.setResizable(false)
  container.setVisible(true)

  container.addWindowListener(new WindowAdapter() {
    override def windowClosing(e: WindowEvent): Unit = {
      System.exit(0)
    }
  })
  addKeyListener(keyHandler)
  requestFocus()
  createBufferStrategy(2)
  bufStrategy = getBufferStrategy
  initEntities()
  gameLoop()

  private def initEntities(): Unit = {
    ship = new ShipEntity(this, "sprites/ship.gif", 370, SCREEN_HEIGHT - 50)
    val aliens =
      for {
        row <- 0 until ALIEN_ROWS
        x <- 0 until ALIEN_COLS
      } yield new AlienEntity(this, "sprites/alien.gif", 100 + (x * 50), 50 + row * 30)
    entities = Seq(ship) ++ aliens
    alienCount = aliens.size
  }

  def updateLogic(): Unit =
    logicRequiredThisLoop = true

  def removeEntity(entity: Entity): Unit =
    removeList += entity

  def notifyDeath(): Unit = {
    message = "Oh no! They got you, try again?"
    keyHandler.reset()
  }

  /** Notification that the player has won since all the aliens are dead. */
  def notifyWin(): Unit = {
    message = "Well done! You Win!"
    keyHandler.reset()
  }

  def notifyAlienKilled(): Unit = {
    alienCount -= 1
    if (alienCount == 0) notifyWin()
    for (e <- entities)
      e.setHorizontalMovement(e.getHorizontalMovement * SPEEDUP_FACTOR)
  }

  def tryToFire(): Unit = {
    if (System.currentTimeMillis - lastFire < FIRING_INTERVAL) return
    lastFire = System.currentTimeMillis
    val shot = new ShotEntity(this, "sprites/shot.gif", ship.getX + 10, ship.getY - 30)
    entities :+= shot
  }

  def gameLoop(): Unit = {
    var lastLoopTime = System.currentTimeMillis
    while (true) {
      val delta = System.currentTimeMillis - lastLoopTime
      lastLoopTime = System.currentTimeMillis

      if (keyHandler.isStarted)
        initEntities()
      gameStep(delta)
    }
  }

  def gameStep(timeStep: Long): Unit = {
    val g = bufStrategy.getDrawGraphics.asInstanceOf[Graphics2D]
    try {
      clearScene(g)
      moveLivingEntities(timeStep, g)
      runDeferredLogic()
      drawPauseOverlay(g)
    } finally {
      g.dispose()
    }
    bufStrategy.show()
    applyShipAndFireInput()
  }

  private def clearScene(g: Graphics2D): Unit = {
    g.setColor(Color.black)
    g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT)
  }

  private def moveLivingEntities(timeStep: Long, g: Graphics2D): Unit = {
    if (!keyHandler.waitingForKeyPress)
      for (e <- entities) e.move(timeStep)

    for (e <- entities) e.draw(g)

    checkForCollisions()
    entities = entities.filter(e => !removeList.contains(e))
    removeList = Set()
  }

  private def runDeferredLogic(): Unit = {
    if (logicRequiredThisLoop) {
      entities.foreach(e => e.doLogic())
      logicRequiredThisLoop = false
    }
  }

  private def drawPauseOverlay(g: Graphics2D): Unit = {
    if (keyHandler.waitingForKeyPress) {
      g.setColor(Color.white)
      g.drawString(message, (SCREEN_WIDTH - g.getFontMetrics.stringWidth(message)) / 2, 250)
      g.drawString("Press any key", (SCREEN_WIDTH - g.getFontMetrics.stringWidth("Press any key")) / 2, 300)
    }
  }

  private def applyShipAndFireInput(): Unit = {
    ship.setHorizontalMovement(0)
    if (keyHandler.isLeftPressed) ship.setHorizontalMovement(-MOVE_SPEED)
    else if (keyHandler.isRightPressed) ship.setHorizontalMovement(MOVE_SPEED)
    if (keyHandler.isFirePressed) tryToFire()
  }

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
