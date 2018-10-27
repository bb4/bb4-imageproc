package com.barrybecker4.java2d.print

import javax.swing.AbstractAction
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.KeyStroke
import java.awt.Toolkit
import java.awt.event.ActionEvent
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.print.PrinterException
import java.awt.print.PrinterJob


object SwingPrinter {
  def main(args: Array[String]): Unit = {
    new SwingPrinter
  }

  class FileQuitAction() extends AbstractAction("Quit") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      System.exit(0)
    }
  }
}

class SwingPrinter() extends JFrame("SwingPrinter v1.0") {
  createUI()
  val pj: PrinterJob = PrinterJob.getPrinterJob
  private var mPageFormat = pj.defaultPage
  setVisible(true)

  private def createUI(): Unit = {
    setSize(300, 300)
    center()
    // Add the menu bar.
    val mb = new JMenuBar
    val file = new JMenu("File", true)
    file.add(new SwingPrinter#FilePrintAction)
      .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK))
    file.add(new SwingPrinter#FilePageSetupAction)
      .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK))
    file.addSeparator()
    file.add(new SwingPrinter.FileQuitAction)
      .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK))
    mb.add(file)
    setJMenuBar(mb)
    // Add the contents of the window.
    getContentPane.add(new PatchworkComponent)
    // Exit the application when the window is closed.
    addWindowListener(new WindowAdapter() {
      override def windowClosing(e: WindowEvent): Unit = {
        System.exit(0)
      }
    })
  }

  protected def center(): Unit = {
    val screen = Toolkit.getDefaultToolkit.getScreenSize
    val us = getSize
    val x = (screen.width - us.width) / 2
    val y = (screen.height - us.height) / 2
    setLocation(x, y)
  }

  class FilePrintAction() extends AbstractAction("Print") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      val pj = PrinterJob.getPrinterJob
      val cp = new ComponentPrintable(getContentPane)
      pj.setPrintable(cp, mPageFormat)
      if (pj.printDialog) try
        pj.print()
      catch {
        case e: PrinterException =>
          System.out.println(e)
      }
    }
  }

  class FilePageSetupAction() extends AbstractAction("Page setup...") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      val pj = PrinterJob.getPrinterJob
      mPageFormat = pj.pageDialog(mPageFormat)
    }
  }
}