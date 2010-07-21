package de.enough.skylight.renderer;

import de.enough.polish.ui.Background;
import de.enough.polish.ui.Border;
import de.enough.polish.ui.Container;
import de.enough.polish.ui.Font;
import de.enough.polish.ui.ImageItem;
import de.enough.polish.ui.Item;
import de.enough.polish.ui.StringItem;
import de.enough.polish.ui.Style;
import de.enough.polish.ui.backgrounds.SimpleBackground;
import de.enough.skylight.modeller.Box;
import de.enough.skylight.renderer.node.CssElement;

/**
 * A BoxContainer object is the visual component for a Box object like a div. The order of its children corresponds
 * with the order of children in the associated Box object.
 * TODO: This should be a ContainerView.
 */
public class BoxContainer extends Container {

	private final Box box;

	public BoxContainer(Box box) {
		//#style boxcontainer
		super(false);
		this.box = box;
	}
	
	@Override
	protected void initContent(int firstLineWidth, int availWidth, int availHeight) {
		super.initContent(firstLineWidth, availWidth, availHeight);
		mapBoxToItem(this.box,this);
		//#debug
		System.out.println("BoxContainer.initContent():items for box:"+this.box);
		Item item;
		Item[] myItems = (Item[]) this.itemsList.toArray( new Item[ this.itemsList.size() ]);
		int itemCount = myItems.length;
		for(int i = 0; i < itemCount; i++) {
			item = myItems[i];
			// We either have containers or leaf nodes like StringItems as children.
			if(item instanceof BoxContainer) {
				((BoxContainer) item).initContent(firstLineWidth, availWidth, availHeight);
			} else {
				Box childBox = (Box)this.box.children.get(i);
				mapBoxToItem(childBox,item);
			}
		}
		
	}

	public Box getBox() {
		return this.box;
	}

	/**
	 * This method will map the given CSS style in the parameter cssElement to the given J2ME Polish item.
	 * The mapping is from the world of W3C CSS to J2MEPOLISH CSS.
	 * @param cssElement
	 * @param item
	 */
	private void mapBoxToItem(Box box, Item item) {
		

		CssElement cssElement = box.correspondingNode;
		
		Background background = null;
		Border border = null;
		Style newItemStyle = new Style("",Item.LAYOUT_LEFT|Item.LAYOUT_TOP,background,border,null,null);
		newItemStyle.addAttribute("font-size", new Integer( Font.SIZE_SMALL ));
		
		if(item instanceof StringItem) {
			String text = ((StringItem)item).getText();
			//#debug
			System.out.println("  BoxContainer.initContent():Mapping to StringItem with text '"+text+"':"+item.relativeX+"x"+item.relativeY);
		}
		
		if(item instanceof ImageItem) {
			newItemStyle.background = new SimpleBackground(0x0000FF);
		}
		
		// The order is important! Set the position after the style is set.
		item.setStyle(newItemStyle);
		item.relativeX = box.x + box.marginLeft + box.paddingLeft;
		item.relativeY = box.y + box.marginTop + box.paddingTop;
	}
}
