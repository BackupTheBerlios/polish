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

	@Override
	public boolean getAltKey() {
		return false;
	}

	@Override
	public short getButton() {
		return 0;
	}

	@Override
	public int getClientX() {
		return this.clientX;
	}

	@Override
	public int getClientY() {
		return this.clientY;
	}

	@Override
	public boolean getCtrlKey() {
		return false;
	}

	@Override
	public boolean getMetaKey() {
		return false;
	}

	@Override
	public EventTarget getRelatedTarget() {
		return null;
	}

	@Override
	public int getScreenX() {
		return this.screenX;
	}

	@Override
	public int getScreenY() {
		return this.screenY;
	}

	@Override
	public boolean getShiftKey() {
		return false;
	}

	@Override
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

	@Override
	public int getDetail() {
		return this.detail;
	}

	@Override
	public AbstractView getView() {
		return this.view;
	}

	@Override
	public void initUIEvent(String typeArg, boolean canBubbleArg,
			boolean cancelableArg, AbstractView viewArg, int detailArg) {
		this.initEvent(typeArg, canBubbleArg, cancelableArg);
		this.view = viewArg;
		this.detail = detailArg;
	}
	
}
