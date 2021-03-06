/*
 * Screenshot.java (requires Java 1.4+)
 */

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;;
//import java.awt.Window;

public class Screenshot extends Frame
implements ActionListener, MouseListener,MouseMotionListener,KeyListener,Runnable{
	
	private static final long serialVersionUID = 1L;
	private int x = 400,y = 400,width = 176,height = 220;
	private int interval = 120,maxImages = 120;
	private Button bStart,bEnde,bQuit,bRefresh;
	private BufferedImage scrIm;
	private TextField tfx, tfy, tfw, tfh,tfInterval,tfMaxmages,tfImageName;
	private Label labx,laby,labw,labh,labInterval,labMaxImages,labImageName;
	private boolean isShooting = false;
	private Thread th;
    public void actionPerformed( ActionEvent e )
    {
    	String cmd = e.getActionCommand ();
    	if (cmd.equals ("Refresh")){ 
	        this.x = Integer.parseInt(this.tfx.getText());
	        this.y = Integer.parseInt(this.tfy.getText());
	        this.width = Integer.parseInt(this.tfw.getText());
	        this.height = Integer.parseInt(this.tfh.getText());
	        this.screenBackground();
	        this.repaint();
    	}
    	else if (cmd.equals ("start")){
    		try {
    			this.maxImages = Integer.parseInt(this.tfMaxmages.getText());
    			this.isShooting = true;
				this.shot(1);
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    	}    	
    	else if (cmd.equals ("ende")){
    		this.isShooting = false;
    	}
    	else if (cmd.equals ("Programm Beenden")){
            System.exit(0);
    	}
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Screenshot screenshot;
		screenshot = new Screenshot();
	}
	
	public void screenBackground(){
		this.th = new Thread(this);
		this.setVisible(false);
		this.dispose();
//		this.setVisible(false);
//		this.hide();
		long starttime = System.currentTimeMillis() + 25;
		while(starttime > System.currentTimeMillis()){
		}	
			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		    int scrWidth = d.width;  int scrHeight = d.height;
		    Rectangle scrRect = new Rectangle(0,0, d.width, d.height);
		    Robot robot;
			try {
				robot = new Robot();
				this.scrIm = robot.createScreenCapture(scrRect);    // take a snap
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.setSize( d.width, d.height);
			this.setVisible(true);
			this.repaint();
	}
	
	public Screenshot(){

//		this.setUndecorated(true);
	    this.setLayout(null);
	    
	    
	    this.bStart = new Button("start");
	    this.bStart.addActionListener(this);
	    this.bEnde = new Button("ende");	
	    this.bEnde.addActionListener(this);
	    this.bQuit = new Button("Programm Beenden");
	    this.bQuit.addActionListener(this);
	    this.bRefresh = new Button("Refresh");
	    this.bRefresh.addActionListener(this);
		this.tfx = new TextField(""+this.x, 20);
		this.tfy = new TextField(""+this.y, 20);
		this.tfw = new TextField(""+this.width, 20);
		this.tfh = new TextField(""+this.height, 20);
		this.tfMaxmages = new TextField(""+120);
		this.tfInterval = new TextField(""+this.interval, 20);
		this.tfImageName = new TextField("ImageName",20);
		this.labx = new Label("X :");
		this.laby = new Label("Y :");
		this.labw = new Label("Width :");
		this.labh = new Label("Height :");
		this.labMaxImages = new Label("Max. Image :");
		this.labInterval = new Label("Interval :");
		this.labImageName = new Label("Image Name :");
	    this.bStart.setBounds(0,125,150,25);
	    this.bEnde.setBounds(0,150,150,25);	 
	    this.bQuit.setBounds(0,175,150,25);	
	    this.bRefresh.setBounds(0,200,150,25);
		this.tfx.setBounds(80,300,100,25);
		this.tfy.setBounds(80,325,100,25);
		this.tfw.setBounds(80,350,100,25);
		this.tfh.setBounds(80,375,100,25);  
		this.tfInterval.setBounds(80,400,100,25);
		this.tfMaxmages.setBounds(80,425,100,25);
		this.tfImageName.setBounds(80,450,100,25);
		this.labx.setBounds(5,300,80,25);
		this.laby.setBounds(5,325,80,25);
		this.labw.setBounds(5,350,80,25);
		this.labh.setBounds(5,375,80,25);
		this.labMaxImages.setBounds(5,400,80,25);
		this.labInterval.setBounds(5,425,80,25);
		this.labImageName.setBounds(5,450,80,25);
	    this.add(this.bStart);
	    this.add(this.bEnde);
	    this.add(this.bQuit);
	    this.add(this.bRefresh);
	    this.add(this.tfx);
	    this.add(this.tfy);
	    this.add(this.tfw);
	    this.add(this.tfh);
	    this.add(this.tfInterval);
	    this.add(this.tfMaxmages);
	    this.add(this.tfImageName);
	    this.add(this.labx);
	    this.add(this.laby);
	    this.add(this.labw);
	    this.add(this.labh);
	    this.add(this.labMaxImages);
	    this.add(this.labInterval);
	    this.add(this.labImageName);
	    this.addKeyListener(this);
	    this.tfx.addKeyListener(this);
	    this.tfy.addKeyListener(this);
	    this.tfw.addKeyListener(this);
	    this.tfh.addKeyListener(this);
	    this.addMouseListener(this);
	    this.screenBackground();
//		this.repaint();
	}


	    
	  public void paint(Graphics g) {
		  g.drawImage(this.scrIm, 0, 0, null);
		  g.drawRect(this.x,this.y,this.width,this.height);
	  }

	public void shot(int i) throws IOException{
		String stImageName = this.tfImageName.getText();
		File userDir = new File(stImageName);
		userDir.mkdir(); 
		this.interval =  Integer.parseInt(this.tfInterval.getText());
		this.maxImages = Integer.parseInt(this.tfMaxmages.getText());
//		long inter = System.currentTimeMillis() + this.interval;
		this.setVisible(false);
		this.dispose();
		String outFileName;
		while(this.isShooting && i < this.maxImages){
//			if(inter <= System.currentTimeMillis()){
				outFileName = stImageName+i+".png";
				Rectangle screenRect = new Rectangle(x, y, width, height);
				// create screen shot
				Robot robot;
				try {
					robot = new Robot();
					BufferedImage image = robot.createScreenCapture(screenRect);
					// save captured image to PNG file
					ImageIO.write(image, "png", new File(stImageName+"/"+outFileName));
				} catch (AWTException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					this.th.sleep(this.interval);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.print("error");
				}
				i++;
//				inter = System.currentTimeMillis() + this.interval;
//			}
			
		}
		this.isShooting = false;
		this.setVisible(true);
	}

	public void mouseClicked(MouseEvent evt) {
		// TODO Auto-generated method stub
		this.x = evt.getX();
		this.y = evt.getY();
		this.tfx.setText( ""+evt.getX()) ;
		this.tfy.setText( ""+evt.getY());
		this.repaint();
	}

	public void mousePressed(MouseEvent evt) {
		// TODO Auto-generated method stub
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	public void keyPressed(KeyEvent evt) {
		// TODO Auto-generated method stub	
	}
	public void keyReleased(KeyEvent evt) {
		// TODO Auto-generated method stub
		System.out.print("iii");
        this.x = Integer.parseInt(this.tfx.getText());
        this.y = Integer.parseInt(this.tfy.getText());
        this.width = Integer.parseInt(this.tfw.getText());
        this.height = Integer.parseInt(this.tfh.getText());
		this.repaint();
	}
	public void run() {
		// TODO Auto-generated method stub
		
	}

}