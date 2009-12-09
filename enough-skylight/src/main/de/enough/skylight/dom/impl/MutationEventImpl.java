package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.DomNode;
import de.enough.skylight.dom.MutationEvent;

public class MutationEventImpl extends EventImpl implements MutationEvent{

	private DomNode relatedNodeArg;
	private String prevValueArg;
	private String attrNameArg;
	private short attrChangeArg;
	private String newValueArg;

	@Override
	public short getAttrChange() {
		return this.attrChangeArg;
	}

	@Override
	public String getAttrName() {
		return this.attrNameArg;
	}

	@Override
	public String getNewValue() {
		return this.newValueArg;
	}

	@Override
	public String getPrevValue() {
		return this.prevValueArg;
	}

	@Override
	public DomNode getRelatedNode() {
		return this.relatedNodeArg;
	}

	@Override
	public void initMutationEvent(String typeArg, boolean canBubbleArg,boolean cancelableArg, DomNode relatedNodeArg, String prevValueArg,String newValueArg, String attrNameArg, short attrChangeArg) {
		initEvent(typeArg, canBubbleArg, cancelableArg);
		this.relatedNodeArg = relatedNodeArg;
		this.prevValueArg = prevValueArg;
		this.newValueArg = newValueArg;
		this.attrNameArg = attrNameArg;
		this.attrChangeArg = attrChangeArg;
	}

	protected String getNameForChange(short change) {
		switch(change) {
			case MODIFICATION: return "Modification";
			case ADDITION: return "Addition";
			case REMOVAL: return "Removal";
			default: throw new RuntimeException("The change type '"+change+"' is unknown.");
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("MutationEvent:[change='");
		buffer.append(getNameForChange(this.attrChangeArg));
		buffer.append("',prevValue='");
		buffer.append(this.prevValueArg);
		buffer.append("',newValue='");
		buffer.append(this.newValueArg);
		buffer.append("']");
		return super.toString();
	}
}
