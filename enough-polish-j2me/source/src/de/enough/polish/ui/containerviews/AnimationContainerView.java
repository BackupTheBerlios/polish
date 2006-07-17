/**
 * 
 */
package de.enough.polish.ui.containerviews;

import de.enough.polish.ui.ContainerView;
import de.enough.polish.ui.Item;

/**
 * Animates all embedded items instead only one.
 * 
 * @author simon
 *
 */
public class AnimationContainerView extends ContainerView {

	public boolean animate() {
		boolean animated = super.animate(); 
		Item[] items = this.parentContainer.getItems();
		for (int i = 0; i < items.length; i++) {
			Item item = items[i];
			if (item != this.focusedItem) {
				animated |= item.animate();
			}
		}
		return animated;
	}
	
	

}
