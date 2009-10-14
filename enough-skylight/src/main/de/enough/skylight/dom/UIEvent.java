package de.enough.skylight.dom;
public interface UIEvent extends Event {
    public AbstractView getView();

    public int getDetail();

    public void initUIEvent(String typeArg, 
                            boolean canBubbleArg, 
                            boolean cancelableArg, 
                            AbstractView viewArg, 
                            int detailArg);

}