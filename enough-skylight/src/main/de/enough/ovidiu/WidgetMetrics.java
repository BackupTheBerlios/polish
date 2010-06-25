
package de.enough.ovidiu;

/**
 *
 * @author Ovidiu Iliescu
 */
public interface WidgetMetrics {

    public static final int WIDGET_BUTTON = 0;
    public static final int WIDGET_TEXT_INPUT = 1;
    public static final int WIDGET_LIST_ITEM = 2;

    public int getWidgetWidth( int widgetType, int contentWidth );
    public int getWidgetHeight( int widgetType, int contentHeight );

}
