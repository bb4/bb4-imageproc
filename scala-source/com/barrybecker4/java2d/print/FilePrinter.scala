package com.barrybecker4.java2d.print

import javax.swing._
import java.awt._
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.print.PrinterException
import java.awt.print.PrinterJob
import java.io.IOException


object FilePrinter {
  def main(args: Array[String]): Unit = {
    new FilePrinter
  }

  class FileQuitAction() extends AbstractAction("Quit") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      System.exit(0)
    }
  }

}

class FilePrinter() extends JFrame("FilePrinter v1.0") {
  createUI()
  val pj: PrinterJob = PrinterJob.getPrinterJob
  private var mPageFormat = pj.defaultPage
  private var mPageRenderer: FilePageRenderer = _
  private var mTitle: String = _
  setVisible(true)


  protected def createUI(): Unit = {
    setSize(350, 300)
    center()
    val content = getContentPane
    content.setLayout(new BorderLayout)
    // Add the menu bar.
    val mb = new JMenuBar
    val file = new JMenu("File", true)
    file.add(new FileOpenAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK))
    file.add(new FilePrintAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK))
    file.add(new FilePageSetupAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK | Event.SHIFT_MASK))
    file.addSeparator()
    file.add(new FilePrinter.FileQuitAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK))
    mb.add(file)
    val page = new JMenu("Page", true)
    page.add(new PageNextPageAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0))
    page.add(new PagePreviousPageAction).setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0))
    mb.add(page)
    setJMenuBar(mb)
    // Add the contents of the window.
    getContentPane.setLayout(new BorderLayout)
    // Exit the application when the window is closed.
    addWindowListener(new WindowAdapter() {
      override def windowClosing(e: WindowEvent): Unit = {
        System.exit(0)
      }
    })
  }

  protected def center(): Unit = {
    val screenSize = Toolkit.getDefaultToolkit.getScreenSize
    val frameSize = getSize
    val x = (screenSize.width - frameSize.width) / 2
    val y = (screenSize.height - frameSize.height) / 2
    setLocation(x, y)
  }

  def showTitle(): Unit = {
    val currentPage = mPageRenderer.getCurrentPage + 1
    val numPages = mPageRenderer.getNumPages
    setTitle(mTitle + " - page " + currentPage + " of " + numPages)
  }

  class FileOpenAction() extends AbstractAction("Open...") {
    override def actionPerformed(ae: ActionEvent): Unit = { // Pop up a file dialog.
      val fc = new JFileChooser(".")
      val result = fc.showOpenDialog(FilePrinter.this)
      if (result != 0) return
      val f = fc.getSelectedFile
      if (f == null) return
      // Load the specified file.
      try {
        mPageRenderer = new FilePageRenderer(f, mPageFormat)
        mTitle = "[" + f.getName + "]"
        showTitle()
        val jsp = new JScrollPane(mPageRenderer)
        getContentPane.removeAll()
        getContentPane.add(jsp, BorderLayout.CENTER)
        validate()
      } catch {
        case ioe: IOException =>
          System.out.println(ioe)
      }
    }
  }

  class FilePrintAction() extends AbstractAction("Print") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      val pj = PrinterJob.getPrinterJob
      pj.setPrintable(mPageRenderer, mPageFormat)
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
      if (mPageRenderer != null) {
        mPageRenderer.paginate(mPageFormat)
        showTitle()
      }
    }
  }

  class PageNextPageAction() extends AbstractAction("Next page") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      if (mPageRenderer != null) mPageRenderer.nextPage()
      showTitle()
    }
  }

  class PagePreviousPageAction() extends AbstractAction("Previous page") {
    override def actionPerformed(ae: ActionEvent): Unit = {
      if (mPageRenderer != null) mPageRenderer.previousPage()
      showTitle()
    }
  }

}