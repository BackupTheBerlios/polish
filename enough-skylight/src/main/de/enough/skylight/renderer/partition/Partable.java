package de.enough.skylight.renderer.partition;

import de.enough.skylight.renderer.element.BlockContainingBlock;


public interface Partable {
	public void partition(BlockContainingBlock block, PartitionList partitions);
}
