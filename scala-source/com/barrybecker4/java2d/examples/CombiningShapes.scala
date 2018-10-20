package com.barrybecker4.java2d.examples

import com.barrybecker4.ui.application.ApplicationFrame
import javax.swing._
import java.awt._
import java.awt.event.ItemEvent
import java.awt.geom.Area
import java.awt.geom.Ellipse2D
import java.awt.geom.Rectangle2D


/**
  * Derived from code accompanying "Java 2D Graphics" by Jonathan Knudsen.
  */
object CombiningShapes {
  def main(args: Array[String]): Unit = {
    val f = new ApplicationFrame("CombiningShapes v1.0")
    f.getContentPane.add(new CombiningShapes)
    f.setSize(220, 220)
    f.center()
    f.setVisible(true)
  }
}

class CombiningShapes() extends JComponent { // Create the two shapes, a circle and a square.
  private var mShapeOne = new Ellipse2D.Double(40, 20, 80, 80)
  private var mShapeTwo = new Rectangle2D.Double(60, 40, 80, 80)
  setBackground(Color.white)
  setLayout(new BorderLayout)
  // Create a panel to hold the combo box.
  val controls = new JPanel
  // Create the combo box with the names of the area operators.
  private var mOptions = new JComboBox[String](
    Array[String]("outline", "add", "intersection", "subtract", "exclusive or")
  )
  // Repaint ourselves when the selection changes.
  mOptions.addItemListener((ie: ItemEvent) => repaint())
  controls.add(mOptions)
  add(controls, BorderLayout.SOUTH)


  override def paintComponent(g: Graphics): Unit = {
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    // Retrieve the selection option from the combo box.
    val option = mOptions.getSelectedItem.asInstanceOf[String]
    if ("outline" == option) { // Just draw the outlines and return.
      g2.draw(mShapeOne)
      g2.draw(mShapeTwo)
      return
    }
    // Create Areas from the shapes.
    val areaOne = new Area(mShapeOne)
    val areaTwo = new Area(mShapeTwo)
    // Combine the Areas according to the selected option.
    option match {
      case "add" => areaOne.add(areaTwo)
      case "intersection" => areaOne.intersect(areaTwo)
      case "subtract" => areaOne.subtract(areaTwo)
      case "exclusive or" => areaOne.exclusiveOr(areaTwo)
    }

    // Fill the resulting Area.
    g2.setPaint(Color.orange)
    g2.fill(areaOne)
    // Draw the outline of the resulting Area.
    g2.setPaint(Color.black)
    g2.draw(areaOne)
  }
}