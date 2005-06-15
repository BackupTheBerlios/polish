/*
 * Created on 05-Jun-2005
 */
package de.enough.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*
 * This class describes actions and steps which may be done and which haven't been done at the moment.
 * Every successToken is a well done action and errorTokens indicate not jet done actions. Having errorTokens
 * means having a erroneous situation. To resolve it a series of actions denoted by successTokens should
 * be carried out.
 */
public class ErrorSituation {

    private List errorTokens;
    private List successTokens;
    
    private Map errorTokenToValueMapping;
    private Map successTokenToValueMapping;
    private Map errorTokenToErrorResolveStepsMapping;
    
    public ErrorSituation() {
        this.errorTokens = new ArrayList();
        this.successTokens = new ArrayList();
        this.errorTokenToValueMapping = new HashMap();
        this.successTokenToValueMapping = new HashMap();
        this.errorTokenToErrorResolveStepsMapping = new HashMap();
    }
    
    public void registerSuccessToken(String successToken,String value){
        if(successToken == null || value == null){
            throw new IllegalArgumentException("ERROR:ErrorSituation.addSuccessToken(...): Some Parameters are null.");
        }
        this.successTokenToValueMapping.put(successToken,value);
    }
    
    public void registerErrorToken(String errorToken,String value){
        if(errorToken == null || value == null){
            throw new IllegalArgumentException("ERROR:ErrorSituation.addErrorToken(...): Some Parameters are null.");
        }
        this.errorTokenToValueMapping.put(errorToken,value);
    }
    
    public void registerErrorResolveSteps(String errorToken,List resolveSteps){
        if(errorToken == null || resolveSteps == null){
            throw new IllegalArgumentException("ERROR:ErrorSituation.addErrerResolveStreps(...): Some Parameters are null.");
        }
        for(Iterator iterator = resolveSteps.iterator();iterator.hasNext();){
            String resolveStep;
            try{
                resolveStep = (String)iterator.next();
            }
            catch(ClassCastException exception){
                throw new IllegalArgumentException("ERROR:ErrorSituation.addErrorResolveStreps(...): Parameter 'resolveSteps' contains a non-String element.");
            }
            if( ! this.successTokens.contains(resolveStep)){
                throw new IllegalArgumentException("ERROR:ErrorSituation.addErrorResolveStreps(...): Parameter 'resolveSteps' contains a element not beeing a successToken. Register the element as successToken.");
            }
        }
        this.errorTokenToErrorResolveStepsMapping.put(errorToken,resolveSteps);
    }
    
    //TODO: Remove methods for errorTokens and successTokens.
    
    /*
     * Returns a List with success token which should be carried out in the given order to resolve an error.
     */
    public List resolveError(String errorToken){
        if (errorToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.resolveError(...):Parameter 'errorToken' is null.");
        }
        if( ! this.errorTokens.contains(errorToken)){
            throw new IllegalArgumentException("ERROR:ErrorSituation.resolveError(...):Parameter 'errorToken' is not registered as a errorToken.");
        }
        List resolveSteps = (List)this.errorTokenToErrorResolveStepsMapping.get(errorToken);
        
        return  resolveSteps == null? new ArrayList():resolveSteps;
    }
    
    public void addErrorToken(String errorToken){
        if (errorToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.addErrorToken(...):Parameter 'errorToken' is null.");
        }
        if( ! this.errorTokenToValueMapping.containsKey(errorToken)){
            throw new IllegalArgumentException("ERROR:ErrorSituation.addErrorToken(...):Parameter 'errorToken' is not registered as a errorToken.");
        }
        this.errorTokens.add(errorToken);
    }
    
    public void removeErrorToken(String errorToken){
        if (errorToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.removeErrorToken(...):Parameter 'errorToken' is null.");
        }
        if( ! this.errorTokenToValueMapping.containsKey(errorToken)){
            throw new IllegalArgumentException("ERROR:ErrorSituation.removeErrorToken(...):Parameter 'errorToken' is not registered as a errorToken.");
        }
        this.errorTokens.remove(errorToken);
    }

    public void addSuccessToken(String successToken){
        if (successToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.addSuccessToken(...):Parameter 'successToken' is null.");
        }
        if( ! this.successTokenToValueMapping.containsKey(successToken)){
            throw new IllegalArgumentException("ERROR:ErrorSituation.addSuccessToken(...):Parameter 'successToken' is not registered as a successToken.");
        }
        this.successTokens.add(successToken);
    }
    
    public void removeSuccessToken(String successToken){
        if (successToken == null) {
            throw new IllegalArgumentException("ERROR:ErrorSituation.removeSuccessToken(...):Parameter 'successToken' is null.");
        }
        if( ! this.successTokenToValueMapping.containsKey(successToken)){
            throw new IllegalArgumentException("ERROR:ErrorSituation.removeSuccessToken(...):Parameter 'successToken' is not registered as a successToken.");
        }
        this.successTokens.remove(successToken);
    }

    public boolean isErroneousSituation(){
        return this.errorTokens.isEmpty();
    }
    
    /*
     * Returns a List with success token which should be carried out in the given order to resolve
     * the whole erroneous situation.
     * TODO: This is a CSP.
     */
//    public List resolveErrorSituation(){
//        throw new UnsupportedOperationException("ERROR:ErrorSituation.resolveErrorSituation:not implemented jet.");
//    }
    
}
