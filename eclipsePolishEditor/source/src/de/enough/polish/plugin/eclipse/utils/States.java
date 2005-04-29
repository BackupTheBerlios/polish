/*
 * Created on 09.04.2005
 */
package de.enough.polish.plugin.eclipse.utils;

import org.eclipse.jface.util.Assert;




public class States{
    
    private int state;
    
    public States() {
        this.state = 0;
    }
    
    public void setState(int stateToSet) {
        Assert.isLegal(stateToSet >= 0);
        this.state = stateToSet;
    }
    
    public void reset() {
        this.state = 0;
    }
    
    public boolean isInState(int stateToTest) {
        Assert.isLegal(stateToTest >= 0);
        return (this.state & stateToTest) == stateToTest;
    }
    
    public void addState(int stateToAdd) {
        Assert.isLegal(stateToAdd >= 0);
        this.state = this.state | stateToAdd;
    }
    
    public void removeState(int stateToRemove) {
        Assert.isLegal(stateToRemove >= 0);
        this.state = this.state & ~stateToRemove;
    }


}