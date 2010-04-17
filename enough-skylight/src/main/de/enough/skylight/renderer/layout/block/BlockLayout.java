package de.enough.skylight.renderer.layout.block;

import de.enough.polish.ui.Item;
import de.enough.skylight.renderer.css.HtmlCssElement;
import de.enough.skylight.renderer.element.BlockContainingBlock;
import de.enough.skylight.renderer.element.InlineContainingBlock;
import de.enough.skylight.renderer.element.LayoutDescriptor;
import de.enough.skylight.renderer.element.view.ContentView;
import de.enough.skylight.renderer.linebox.InlineLinebox;
import de.enough.skylight.renderer.linebox.InlineLineboxList;
import de.enough.skylight.renderer.node.CssElement;
import de.enough.skylight.renderer.partition.Partition;
import de.enough.skylight.renderer.partition.PartitionList;

public class BlockLayout {

	InlineLineboxList lineboxes;

	BlockContainingBlock block;

	CssElement cssElement;

	int blockAvailableWidth;

	int width;

	int height;

	public BlockLayout(BlockContainingBlock block) {
		this.block = block;
		this.blockAvailableWidth = block.getAvailableContentWidth();
		LayoutDescriptor layoutDescriptor = this.block.getLayoutDescriptor();
		if (layoutDescriptor != null) {
			this.cssElement = layoutDescriptor.getCssElement();
		}
	}

	public void layoutPartitions(PartitionList partitions,
			InlineContainingBlock block) {
		this.lineboxes = getLineboxes(partitions, block);
	}

	InlineLineboxList getLineboxes(PartitionList partitions,
			InlineContainingBlock block) {
		InlineLineboxList lineboxes = new InlineLineboxList();
		InlineLinebox linebox = null;

		int top = 0;

		if (partitions.size() > 0) {
			int index = 0;
			while (index < partitions.size()) {
				Partition partition = partitions.get(index);

				if (linebox == null) {
					linebox = new InlineLinebox(this.blockAvailableWidth);
					linebox.setBlockRelativeTop(top);
					lineboxes.add(linebox);
				}

				if (linebox.fits(partition)
						&& !partition.hasAttribute(Partition.ATTRIBUTE_NEWLINE)) {
					addToLinebox(linebox, partition);
					index++;
				} else {
					if (partition.hasAttribute(Partition.ATTRIBUTE_NEWLINE)) {
						index++;
					} else if (partition
							.hasAttribute(Partition.ATTRIBUTE_WHITESPACE)) {
						// ignore whitespace at line end
						continue;
					} else {
						// align linebox
						alignLinebox(linebox, this.cssElement,
								this.blockAvailableWidth);

						top += linebox.getLineHeight();

						int lineWidth = linebox.getWidth();
						if (lineWidth > this.width) {
							this.width = lineWidth;
						}

						linebox = null;
					}
				}
			}

			if (linebox != null) {
				// align linebox
				alignLinebox(linebox, this.cssElement,
						this.blockAvailableWidth);

				top += linebox.getLineHeight();

				int lineWidth = linebox.getWidth();
				if (lineWidth > this.width) {
					this.width = lineWidth;
				}
				
				this.height = top;
			}
		}

		return lineboxes;
	}

	void alignLinebox(InlineLinebox linebox, CssElement cssElement,
			int blockAvailableWidth) {
		int left = 0;
		
		if (cssElement != null) {
			if (cssElement.isTextAlign(HtmlCssElement.TextAlign.CENTER)) {
				left = (blockAvailableWidth - linebox.getTrimmedWidth()) / 2;
			} else if (cssElement.isTextAlign(HtmlCssElement.TextAlign.RIGHT)) {
				left = blockAvailableWidth - linebox.getTrimmedWidth();
			}
		}

		linebox.setBlockRelativeLeft(left);
	}

	public void addToLinebox(InlineLinebox linebox, Partition partition) {
		Item partitionItem = partition.getParentItem();
		LayoutDescriptor layoutDescriptor = ContentView
				.getLayoutDescriptor(partitionItem);
		layoutDescriptor.getLineboxes().add(linebox);

		partition.setLineboxRelativeX(linebox.getWidth());
		linebox.addPartition(partition);
	}

	public InlineLineboxList getLineBoxes() {
		return this.lineboxes;
	}

	public int getLayoutWidth() {
		return this.width;
	}

	public int getLayoutHeight() {
		return this.height;
	}
}
