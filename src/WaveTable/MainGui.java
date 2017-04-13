package WaveTable;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

// From http://www.tutorialspoint.com/javaexamples/gui_polygon.htm
public class MainGui {
  public JFrame frame;
  public DrawingPanel drawpanel;
  //public WaveGrid WGrid;
  /* ********************************************************************************* */
  public MainGui() {
  }
  /* ********************************************************************************* */
  public void Init() {
    this.frame = new JFrame();
    this.frame.setTitle("Grid");
    this.frame.setSize(900, 900);
    this.frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });
    Container contentPane = this.frame.getContentPane();
    this.drawpanel = new DrawingPanel();
    contentPane.add(this.drawpanel);
    this.drawpanel.BigApp = this;
    frame.setVisible(true);
  }
  /* ********************************************************************************* */
  public void SaveAudio() {
  }
  /* ********************************************************************************* */
  public static class DrawingPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener, ComponentListener, KeyListener {
    MainGui BigApp;
    WaveTable WGrid;
    int ScreenMouseX = 0, ScreenMouseY = 0;
    double MouseOffsetX = 0, MouseOffsetY = 0;
    /* ********************************************************************************* */
    public DrawingPanel() {
      this.Init();
    }
    /* ********************************************************************************* */
    public final void Init() {
      this.WGrid = new WaveTable();
      this.addMouseListener(this);
      this.addMouseMotionListener(this);
      this.addMouseWheelListener(this);
      this.addKeyListener(this);
    }
    /* ********************************************************************************* */
    public void Draw_Me(Graphics2D g2d) {
      DrawingContext dc = new DrawingContext();
      dc.gr = g2d;
      int wdt, hgt;

      this.ResetPlaybackCursor();

      wdt = this.getWidth();
      hgt = this.getHeight();

      Rectangle2D rect = new Rectangle2D.Float();
      if (true) {
        rect.setRect(0, 0, wdt, hgt);
      }
      Stroke oldStroke = g2d.getStroke();
      BasicStroke bs = new BasicStroke(5f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
      g2d.setStroke(bs);
      g2d.setColor(Color.green);
      g2d.draw(rect);// green rectangle confidence check for clipping
      g2d.setStroke(oldStroke);
      WGrid.Draw_Me(dc);
      WGrid.Update();
      WGrid.Rollover();
      this.repaint();
    }
    /* ********************************************************************************* */
    @Override public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      Draw_Me(g2d);// redrawing everything is overkill for every little change or move. to do: optimize this
    }
    /* ********************************************************************************* */
    @Override public void mouseDragged(MouseEvent me) {
    }
    @Override public void mouseMoved(MouseEvent me) {
    }
    /* ********************************************************************************* */
    @Override public void mouseClicked(MouseEvent me) {
    }
    @Override public void mousePressed(MouseEvent me) {
    }
    @Override public void mouseReleased(MouseEvent me) {
    }
    @Override public void mouseEntered(MouseEvent me) {
    }
    @Override public void mouseExited(MouseEvent me) {
    }
    /* ********************************************************************************* */
    @Override public void mouseWheelMoved(MouseWheelEvent mwe) {
    }
    /* ********************************************************************************* */
    @Override public void componentResized(ComponentEvent ce) {
    }
    @Override public void componentMoved(ComponentEvent ce) {
    }
    @Override public void componentShown(ComponentEvent ce) {
    }
    @Override public void componentHidden(ComponentEvent ce) {
    }
    /* ********************************************************************************* */
    @Override public void keyTyped(KeyEvent ke) {
    }
    @Override public void keyPressed(KeyEvent ke) {
    }
    @Override public void keyReleased(KeyEvent ke) {
    }
    /* ********************************************************************************* */
    // Playback cursor
    int PrevTime0 = 0, PrevTime1 = 0;
    int TimeInt0 = 0, TimeInt1 = 0;
    /* ********************************************************************************* */
    public void ResetPlaybackCursor() {
      this.PrevTime0 = 0;
      this.PrevTime1 = 0;
      this.TimeInt0 = 0;
      this.TimeInt1 = 0;
    }
    /* ********************************************************************************* */
    public void DrawPlaybackCursor() {
      Graphics2D gr = (Graphics2D) this.getGraphics();
      gr.setXORMode(Color.white);
      gr.fillRect(TimeInt0, 0, TimeInt1 - TimeInt0, this.getHeight());// draw new
    }
  }
}
