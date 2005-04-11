/*
 * Created on 09-Apr-2005
 */

package de.enough.testutils;
import java.lang.reflect.Field;
import junit.framework.Assert;

/**
 * Provides access to private members in classes.
 */
public class PrivateAccessor {
    
  public static Object getPrivateField (Object objectToQuery, String fieldNameToGet) {
    
    //Check we have valid arguments
    Assert.assertNotNull(objectToQuery);
    Assert.assertNotNull(fieldNameToGet);
    
    //Go and find the private field...
    final Field declaredFields[] = objectToQuery.getClass().getDeclaredFields();
    
    for (int currentFieldIndex = 0; currentFieldIndex < declaredFields.length; ++currentFieldIndex) {
      
        String nameOfDeclaredField = declaredFields[currentFieldIndex].getName();
        
        if (fieldNameToGet.equals(nameOfDeclaredField)) {
	        try {
	            declaredFields[currentFieldIndex].setAccessible(true);
	            return declaredFields[currentFieldIndex].get(objectToQuery);
	        }
	        catch (IllegalAccessException ex) {
	            Assert.fail("IllegalAccessException accessing "+fieldNameToGet);
	        }
        }
    }
    Assert.fail("Field '" + fieldNameToGet +"' not found on object"+objectToQuery);
    return null;
  }
  
}

