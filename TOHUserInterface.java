// Copyright (C) 2020 Jarmo Hurri

// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.

// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

import javax.swing.*;        
import java.awt.*;
import java.awt.event.*;

public class TOHUserInterface extends JFrame
{

  public TOHUserInterface (TOHApp controller, TowerOfHanoi viewToh)
  {
    super ("Tower of Hanoi solution framework");
    setViewToh(viewToh);
    setController(controller);
    initializeKeyBindings();

    setSize (XSIZE, YSIZE);
    setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

    tohPanel = new JPanel ()
      {
        @Override
        protected void paintComponent (Graphics graphics)
        {
          assert graphics != null;
          drawApplication ((Graphics2D) graphics);
        }
      };

    setLayout(new BorderLayout());
    add(tohPanel, BorderLayout.CENTER);
    initializeButtonControls();

    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setVisible (true);

    applicationUpdateTimer = new Timer (updateIntervalMs, new MoveForward());
    applicationUpdateTimer.setActionCommand("Timer");
  }

  private void initializeButtonControls () {
    JPanel controls = new JPanel();
    controls.setBackground(Color.WHITE);

    for (KeyStrokeAction act : actions) {
      JButton button = new JButton();

      button.setAction(act);
      button.setFocusable(false);

      if (act instanceof PlayPause) playButton = button;
      controls.add(button);
    }

    add (controls, BorderLayout.PAGE_END);
  }

  private void initializeKeyBindings() {
    InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = getRootPane().getActionMap();

    for (KeyStrokeAction a : actions) {
      inputMap.put(a.getKeyStroke(), a.getValue(Action.NAME));
      actionMap.put(a.getValue(Action.NAME), a);
    }
  }

  public void drawApplication (Graphics2D graphics)
  {
    assert graphics != null;
    final int width = tohPanel.getWidth() , height = tohPanel.getHeight();
    graphics.setColor (Color.white);
    graphics.fillRect (0, 0, width - 1,  height - 1);

    // graphical element scaling to window dimensions
    int pegLabelY = 11 * height / 12;
    int pegBaseY = 5 * height / 6;
    int pegHeight = 2 * height / 3;
    int pegTopY = pegBaseY - pegHeight;
    int pegWidth = width / 36;
    int pegIndex = 1;
    int numDiscs = viewToh.getNumDiscs ();
    int discHeight = pegHeight / (numDiscs + 1);
    int discMaxWidth = width / (NUM_PEGS + 3);
    int discMinWidth = pegWidth * 2;

    for (Peg peg : Peg.values ())
    {
      graphics.setColor (Color.black);
      final int x = pegIndex * width / (NUM_PEGS + 1);
      drawString (graphics, peg.toString(), x-5, pegLabelY); // peg label
      graphics.drawRect (x - pegWidth / 2, pegTopY, pegWidth, pegHeight); // peg

      // discs
      Integer[] discs = viewToh.getDiscs (peg);
      int numPegDiscs = discs.length;
      for (int i = 0; i < numPegDiscs; i++)
      {
        graphics.setColor (Color.black);
        int disc = discs [i];
        int discWidth =
          ((discMaxWidth - discMinWidth) * disc + discMinWidth * numDiscs - discMaxWidth)
          / (numDiscs - 1);
        int rectX = x - discWidth / 2,
          rectY = pegBaseY - (i + 1) * discHeight;
        graphics.fillRect (rectX, rectY, discWidth, discHeight);
        graphics.setColor (Color.white);
        graphics.drawRect (rectX, rectY, discWidth, discHeight);
      }

      pegIndex++;
    }
  }

  public void drawString (Graphics2D graphics,
                          String str,
                          int x,
                          int y,
                          int fontSize,
                          boolean antiAliasing)
  {
    assert graphics != null;
    if (antiAliasing)
      graphics.setRenderingHint (RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
    Font font = new Font ("Courier", Font.PLAIN, fontSize);
    graphics.setFont (font);
    graphics.drawString (str, x, y);
  }

  public void drawString (Graphics2D graphics,
                          String str,
                          int x,
                          int y,
                          int fontSize)
  {
    assert graphics != null;
    drawString (graphics, str, x, y, fontSize, true);
  }
    
  public void drawString (Graphics2D graphics,
                          String str,
                          int x,
                          int y)
  {
    assert graphics != null;
    drawString (graphics, str, x, y, 16);
  }

  public TOHApp getController() {
    return controller;
  }

  private void setController(TOHApp controller) {
    this.controller = controller;
  }

  public void setViewToh(TowerOfHanoi viewToh) {
    this.viewToh = viewToh;
  }

  class Reset extends KeyStrokeAction {
    Reset() {
      super("Reset", KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "R");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (applicationUpdateTimer.isRunning()) new PlayPause().actionPerformed(e);
      getController().resetToh();
      repaint();
    }
  }

  class PlayPause extends KeyStrokeAction {
    PlayPause() {
      super("Play", KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "Space bar");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (applicationUpdateTimer.isRunning()) {
        applicationUpdateTimer.stop();
        playButton.setText("Play");
      }
      else {
        applicationUpdateTimer.restart();
        playButton.setText("Pause");
      }
    }
  }

  class SpeedUp extends KeyStrokeAction {
    SpeedUp() {
      super("+", KeyStroke.getKeyStroke('+'), "Increase the speed of autoplay"); //necessary to support both US and EU layouts
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      int currDelay = applicationUpdateTimer.getDelay();
      if (currDelay <= 1) return;

      applicationUpdateTimer.setDelay(currDelay / 2);
    }
  }

  class SlowDown extends KeyStrokeAction {
    SlowDown() {
      super("-", KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "Decrease the speed of autoplay");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      int currDelay = applicationUpdateTimer.getDelay();
      if (currDelay >= 5000) return;

      applicationUpdateTimer.setDelay(applicationUpdateTimer.getDelay() * 2);
    }
  }

  class MoveForward extends KeyStrokeAction {
    MoveForward() {
      super(">", KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "Right arrow");
      putValue(Action.ACTION_COMMAND_KEY, "ButtonPressed");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (applicationUpdateTimer.isRunning() && e.getActionCommand().equals("ButtonPressed"))
        new PlayPause().actionPerformed(e);
      getController().makeMove();
      repaint();
    }
  }

  class MoveBack extends KeyStrokeAction {
    MoveBack() {
      super("<", KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "Left arrow");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (applicationUpdateTimer.isRunning()) new PlayPause().actionPerformed(e);
      getController().retractMove();
      repaint();
    }
  }

  private final KeyStrokeAction[] actions = {
          new MoveBack(),
          new PlayPause(),
          new Reset(),
          new MoveForward(),
          new SpeedUp(),
          new SlowDown(),
  };

  public final int XSIZE = 1024;
  public final int YSIZE = 768;
  public final int NUM_PEGS = Peg.values ().length;

  private final Timer applicationUpdateTimer;
  public final int updateIntervalMs = 1000;
  private TowerOfHanoi viewToh;
  private TOHApp controller;

  private JPanel tohPanel;
  private JButton playButton;
}

