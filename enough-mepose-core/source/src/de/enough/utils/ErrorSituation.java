/*
 * Created on 05-Jun-2005
 */
package de.enough.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ErrorSituation {

    // warningToken would be nice, too.
    private List errorTokens;
    
    public ErrorSituation() {
        this.errorTokens = new ArrayList();
    }
    
    public void addErrorToken(String errorToken){
        if (errorToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.addErrorToken(...):Parameter 'errorToken' is null.");
        }
        this.errorTokens.add(errorToken);
    }
    
    public void removeErrorToken(String errorToken){
        if (errorToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.removeErrorToken(...):Parameter 'errorToken' is null.");
        }
        this.errorTokens.remove(errorToken);
    }

    public boolean isErroneousSituation(){
        return this.errorTokens.isEmpty();
    }
}
