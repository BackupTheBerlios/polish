/*
 * Created on Mar 29, 2005 at 2:11:52 PM.
 * 
 * Copyright (c) 2005 Robert Virkus / Enough Software
 *
 * This file is part of J2ME Polish.
 *
 * J2ME Polish is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * J2ME Polish is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with J2ME Polish; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * Commercial licenses are also available, please
 * refer to the accompanying LICENSE.txt or visit
 * http://www.j2mepolish.org for details.
 */
package de.enough.polish.plugin.eclipse.polishEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.internal.runtime.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;


/**
 * <p></p>
 *
 * <p>Copyright Enough Software 2005</p>
 * <pre>
 * history
 *        Mar 29, 2005 - ricky creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class PolishDirectiveRule implements IRule {

    private char[][] delimiters;
    
    private Matcher matcher;
    private Pattern pattern;
    
    private IToken defaultToken;
    private IToken directiveToken;
    private IToken nameToken;
    
    private String REG_EX_PATTERN =
        "(//#def)" + "|" + //1
        "(//#else)" + "|" + //2
        "(//#ifdef)" + "|" + //3 // Order is important: beware that the longer sequence is before the shorter one if both have same prefix.
        "(//#if)" + "|" + //4
        "(//#elif)" + "|" + //5
        "(//#endif)" + "|" + //6
        "(//#debug)" + "|" + //7
        "(//#=)" + "|" + //8
        "([a-zA-Z\\.]+)" + "|" + //9
        "(//)" + "|" + //10
        "([ ]+)" + "|" + //11
        "(\\$\\{|\\})" //12
        ;
    
    public static final int DIRECTIVE_SYMBOL = 1;
    public static final int ASSIGNMENT_SYMBOL = 2;
    public static final int NAME_SYMBOL = 4;
    public static final int COMMENT_SYMBOL = 8;
    public static final int FUNCTION_SYMBOL = 16;
    
    private int state;
    public static final int COMMENT_STATE = 1;
    public static final int DIRECTIVE_STATE = 2;
    public static final int ASSIGNMENT_STATE = 4;
    
    private boolean isInState(int stateToTest) {
        return (this.state & stateToTest) == stateToTest;
    }
    
    private void addState(int stateToAdd) {
        this.state = this.state | stateToAdd;
    }
    
    private void removeState(int stateToRemove) {
        this.state = this.state & ~stateToRemove;
    }
    
    private void setState(int stateToSet) {
        this.state = stateToSet;
    }
    public PolishDirectiveRule() {
        this.pattern = Pattern.compile(this.REG_EX_PATTERN);
        this.matcher = this.pattern.matcher("");
    }
    
    public void setDefaultToken(IToken token) {
        Assert.isNotNull(token);
        this.defaultToken = token;
    }
    
    public void setDirectiveToken(IToken token) {
        Assert.isNotNull(token);
        this.directiveToken = token;
    }
    
    public void setNameToken(IToken token) {
        Assert.isNotNull(token);
        this.nameToken = token;
    }
   
    /* (non-Javadoc)
     * @see org.eclipse.jface.text.rules.IRule#evaluate(org.eclipse.jface.text.rules.ICharacterScanner)
     */
    public IToken evaluate(ICharacterScanner scanner) {
        
        Assert.isNotNull(this.defaultToken);
        Assert.isNotNull(this.directiveToken);
        Assert.isNotNull(this.nameToken);
        
        
        int c =  Integer.MIN_VALUE;
        int readCount = 0; // number of times read was called.
        this.delimiters = scanner.getLegalLineDelimiters();

        // Read the the whole line.
        StringBuffer stringBuffer = new StringBuffer(100);
        c = scanner.read();
        readCount++;
        
        while( ! charIsEOLorEOF(c)) {
            stringBuffer.append((char)c);
            c = scanner.read();
            readCount++;
        }
        //System.out.println("Buffer:"+stringBuffer.toString()+"readCount:"+readCount);
        
        //Reset the scanner so length calculation remain correct.
        for(int i = 0; i < readCount; i++) {
            scanner.unread();
        }
        String commentString = stringBuffer.toString();
        this.matcher.reset(commentString);
        
        boolean matcherMatchedSomething;
        matcherMatchedSomething = this.matcher.lookingAt();
		
		if(!matcherMatchedSomething){
			//System.out.println("AAA Not matched anything.");
		    
		    // We havent matched anything. The default UNDEFINED token is returned.
			return Token.UNDEFINED;
		}
		//System.out.println("AAA matched something.");
		IToken token = this.defaultToken;
		
//		We have something and is at the offset. Find out what it is.
		for(int i = 1; i <= this.matcher.groupCount();i++){
			String value = this.matcher.group(i);
			
			// In theory we pass this condition exactly once. the tokenlanguage is LL so only one
			// group match. And because we have some groups we do have a group != "", too.
            	if((value != null) && (value.length() > 0)){ // Arcording to API sometimes "" can be a group. "a*" as an example.
            	    //System.out.println("AAA group found:"+value+":i:"+i);
            	    int lengthOfValue = value.length();
            	    // Set the scanner to the right offset.
				for(int j = 0; j < lengthOfValue; j++) {
				    scanner.read();
				}
				
//				DANGER: Adept this numbers to the pattern array indecies. The groups ids should match the numbers.
				if(1 <= i && i <= 8) { 
				    //System.out.println("AAA token generated:directive.");
				    this.state = DIRECTIVE_STATE;
				    token = this.directiveToken;
				}
//				else if(8 <= i && i <= 8) {
//				    //System.out.println("AAA token generated:name.");
//				    this.state = (DIRECTIVE_STATE | ASSIGNMENT_STATE);
//				    token = this.directiveToken;
//				}
				else if(9 <= i && i <= 9) {
				    //System.out.println("AAA token generated:name.");
				    if(isInState(DIRECTIVE_STATE)) { // Color only names within a polish directive.
				        token = this.nameToken;
				    }
//				    if(isInState(ASSIGNMENT_STATE)) {
//				        if(value.)
//				    }
				}
				else if(10 <= i && i <= 10) {
				    this.state = COMMENT_STATE;  
				}
				else if(12 <= i && i <= 12) {
				    if(isInState(DIRECTIVE_STATE)) {
				        token = this.directiveToken;
				    }
				}
				
				break;
			}
		}
        return token;
    }

    
     private boolean charIsEOLorEOF(int c) {
        Assert.isNotNull(this.delimiters);
        if(c == ICharacterScanner.EOF) {
            return true;
        }
        for(int i = 0; i < this.delimiters.length; i++) {
            // Probably we should look at the whole delimiter string to catch CR LF. So read and  unread.
            if(c == this.delimiters[i][0]) {
                return true;
            }
        }
        return false;
    }
}
