package de.enough.polish.runtime.swing;

import de.enough.polish.runtime.Simulation;
import de.enough.polish.runtime.SimulationManager;

import java.awt.Container;
import java.util.HashMap;

import javax.microedition.lcdui.Canvas;
import javax.swing.JApplet;

public class SwingAppletSimulator
  extends JApplet
{
  private SimulationManager manager;
  private Simulation simulation;

  public void init()
  {
    super.init();

    try
    {
      this.manager = new SimulationManager( new HashMap() );
      this.simulation = this.manager.loadSimulation("Nokia/6600", "de.enough.polish.sample.browser.BrowserMidlet");
//      this.simulation.setDisplayChangeListener( this );
      this.simulation.simulateGameActionEvent( Canvas.DOWN );
      Container contentPane = getContentPane();
      contentPane.add( this.simulation );
//      pack();
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void destroy()
  {
    // TODO Auto-generated method stub
    super.destroy();
  }
  
  public void start()
  {
    // TODO Auto-generated method stub
    super.start();
  }

  public void stop()
  {
    // TODO Auto-generated method stub
    super.stop();
  }
  
}
