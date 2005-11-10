package de.enough.polish.ui.containerviews;


import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.Style;

public class DinnerForOneView extends TableView {
	
	private final static int START_MAXIMUM = 30;
	private final static int MAX_PERIODE = 5;
	private final static int DEFAULT_DAMPING = 10;
	private final static int SPEED = 10;
	private int [] yAdjustments;
	private int [] xAdjustments;
	private int [] yNewAdjust;
	private int [] xNewAdjust;
	private int delayVertical,delayHorizontal,myitemslenght;
	private int startX, startY;
	private boolean isAnimationRunning = true;
	private int damping = DEFAULT_DAMPING;
	private int currentPeriode;
	private int maxPeriode = MAX_PERIODE;
	private int currentMaximum;
	private int startMaximum = START_MAXIMUM;
	private int speed = SPEED;
	private boolean animationInitialised;
	
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
//		this.restartAnimation
//		this.contentHeight 
//		this.contentWidth 
//		this.isAnimationRunning = true;
		System.out.print("column"+this.columns+""+this.contentHeight+":::"+this.contentWidth+"\n");
		this.parentContainer = parent;
		Item[] myItems = this.parentContainer.getItems();
		this.yNewAdjust   = new int [myItems.length];
		this.xNewAdjust   = new int [myItems.length];
		this.xAdjustments = new int [myItems.length];
		this.yAdjustments = new int [myItems.length];
		int y = 50,x = 20,columns = 0;
		this.myitemslenght = myItems.length;
		for (int i = 0; i < this.myitemslenght; i++) {
			Item item = myItems[i];
			this.xAdjustments[i] = x;
			this.yAdjustments[i] = y;
			if(this.focusedIndex == i){
				this.startX = x;
				this.startY = y;
			}
			System.out.print(x+"...x\n");
			columns = ((columns + 1)) % this.columns;
			if(columns == 0){
				x = 0;
				y += this.paddingVertical+item.getItemHeight(firstLineWidth,lineWidth);
			}else{
				x += this.paddingHorizontal+item.getItemWidth(firstLineWidth,lineWidth);
			}
		}
		for (int i = 0; i < this.myitemslenght; i++) {
			this.yNewAdjust[i] = this.startY;
			this.xNewAdjust[i] = this.startX;
			System.out.print(this.xAdjustments[i]+"::"+this.xNewAdjust[i]+";\n");
		}
		this.delayVertical = (this.paddingVertical+ myItems[0].getItemHeight(firstLineWidth,lineWidth));
//#ifdef polish.css.columns
		this.delayHorizontal = (this.paddingHorizontal+ myItems[0].getItemWidth(firstLineWidth,lineWidth));
//#endif
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


	
//	//#ifdef polish.css.droppingview-repeat-animation
//	public void showNotify() {
//		if (this.repeatAnimation && this.xAdjustments != null) {
//			initAnimation( this.parentContainer.getItems(), this.xAdjustments );
//		}
//	}	
//	//#endif
	
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
//		Thread th = new Thread();
//		try {
//			th.sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.print("sleep-error"+e+"\n");
//		}
		return this.isAnimationRunning;
	}

	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO Auto-generated method stub
		Item[] myItems = this.parentContainer.getItems();
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				if(this.yAdjustments[i] < this.yNewAdjust[i]){
					this.yNewAdjust[i] -= 1;
				}
				else if(this.yAdjustments[i] > this.yNewAdjust[i]){
					this.yNewAdjust[i] +=  1;
				}
				if(this.xAdjustments[i] < this.xNewAdjust[i]){
					this.xNewAdjust[i] -= 1;
					System.out.print("x-"+this.xNewAdjust[i]+"\n");
				}
				else if(this.xAdjustments[i] > this.xNewAdjust[i]){
					this.xNewAdjust[i] +=  1;
					System.out.print("x+"+this.xNewAdjust[i]+"\n");
				}
				item.paint(this.xNewAdjust[i], this.yNewAdjust[i], this.xNewAdjust[i], this.xNewAdjust[i]+this.delayHorizontal, g);
			}
		}
}
