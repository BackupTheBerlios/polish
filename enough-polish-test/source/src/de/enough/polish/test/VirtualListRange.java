package de.enough.polish.test;

import de.enough.polish.ui.Canvas;

public class VirtualListRange {
	int FACTOR_BUFFER = 3;

	int FACTOR_LIMIT = 3;

	boolean set;

	int start;

	int end;

	int total;

	int range;

	int offset;

	int referenceHeight;

	int availableHeight;

	int totalHeight;

	int listHeight;

	int listRange;

	int maximumY;

	int minimumY;

	public VirtualListRange(int referenceHeight, int availableHeight,
			int minimumItems) {
		this.referenceHeight = referenceHeight;
		this.availableHeight = availableHeight;

		this.range = getRange(minimumItems);
		this.offset = -1;
	}

	int getLimitHeight() {
		return this.availableHeight / FACTOR_LIMIT;
	}

	int getBufferHeight() {
		return this.availableHeight * FACTOR_BUFFER;
	}

	void setRange(int start, int end, int total) {
		this.start = start;
		this.end = end;
		this.total = total;

		this.totalHeight = total * this.referenceHeight;
		this.listHeight = (end - start) * this.referenceHeight;

		this.minimumY = (start * this.referenceHeight) + getLimitHeight();
		this.maximumY = ((start * this.referenceHeight) + this.listHeight)
				- getLimitHeight();

		// handle list start / end / overlapping
		if (start == 0) {
			this.minimumY = Integer.MIN_VALUE;
		}

		if (end == total - 1) {
			this.maximumY = Integer.MAX_VALUE;
		}

		this.set = true;
	}

	public boolean belowRange(long y) {
		boolean result = y < this.minimumY;
		//#mdebug debug
		if (result) {
			System.out.println("list selection is below range, updating");
		}
		//#enddebug
		return result;
	}

	public boolean overRange(long y) {
		boolean result = (y + this.availableHeight) > this.maximumY;
		//#mdebug debug
		if (result) {
			System.out.println("list selection is over range, updating");
		}
		//#enddebug
		return result;
	}

	public void update(long y, int direction, int total) {
		int start = 0;
		int end = 0;
		long startY = 0;
		long endY = 0;

		if (direction == Canvas.DOWN) {
			endY = y + this.availableHeight;
			startY = endY - (this.range * this.referenceHeight);
			endY += getLimitHeight();
		} else {
			startY = y;
			endY = startY + this.range * this.referenceHeight;
			startY -= getLimitHeight();
		}

		start = (int) startY / this.referenceHeight;
		end = (int) endY / this.referenceHeight;

		if (start <= 0) {
			start = 0;
		}

		if (end > this.total - 1) {
			end = this.total - 1;
		}

		setRange(start, end, total);
	}

	int getRange(int minimum) {
		int neededRange = getBufferHeight() / this.referenceHeight;

		if (this.availableHeight % this.referenceHeight > 0) {
			neededRange++;
		}

		if (minimum > neededRange) {
			return minimum;
		} else {
			return neededRange;
		}
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return this.offset;
	}

	public boolean isSet() {
		return this.set;
	}

	public int getStart() {
		return this.start;
	}

	public int getEnd() {
		return this.end;
	}

	public int getTotal() {
		return this.total;
	}

	public int getRange() {
		return this.range;
	}

	public int getReferenceHeight() {
		return referenceHeight;
	}

	public int getAvailableHeight() {
		return availableHeight;
	}

	public int getTotalHeight() {
		return totalHeight;
	}

	public int getListHeight() {
		return listHeight;
	}

	public int getMinimumY() {
		return maximumY;
	}

	public int getMaximumY() {
		return minimumY;
	}
}
