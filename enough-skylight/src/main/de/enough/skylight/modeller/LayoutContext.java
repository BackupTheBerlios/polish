package de.enough.skylight.modeller;

import de.enough.polish.util.ArrayList;
import de.enough.skylight.renderer.debug.BuildDebug;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.node.TextCssElement;

public class LayoutContext {

	class StackFrame {
		Box workingBox;
		int childPos;
	}

	ArrayList frameStack = new ArrayList();

	// in the content
	int currentYPosition = 0;

	// in the content
	int currentXPosition = 0;

	// in the content
	int startXPosition = 0;

	int maximumXUsed = 0;

	int maximumYUsed = 0;

	int maxPossibleContentWidth = 0;

	Box box = null;

	FloatBoxManager floats = null;

	ArrayList currentRow = new ArrayList();

	ArrayList savedForLater = new ArrayList();

	int childPos = 0;
	ArrayList children = null;

	public int currentRowMaxWidth = 0;

	int currentRowLeftOffset = 0;

	public boolean currentRowHasOnlyFloats = true;

	// The amount of space occupied by previous left and right floats that
	// "overflow" into the current row
	public int currentRowLeftFloatOccupiedSpace = 0;
	public int currentRowRightFloatOccupiedSpace = 0;

	int maxY = 0;
	int maxX = 0;

	/**
	 * Set the current Y position
	 * 
	 * @param y
	 */
	public void setCurrentYPosition(int y) {
		this.currentYPosition = y;
		this.currentXPosition = this.floats.leftIntersectionPoint(this.box.getAbsoluteX()
				+ this.box.marginLeft + this.box.paddingLeft, this.box.getAbsoluteY() + y
				+ this.box.marginTop + this.box.paddingTop, this.currentRowMaxWidth, this.box);
	}

	public void setMaxPossibleContentWidth(int width) {
		if (width == 0) {

		}
		this.maxPossibleContentWidth = width;
	}

	public int getCurrentY() {
		return this.currentYPosition;
	}

	public int getCurrentX() {
		return this.currentXPosition;
	}

	public int getCurrentRowWidth() {
		int i = 0;
		int size = this.currentRow.size();
		int rowWidth = 0;
		while (i < size) {
			Box temp = (Box) this.currentRow.get(i);
			rowWidth += temp.getTotalWidth();
			i++;
		}
		return rowWidth;
	}

	public int getCurrentRowHeight() {
		if (this.rowHeightNoFloats != 0) {
			return this.rowHeightNoFloats;
		} else if (this.rowHeightFloats != Integer.MAX_VALUE) {
			return this.rowHeightFloats;
		} else {
			return 0;
		}
	}

	public int getRemainingRowWidth() {
		if (this.box.hasDynamicSize) {
			System.out.println("A");
			return Math.min(this.currentRowMaxWidth + this.startXPosition, getMaxX())
					- getCurrentX();
		} else {
			System.out.println("B " + this.currentRowMaxWidth + " "
					+ getCurrentRowWidth());
			return this.currentRowMaxWidth - getCurrentRowWidth();
		}
	}

	public int getCurrentRowLeftFloatOccupiedSpace() {
		return this.currentRowLeftFloatOccupiedSpace;
	}

	public int getCurrentRowRightFloatOccupiedSpace() {
		return this.currentRowRightFloatOccupiedSpace;
	}

	public int getLeftFloatsCount() {
		return this.leftFloatsCount;
	}

	public int getRightFloatsCount()

	{
		return this.rightFloatsCount;
	}

	private void setTopFrameAsWorkingBoxFrame() {
		int pos = this.frameStack.size() - 1;
		StackFrame frame = (StackFrame) this.frameStack.get(pos);
		this.box = frame.workingBox;
		this.childPos = frame.childPos;
		this.children = this.box.correspondingNode.getChildren();
		this.frameStack.remove(pos);
	}

	private void saveCurrentFrameToStack() {
		StackFrame frame = new StackFrame();
		frame.workingBox = this.box;
		frame.childPos = this.childPos;
		this.frameStack.add(frame);
	}

	public void goInto(Box b) {
		saveCurrentFrameToStack();
		this.childPos = 0;
		this.box = b;
		this.box.contentWidth = this.box.parent.contentWidth;
		this.children = this.box.correspondingNode.getChildren();

	}

	public void removeFloatsForBox(Box b) {
		this.floats.removeFloatsForBox(b);
	}

	public boolean doesNotFitWithin(Box child) {
		if ((child.getTotalWidth() > this.box.contentWidth)) {

			System.out.println("OOPS: " + child.getTotalWidth() + " "
					+ this.box.contentWidth);
			System.out.println("PARENT: ");
			LayoutModeler.dumpBoxTree(this.box, 0);
			System.out.println("CHILD: ");
			LayoutModeler.dumpBoxTree(child, 0);

		}
		if (this.box.isInline) {
			return (child.getTotalWidth() > this.box.contentWidth);
		} else {
			return (child.getTotalWidth() > this.box.contentWidth);
		}
	}

	public void forcePlacementOnSeparateRow(Box box) {

		do {
			System.out.println("JX");
			nextRow(1);
			removeFloatsForBox(box);
			prepareToPlaceOnCurrentRow(box);
		} while (this.floats.lineOverlapsAnyFloat(box.getAbsoluteX(),
				box.getAbsoluteY(), box.getTotalWidth()));

		placeOnCurrentRow(box);
		if (box.getTotalWidth() > this.currentRowMaxWidth) {
			nextRow();
		}
	}

	public boolean fitsOnCurrentRow(Box child) {
		if (this.box.isFloat) {
			if (getCurrentRowWidth() + child.getTotalWidth() <= this.box.contentWidth) {
				return true;
			} else {
				return false;
			}
		}

		// Otherwise
		if (getCurrentRowWidth() + child.getTotalWidth() <= this.currentRowMaxWidth) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Set position and other parameters as if the box were to be placed next on
	 * the current row
	 */
	public void prepareToPlaceOnCurrentRow(Box box) {
		box.y = getCurrentY();
		box.x = getCurrentX();
	}

	public boolean nextChildFromSavedElements = true;

	public void saveForLater(Box b) {
		this.nextChildFromSavedElements = false;
		removeFloatsForBox(b);
		if (this.savedForLater.indexOf(b) < 0) {
			System.out.println("SAVED FLOAT FOR LATER!");
			this.savedForLater.add(b);
		}
	}

	public Box lastChild = null;
	public boolean repeatLastChild = false;

	public void repeatLastChild() {
		this.repeatLastChild = true;
		this.childPos--;
	}

	public void arrangeCurrentRow() {

		// Handle floats
		// -------------

		ArrayList leftFloats = new ArrayList();
		ArrayList rightFloats = new ArrayList();
		ArrayList regularItems = new ArrayList();

		int i = 0;
		int size = this.currentRow.size();

		Box temp;
		String floatValue = null;
		while (i < size) {
			temp = (Box) this.currentRow.get(i);
			floatValue = (String) StyleManager.getProperty(temp, "float");
			if ("none".equals(floatValue)) {
				regularItems.add(temp);
			} else if ("left".equals(floatValue)) {
				leftFloats.add(temp);
			} else if ("right".equals(floatValue)) {
				rightFloats.add(temp);
			}

			i++;
		}

		this.currentRow.clear();
		this.currentRow.addAll(leftFloats);
		this.currentRow.addAll(regularItems);
		this.currentRow.addAll(rightFloats);

		// Concatenate string items
		Box first, second;

		// <editor-fold>
		Box parent = null;
		if (this.currentRow.size() > 1) {
			i = 0;
			while (i < this.currentRow.size() - 1) {
				System.out.println(i);
				first = (Box) this.currentRow.get(i);
				second = (Box) this.currentRow.get(i + 1);

				if ((first.correspondingNode.getContentType() == CssElement.CONTENT_TEXT)
						&& (second.correspondingNode.getContentType() == CssElement.CONTENT_TEXT)) {

					TextCssElement elem1 = (TextCssElement) first.correspondingNode;
					TextCssElement elem2 = (TextCssElement) second.correspondingNode;

					// They have the same DOM parent (are pieces of an inline
					// context)
					if (((first.DOMParent != null) && (first.DOMParent == second.DOMParent))
							|| (first.correspondingNode.getParent() == second.correspondingNode
									.getParent())) {
						String text = elem1.getValue() + elem2.getValue();
						elem1.setValue(text);
						StyleManager.mapStyleToBox(first);

						// Update frames
						int cur = 0;
						StackFrame cFrame;
						int secondPos = 0;
						while (cur < this.frameStack.size()) {
							cFrame = (StackFrame) this.frameStack.get(cur);
							secondPos = cFrame.workingBox.correspondingNode
									.getChildren().indexOf(elem2);
							if (secondPos >= 0 && secondPos < cFrame.childPos) {
								cFrame.childPos--;
							}
							cur++;
						}

						// Update current context
						secondPos = this.children.indexOf(elem2);

						if (secondPos < this.childPos && secondPos > 0) {
							this.childPos--;
						}

						// Remove the second element from .. everywhere
						this.currentRow.remove(second);
						if (second.parent != null) {
							second.parent.removeChild(second);
						}
						if (first.parent != null) {
							first.parent.removeChild(second);
						}
						if (second.DOMParent != null) {
							second.DOMParent.removeChild(second);
							second.DOMParent.correspondingNode.remove(elem2);
						}
						if (first.DOMParent != null) {
							first.DOMParent.removeChild(second);
							first.DOMParent.correspondingNode.remove(elem2);
						}

						elem2.getParent().remove(elem2);

						continue;

					}

				}

				i++;
			}

		}

		// </editor-fold>

		// Update X and Y positions for items on the current row
		i = 0;
		size = this.currentRow.size();
		int leftmostEdge = this.startXPosition;
		int rightmostEdge = 0;

		// If no width is specified, use the width of the row as the starting
		// rightmost edge
		// TODO: Proper right-alignment desperately needed, but I'm not sure
		// it can be implemented efficently
		if (this.box.hasDynamicSize) {
			rightmostEdge = Math.min(this.startXPosition + this.currentRowMaxWidth,
					getMaxX());
		} else if (StyleManager.getProperty(this.box, "width") == null) {
			rightmostEdge = this.startXPosition + this.currentRowMaxWidth;
		} else // use the current row max width
		{
			rightmostEdge = this.startXPosition + this.currentRowMaxWidth;
		}

		boolean firstRowItemHasBeenFound = false;
		while (i < size) {
			temp = (Box) this.currentRow.get(i);

			temp.y = this.currentYPosition;

			if (!"none".equals(StyleManager.getProperty(temp, "float"))) {
				int type = FloatBoxManager.FLOAT_LEFT;
				if ("right".equals(StyleManager.getProperty(temp, "float"))) {
					type = FloatBoxManager.FLOAT_RIGHT;

					// Align the box according the the rightmost edge
					// and adjust it accordingly
					temp.x = rightmostEdge - temp.getTotalWidth();
					rightmostEdge = temp.x;
				} else {
					// Left float
					temp.x = leftmostEdge;
					leftmostEdge += temp.getTotalWidth();

					if (firstRowItemHasBeenFound == false) {
						temp.isFirstItemOnRow = true;
						firstRowItemHasBeenFound = true;
					}
				}
				FloatBoxManager.Float f = this.floats.addFloat(temp.getAbsoluteX(),
						temp.getAbsoluteY(), temp.getTotalWidth(),
						temp.getTotalHeight(), type, temp);
			} else // This is not a float
			{
				// TODO: Proper vertical align
				temp.y += (getCurrentRowHeight() - temp.getTotalHeight());

				// Align the text to the left
				temp.x = leftmostEdge;
				leftmostEdge += temp.getTotalWidth();

				if (firstRowItemHasBeenFound == false) {
					temp.isFirstItemOnRow = true;
					firstRowItemHasBeenFound = true;
				}
			}

			i++;
		}
	}

	public int rowHeightNoFloats = 0;
	public int rowHeightFloats = Integer.MAX_VALUE;

	public int leftFloatsCount = 0;
	public int rightFloatsCount = 0;

	public void placeOnCurrentRow(Box box) {
		this.savedForLater.remove(box);
		this.currentRow.add(box);

		// See if this box has the biggest Y so far
		if (this.currentYPosition + box.getTotalHeight() > this.maxY) {
			this.maxY = this.currentYPosition + box.getTotalHeight();
		}

		// See if this box has the biggest X so far
		if (this.currentXPosition + box.getTotalWidth() > this.maxX) {
			this.maxX = this.currentXPosition + box.getTotalWidth();
		}

		this.currentXPosition += box.getTotalWidth();

		if (!box.isFloat) {
			this.currentRowHasOnlyFloats = false;
			if (box.getTotalHeight() > this.rowHeightNoFloats) {
				this.rowHeightNoFloats = box.getTotalHeight();
			}
		} else {
			if (box.getTotalHeight() < this.rowHeightFloats) {
				this.rowHeightFloats = box.getTotalHeight();
			}

			String floatType = (String) StyleManager.getProperty(box, "float");
			if ("left".equals(floatType)) {
				this.leftFloatsCount++;
			} else {
				this.rightFloatsCount++;
			}

		}
	}

	public void nextRow() {
		nextRow(0);
	}

	public boolean hasClearRight = false;

	public boolean hasClearRightRequested() {
		return this.hasClearRight;
	}

	public void requestClearRight() {
		this.hasClearRight = true;
	}

	public void nextRow(int minimumOffset) {

		// Arrange the current row
		arrangeCurrentRow();

		// Step 4 : Profit

		// Increment the Y position
		System.out.println(this.currentYPosition);
		setCurrentYPosition(Math.max(this.currentYPosition + minimumOffset,
				this.currentYPosition + getCurrentRowHeight()));
		System.out.println(this.currentYPosition);

		// Clear current row buffer
		this.currentRow.clear();

		int theoreticalMaxWidth = 99999999;

		// Update left and right offsets occupied by previous floats for this
		// row, as well as the row max width
		int x = this.box.getAbsoluteX() + this.box.marginLeft + this.box.paddingLeft;
		int y = this.box.getAbsoluteY() + getCurrentY() + this.box.marginTop
				+ this.box.paddingTop;
		int currentRowAbsoluteY = this.box.getAbsoluteY() + this.currentYPosition
				+ this.box.marginTop + this.box.paddingTop;
		int workMaxWidth = this.box.parent.contentWidth - this.box.marginLeft
				- this.box.marginRight - this.box.paddingLeft - this.box.paddingRight;

		if (this.box.contentWidth > 0) {
			workMaxWidth = this.box.contentWidth;
		}

		this.currentRowLeftFloatOccupiedSpace = this.floats.leftIntersectionPoint(x,
				currentRowAbsoluteY, workMaxWidth, this.box);
		int rightPoint = this.floats.rightIntersectionPoint(x, currentRowAbsoluteY,
				workMaxWidth, this.box);
		this.currentRowRightFloatOccupiedSpace = workMaxWidth - rightPoint;
		// currentRowMaxWidth = rightPoint - currentRowLeftFloatOccupiedSpace ;
		this.currentRowMaxWidth = getMaxRowWidth(this.currentYPosition);

		// Other init stuff
		this.startXPosition = this.floats.leftIntersectionPoint(x, y,
				theoreticalMaxWidth, this.box);
		this.currentXPosition = this.startXPosition;

		this.rowHeightFloats = Integer.MAX_VALUE;
		this.rowHeightNoFloats = 0;

		this.leftFloatsCount = this.rightFloatsCount = 0;
		this.hasClearRight = false;

		this.nextChildFromSavedElements = true;
		this.currentRowHasOnlyFloats = true;

		System.out.println("THIS ROW WIDTH: " + this.currentRowMaxWidth);

	}

	/**
	 * Maximum possible row width for the current box at specified Y offset,
	 * with respect to floats
	 * 
	 * @param yPosition
	 * @return
	 */
	public int getMaxRowWidth(int yPosition) {
		int x = this.box.getAbsoluteX() + this.box.marginLeft + this.box.paddingLeft;
		int y = this.box.getAbsoluteY() + yPosition + this.box.marginTop + this.box.paddingTop;
		int theoreticalMaxWidth = this.box.parent.contentWidth - this.box.marginLeft
				- this.box.marginRight - this.box.paddingLeft - this.box.paddingRight;

		if (this.box.contentWidth > 0) {
			theoreticalMaxWidth = this.box.contentWidth;
		}

		int leftPoint = this.floats.leftIntersectionPoint(x, y, theoreticalMaxWidth,
				this.box);
		int rightPoint = this.floats.rightIntersectionPoint(x, y,
				theoreticalMaxWidth, this.box);

		// LayoutModeler.text(box, 0);
		return (rightPoint - leftPoint);

	}

	/**
	 * Maximum possible box size, with respect to the parent and floats that
	 * apply to this context
	 * 
	 * @param yPosition
	 * @return
	 */
	public int getMaxContentWidth(Box box) {
		int x = box.getAbsoluteX() + box.marginLeft + box.paddingLeft;
		int y = box.getAbsoluteY() + box.marginTop + box.paddingTop;
		int theoreticalMaxWidth = box.parent.contentWidth - box.marginLeft
				- box.marginRight - box.paddingLeft - box.paddingRight;

		int leftCut = this.floats.leftIntersectionPoint(x, y, theoreticalMaxWidth,
				box);
		int rightCut = this.floats.rightIntersectionPoint(x, y, theoreticalMaxWidth,
				box);

		return (rightCut - leftCut);

	}

	public void clearCurrentRow() {
		this.currentRow.clear();
	}

	public int getMaxY() {
		return this.maxY;
	}

	public int getMaxX() {
		return this.maxX;
	}

	public LayoutContext(Box box, FloatBoxManager floats) {
		this.box = box;
		this.floats = floats;
		this.children = box.correspondingNode.getChildren();
		this.childPos = 0;
	}

	public boolean hasMoreChildren() {
		System.out.println("ANY CHILDREN");
		if ((this.childPos < this.children.size()) || (this.savedForLater.size() > 0)) {
			System.out.println("HAS KID IN THIS FRAME");
			return true;
		} else {
			if (this.frameStack.size() > 0) {
				StackFrame frame = (StackFrame) this.frameStack.get(this.frameStack
						.size() - 1);
				if (frame.childPos < frame.workingBox.correspondingNode
						.getChildren().size()) {
					System.out.println("HAS KID IN OTHER FRAME");
					return true;
				} else {
					System.out.println("NO MORE KIDS");
					return false;
				}
			} else {
				System.out.println("NO MORE KIDS");
				return false;
			}
		}
	}

	public boolean hasMoreRegularChildren() {
		System.out.println("MORE REG CHILDREN");
		if ((this.childPos < this.children.size())) {
			System.out.println("HAS KID IN THIS FRAME");
			return true;
		} else {
			if (this.frameStack.size() > 0) {
				StackFrame frame = (StackFrame) this.frameStack.get(this.frameStack
						.size() - 1);
				if (frame.childPos < frame.workingBox.correspondingNode
						.getChildren().size()) {
					System.out.println("HAS KID IN OTHER FRAME");
					return true;
				} else {
					System.out.println("NO MORE KIDS");
					return false;
				}
			} else {
				System.out.println("NO MORE KIDS");
				return false;
			}
		}

	}

	public void clearCalculatedRowLimits() {
		this.maxPossibleContentWidth = this.box.contentWidth + 5;
		this.currentRowMaxWidth = this.box.contentWidth;
		this.startXPosition = 0;
		this.currentXPosition = 0;
	}

	public boolean hasBeenSavedForLater(Box b) {
		return (this.savedForLater.indexOf(b) >= 0);
	}

	public boolean hasSavedForLaterChildren() {
		return (this.savedForLater.size() > 0);
	}

	public Box nextChild() {
		CssElement temp = null;

		Box result = null;
		if (((this.savedForLater.size() > 0) && this.nextChildFromSavedElements)
				|| ((this.savedForLater.size() > 0) && (hasMoreRegularChildren() == false))) {
			result = (Box) this.savedForLater.get(0);
		} else {
			while (this.childPos >= this.children.size()) {
				// Remove current box from the visual model (since it's just a
				// container for the actual inline elements)
				this.box.parent.removeChild(this.box);
				Box tempBox = this.box;
				if (this.box.DOMParent != null) {
					this.box.DOMParent.removeChild(this.box);
				}

				// Restore the previous frame
				setTopFrameAsWorkingBoxFrame();
			}
			System.out.println("KIDS: " + this.childPos + " " + this.children.size());
			temp = (CssElement) this.children.get(this.childPos);
			this.childPos++;

			Box child = null;
			if (temp.box != null) {
				child = temp.box;
			} else {
				child = new Box(temp);
				temp.box = child;
			}
			result = child;

		}

		if (this.box.isInline) {
			result.DOMParent = this.box;

			result.parent = this.box.parent;

			result.correspondingNode.setStyle(this.box.correspondingNode.getStyle());

			int pos = this.children.indexOf(result.correspondingNode);

			System.out.println("WITHIN INLINE! " + pos);
			if (pos == 0) {
				result.firstElementInInline = true;
			}
			if (pos == this.children.size() - 1) {
				result.lastElementInInline = true;
			}
		}

		this.lastChild = result;
		return result;
	}

	public boolean currentRowHasOnlyFloats() {
		return this.currentRowHasOnlyFloats;
	}

}
