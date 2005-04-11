/*
 * Created on 09.04.2005
 */
package de.enough.polish.plugin.eclipse.utils;


public class States{
    
    private int state;
    
    public States() {
        this.state = 0;
    }
    
    public boolean isInState(int stateToTest) {
        return (this.state & stateToTest) == stateToTest;
    }
    
    public void addState(int stateToAdd) {
        this.state = this.state | stateToAdd;
    }
    
    public void setState(int stateToSet) {
        this.state = stateToSet;
    }

  public void removeState(int stateToRemove) {
      this.state = this.state & ~stateToRemove;
  }


}