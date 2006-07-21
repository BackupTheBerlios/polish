package de.enough.polish.postcompile.renaming;

import java.util.Iterator;
import java.util.Map;

public final class ClassRenamingHelper
{
  public static String doRenaming(String str, Map renamingMap)
  {
    if (str == null)
      {
        return null;
      }
    
    Iterator it = renamingMap.entrySet().iterator();
    
    while (it.hasNext())
      {
        Map.Entry entry = (Map.Entry) it.next();
        str = str.replaceAll((String) entry.getKey(), (String) entry.getValue());
      }
    
    return str;
  }

  public static String[] doRenaming(String[] array, Map renamingMap)
  {
    if (array == null)
      {
        return null;
      }
    
    for (int i = 0; i < array.length; i++)
      {
        array[i] = doRenaming(array[i], renamingMap);
      }
    
    return array;
  }
}
