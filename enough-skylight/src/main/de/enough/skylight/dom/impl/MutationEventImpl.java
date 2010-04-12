package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.MutationEvent;

public class MutationEventImpl extends EventImpl implements MutationEvent{

	private DomNode relatedNodeArg;
	private String prevValueArg;
	private String attrNameArg;
	private short attrChangeArg;
	private String newValueArg;

	public short getAttrChange() {
		return this.attrChangeArg;
	}

	public String getAttrName() {
		return this.attrNameArg;
	}

	public String getNewValue() {
		return this.newValueArg;
	}

	public String getPrevValue() {
		return this.prevValueArg;
	}

	public DomNode getRelatedNode() {
		return this.relatedNodeArg;
	}

	public void initMutationEvent(String typeArg, boolean canBubbleArg,boolean cancelableArg, DomNode relatedNodeArg, String prevValueArg,String newValueArg, String attrNameArg, short attrChangeArg) {
		// TODO: Do sanity checks here. Having a wrong integer here is hard to track down the road.
		initEvent(typeArg, canBubbleArg, cancelableArg);
		this.relatedNodeArg = relatedNodeArg;
		this.prevValueArg = prevValueArg;
		this.newValueArg = newValueArg;
		this.attrNameArg = attrNameArg;
		this.attrChangeArg = attrChangeArg;
	}

	protected String getNameForChange() {
		switch(this.attrChangeArg) {
			case MODIFICATION: return "Modification";
			case ADDITION: return "Addition";
			case REMOVAL: return "Removal";
			default: throw new RuntimeException("The change type '"+this.attrChangeArg+"' is unknown.");
		}
	}
	
	@Override
	protected String toStringOfProperties() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("change='");
		buffer.append(getNameForChange());
		buffer.append("',prevValue='");
		buffer.append(this.prevValueArg);
		buffer.append("',newValue='");
		buffer.append(this.newValueArg);
		buffer.append("'");
		return buffer.toString();
	}
	
}
