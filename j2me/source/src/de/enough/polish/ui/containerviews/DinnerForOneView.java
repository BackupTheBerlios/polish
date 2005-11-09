package de.enough.polish.ui.containerviews;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

public class DinnerForOneView extends ContainerView {
	
	private final static int START_MAXIMUM = 30;
	private final static int MAX_PERIODE = 5;
	private final static int DEFAULT_DAMPING = 10;
	private final static int SPEED = 10;
	private int [] yAdjustments;
	private int [] xAdjustments;
	private int delay,myitemslenght;
	private int startX, startY;
	private boolean isAnimationRunning = true;
	private int damping = DEFAULT_DAMPING;
	private int currentPeriode;
	private int maxPeriode = MAX_PERIODE;
	private int currentMaximum;
	private int startMaximum = START_MAXIMUM;
	private int speed = SPEED;
	private boolean animationInitialised;
	//#ifdef polish.css.droppingview-repeat-animation
		private boolean repeatAnimation;
	//#endif
	/**
	 * Creates new DroppingView
	 */
	public DinnerForOneView() {
		super();
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#initContent(de.enough.polish.ui.Container, int, int)
	 */
	protected void initContent(Container parent, int firstLineWidth,
			int lineWidth) 
	{
//		this.isAnimationRunning = true;
		this.parentContainer = parent;
		Item[] myItems = this.parentContainer.getItems();
		this.xAdjustments = new int [myItems.length];
		this.yAdjustments = new int [myItems.length];
		int y = 1,x = 0;
		this.myitemslenght = myItems.length;
		for (int i = 0; i < this.myitemslenght; i++) {
			Item item = myItems[i];
			this.xAdjustments[i] = x;
			this.yAdjustments[i] = y;
			if(item.appearanceMode == Item.PLAIN){
				this.focusItem(i,item);
				this.startX = this.xAdjustments[i];
				this.startY = this.yAdjustments[i];
				System.out.print("startX"+startX+"startY"+startY+"\n");
			}
			y += this.paddingVertical+item.getItemHeight(firstLineWidth,lineWidth);
		}
		this.delay = this.paddingVertical+ myItems[1].getItemHeight(firstLineWidth,lineWidth);
	}



	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#getNextItem(int, int)
	 */
	protected Item getNextItem(int keyCode, int gameAction) {
		if (gameAction == Canvas.DOWN) {
			return getNextFocusableItem( this.parentContainer.getItems(), true, 1, true);
		} else if (gameAction == Canvas.UP) {
			return getNextFocusableItem( this.parentContainer.getItems(), false, 1, true);
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.droppingview-repeat-animation
			Boolean repeat = style.getBooleanProperty("droppingview-repeat-animation");
			if (repeat != null) {
				this.repeatAnimation = repeat.booleanValue();
			} else {
				this.repeatAnimation = false;
			}
		//#endif
		//#ifdef polish.css.droppingview-damping
			Integer dampingInt = style.getIntProperty("droppingview-damping");
			if (dampingInt != null) {
				this.damping = dampingInt.intValue();
			}
		//#endif
		//#ifdef polish.css.droppingview-maximum
			Integer maxInt = style.getIntProperty("droppingview-maximum");
			if (maxInt != null) {
				this.startMaximum = maxInt.intValue();
			}
		//#endif
		//#ifdef polish.css.droppingview-speed
			Integer speedInt = style.getIntProperty("droppingview-speed");
			if (speedInt != null) {
				this.speed = speedInt.intValue();
			}
		//#endif
		//#ifdef polish.css.droppingview-maxperiode
			Integer periodeInt = style.getIntProperty("droppingview-maxperiode");
			if (periodeInt != null) {
				this.maxPeriode = periodeInt.intValue();
			}
		//#endif
	}
	
	//#ifdef polish.css.droppingview-repeat-animation
	public void showNotify() {
		if (this.repeatAnimation && this.xAdjustments != null) {
			initAnimation( this.parentContainer.getItems(), this.xAdjustments );
		}
	}	
	//#endif
	
	/**
	 * Initialises the animation.
	 *  
	 * @param items the items.
	 * @param yValues the y-adjustment-values
	 */
	private void initAnimation(Item[] items, int[] xValues) {
		this.isAnimationRunning = true;
		this.animationInitialised = true;
	}


	/**
	 * Animates this view - the items appear to drop from above.
	 * 
	 * @return true when the view was really animated.
	 */
	public boolean animate() {
		this.delay = this.delay - (this.delay / (this.myitemslenght-1));
		if(this.delay == 2)this.delay = 1;
		return this.isAnimationRunning;
	}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO Auto-generated method stub
		Item[] myItems = this.parentContainer.getItems();
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				int adjustedY = y+(this.yAdjustments[i]/this.delay);
				item.paint(x, adjustedY, leftBorder, rightBorder, g);
				if(adjustedY == (this.yAdjustments[i]+this.yAdjustments[1]))this.isAnimationRunning = false;
				else this.isAnimationRunning = true;
				System.out.print("adY-"+adjustedY+";adY[i]-"+this.yAdjustments[i]+";delay-"+this.delay+":"+this.isAnimationRunning+";\n");
			}
	}
}
