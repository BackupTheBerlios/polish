//#condition polish.usePolishGui
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
	private int delay;
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
		int y = 0,x = 0,columns = 0,myContentWidth = 0,myContentHeight = 0,width = 0;
		this.myitemslenght = myItems.length;
		for (int i = 0; i < this.myitemslenght; i++) {
			Item item = myItems[i];			
			this.xAdjustments[i] = x;
			this.yAdjustments[i] = y;
			System.out.print("zielX:"+x+";zielY:"+y+"\n");
			if(this.focusedIndex == i){
				this.startX = x;
				this.startY = y;
			}
			columns = ((columns + 1)) % this.columns;
			System.out.print("columns"+columns+";\n");
			if(columns == 0 && (i+1) != this.myitemslenght){
				x = 0;
				myContentHeight += this.paddingVertical+item.getItemHeight(firstLineWidth,lineWidth);
				y += this.paddingVertical+item.getItemHeight(firstLineWidth,lineWidth);
				width = 0;
				System.out.print("h:"+myContentHeight+";\n");
			}else{
				width +=  this.paddingHorizontal+item.getItemWidth(firstLineWidth,lineWidth); 
				x += this.paddingHorizontal+item.getItemWidth(firstLineWidth,lineWidth);
				
			}
			if(width > myContentWidth){
				myContentWidth = width;
				System.out.print("w:"+width+";\n");
			}
		}
		this.contentHeight = myContentHeight;
		this.contentWidth = myContentWidth;
		System.out.print("width:"+this.contentWidth+".:height:"+this.contentHeight+"\n");
		for (int i = 0; i < this.myitemslenght; i++) {
			this.yNewAdjust[i] = this.startY;
			this.xNewAdjust[i] = this.startX;
		}
		this.delayVertical = this.delay;
		this.delayHorizontal = this.delay;
		System.out.print("delay:::"+this.delay);
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
		return this.isAnimationRunning;
	}
	
	/* (non-Javadoc)
	 * @see de.enough.polish.ui.ContainerView#setStyle(de.enough.polish.ui.Style)
	 */
	protected void setStyle(Style style) {
		super.setStyle(style);
		//#ifdef polish.css.delay-speed
			Integer delayInt = style.getIntProperty("delay-speed");
			if (delayInt != null) {
				this.delay = delayInt.intValue();
			}
			System.out.print("define-css-delay"+this.delay+"\n");
		//#endif
	}
	
	
	protected void paintContent(int x, int y, int leftBorder, int rightBorder, Graphics g) {
		// TODO Auto-generated method stub
		Item[] myItems = this.parentContainer.getItems();
			for (int i = 0; i < myItems.length; i++) {
				Item item = myItems[i];
				if(this.yAdjustments[i] <= (this.yNewAdjust[i]-this.delayVertical)){
					this.yNewAdjust[i] -= this.delayVertical;
				}
				else if(this.yAdjustments[i] >= (this.yNewAdjust[i]+this.delayVertical)){
					this.yNewAdjust[i] +=  this.delayVertical;
				}else{
					this.yNewAdjust[i] = this.yAdjustments[i];
				}
				if(this.xAdjustments[i] <= (this.xNewAdjust[i]-this.delayHorizontal)){				
					this.xNewAdjust[i] -= this.delayHorizontal;
				}
				else if(this.xAdjustments[i] >= (this.xNewAdjust[i]+this.delayHorizontal)){
					this.xNewAdjust[i] +=  this.delayHorizontal;
				}else{
					this.xNewAdjust[i] = this.xAdjustments[i];
				}
				item.paint((this.xNewAdjust[i]+x),(y+ this.yNewAdjust[i]), leftBorder, rightBorder, g);
			}
		}
	}
