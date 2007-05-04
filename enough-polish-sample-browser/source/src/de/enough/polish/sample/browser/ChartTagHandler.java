package de.enough.polish.sample.browser;

import de.enough.polish.browser.Browser;
import de.enough.polish.browser.TagHandler;
import de.enough.polish.ui.ChartItem;
import de.enough.polish.ui.Container;
import de.enough.polish.util.ArrayList;
import de.enough.polish.util.HashMap;
import de.enough.polish.util.StringTokenizer;
import de.enough.polish.util.TextUtil;
import de.enough.polish.xml.SimplePullParser;

public class ChartTagHandler
  extends TagHandler
{
  private Browser browser;
  private boolean collectData;
  private ArrayList strDataSequences = new ArrayList();
  private String strColors;

  public void register(Browser browser)
  {
    this.browser = browser;
    
    this.browser.addTagHandler("div", this);
    this.browser.addTagHandler("span", this);
  }
  
  public boolean handleTag(Container parentItem, SimplePullParser parser, String tagName, boolean opening, HashMap attributeMap)
  {
    if (TextUtil.equalsIgnoreCase("div", tagName))
    {
      if (opening)
      {
        this.strDataSequences.clear();
        this.strColors = null;
        this.collectData = true;
      }
      else
      {
        // TODO: remove this.
        int[][] dataSequences = parseDataSequences();
        int[] colors = new int[]{ 0xFF0000, 0x00FF00, 0x0000FF };
        
        //#style browserChart
        ChartItem item = new ChartItem("label", dataSequences, colors);
        parentItem.add(item);
        return true;
      }
    }
    else if (TextUtil.equalsIgnoreCase("span", tagName) && this.collectData)
    {
       String cssClass = (String) attributeMap.get("class");
       
       if (TextUtil.equalsIgnoreCase("chartdata", cssClass) && opening)
       {
         parser.next();
         String data = parser.getText();
         this.strDataSequences.add(data);
         
         // TODO: parse and collect data.
         
         return true;
       }
       else if (TextUtil.equalsIgnoreCase("charcolors", cssClass))
       {
         parser.next();
         this.strColors = parser.getText();
       }
    }
    
    return false;
  }

  private int[][] parseDataSequences()
  {
    int num = this.strDataSequences.size();
    int[][] result = new int[num][];
    
    for (int i = 0; i < num; i++)
    {
      String sequence = (String) this.strDataSequences.get(i);
      StringTokenizer st = new StringTokenizer(sequence, ',');
      int[] array = new int[st.countTokens()];
      
      for (int index = 0; st.hasMoreTokens(); index++)
      {
        array[index] = Integer.parseInt(st.nextToken());
      }
      
      result[i] = array;
    }
    
    return result;
  }
}
