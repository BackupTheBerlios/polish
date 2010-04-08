package de.enough.skylight.dom.impl;

import de.enough.skylight.dom.AbstractView;
import de.enough.skylight.dom.EventTarget;
import de.enough.skylight.dom.MouseEvent;

public class MouseEventImpl extends EventImpl implements MouseEvent {

	private AbstractView view;
	private int detail;
	private int screenX;
	private int screenY;
	private int clientX;
	private int clientY;

	public boolean getAltKey() {
		return false;
	}

	public short getButton() {
		return 0;
	}

	public int getClientX() {
		return this.clientX;
	}

	public int getClientY() {
		return this.clientY;
	}

	public boolean getCtrlKey() {
		return false;
	}

	public boolean getMetaKey() {
		return false;
	}

	public EventTarget getRelatedTarget() {
		return null;
	}

	public int getScreenX() {
		return this.screenX;
	}

	public int getScreenY() {
		return this.screenY;
	}

	public boolean getShiftKey() {
		return false;
	}

	public void initMouseEvent(String typeArg, boolean canBubbleArg,
			boolean cancelableArg, AbstractView viewArg, int detailArg,
			int screenXArg, int screenYArg, int clientXArg, int clientYArg,
			boolean ctrlKeyArg, boolean altKeyArg, boolean shiftKeyArg,
			boolean metaKeyArg, short buttonArg, EventTarget relatedTargetArg) {
		this.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, detailArg);
		this.screenX = screenXArg;
		this.screenY = screenYArg;
		this.clientX = clientXArg;
		this.clientY = clientYArg;
		// All other parameters are not supported.
	}

	public int getDetail() {
		return this.detail;
	}

	public AbstractView getView() {
		return this.view;
	}

	public void initUIEvent(String typeArg, boolean canBubbleArg,
			boolean cancelableArg, AbstractView viewArg, int detailArg) {
		this.initEvent(typeArg, canBubbleArg, cancelableArg);
		this.view = viewArg;
		this.detail = detailArg;
	}
	
	@Override
	protected String toStringOfProperties() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("x:");
		buffer.append(this.screenX);
		buffer.append(",y:");
		buffer.append(this.screenY);
		return buffer.toString();
	}
	
}
