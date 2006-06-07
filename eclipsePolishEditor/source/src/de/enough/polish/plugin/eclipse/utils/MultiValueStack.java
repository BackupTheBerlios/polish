/*
 * Created on 09.04.2005
 */
package de.enough.polish.plugin.eclipse.utils;



class MultiValueStack{
    
    public static final int ELEMENT_NOT_IN_STACK = -1;
    public static final int NO_ELEMENTS_IN_STACK = -1;

    private int indexOfTopElement;
    private int[] stack;
    
    public MultiValueStack() {
        this.stack = new int[10];
        this.indexOfTopElement = -1;
    }
    
    // ####################################################
    // Destructive Stack operations.
    
    public void pushElement(int element){
    	enlargeCapacityForOneMoreElement();

    	this.indexOfTopElement++;
    	this.stack[this.indexOfTopElement] = element;
    }

	public int popElement(){
	    int poppedElement;
	    if(this.indexOfTopElement < 0){
	        poppedElement = NO_ELEMENTS_IN_STACK;
	    }
	    else{
	        poppedElement = this.stack[this.indexOfTopElement];
	        this.indexOfTopElement--;
	    }
    	return poppedElement;
    }

	public void addElementToTop(int elementToAdd){
    	int topElement = this.stack[this.indexOfTopElement];
    	this.stack[this.indexOfTopElement] = toBaseElementAddElement(topElement, elementToAdd);
    }
    

    public void removeElementFromTop(int elementToREmove){
        int topElement = this.stack[this.indexOfTopElement];
        this.stack[this.indexOfTopElement] = fromBaseElementRemoveElement(topElement, elementToREmove);
    }

    // ####################################################
    // Investigating Stack operations.

    public int topElement(){
    	return this.stack[this.indexOfTopElement];
    }

    public boolean elementIsOnTop(int elementToTest){
    	int topElement = this.stack[this.indexOfTopElement];
    	return baseElementContainsElementToTest(topElement, elementToTest);
    }
    
    /*
     * Returns the 0 based position of an element to test within the stack. If the element is on top,
     * 0 is returned, if it is one below the top 1 is returned and so on. -1 is returned if the element
     * is not in the stack.
     */
    public int positionOfElementInStack(int elementToTest){
        int elementAtCurrentPosition;
        for(int currentPositionInStack = this.indexOfTopElement; currentPositionInStack >= 0; currentPositionInStack--){
            elementAtCurrentPosition = this.stack[currentPositionInStack];
            if(baseElementContainsElementToTest(elementAtCurrentPosition,elementToTest)){
                return this.indexOfTopElement - currentPositionInStack;
            }
        }
    	return ELEMENT_NOT_IN_STACK;
    }
    
    public boolean elementIsInStack(int element){
        return (positionOfElementInStack(element) != ELEMENT_NOT_IN_STACK);
    }
    
    // ####################################################
    // Helping methods.
    
    private void enlargeCapacityForOneMoreElement() {
	    int lastPossibleIndex = this.stack.length - 1;
	    
		if(this.indexOfTopElement >= lastPossibleIndex){

		    int[] newStack = new int[this.stack.length * 2];
		    System.arraycopy(this.stack,0,newStack,0,this.stack.length);
		}
	}

    private boolean elementIsValid(int element) {
		return (0 < element);
	}

    // ####################################################
    // Bit methods.
    
    private boolean baseElementContainsElementToTest(int baseElement, int elementToTest) {
        return (baseElement & elementToTest) == elementToTest;
    }

    private int toBaseElementAddElement(int baseElement, int elementToAdd) {
        return baseElement | elementToAdd;
    }

    private int fromBaseElementRemoveElement(int baseElement, int elementToREmove) {
        return baseElement & ~elementToREmove;
    }


}