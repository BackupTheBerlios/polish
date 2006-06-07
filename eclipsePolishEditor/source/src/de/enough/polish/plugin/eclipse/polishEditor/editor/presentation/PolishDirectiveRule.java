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
package de.enough.polish.plugin.eclipse.polishEditor.editor.presentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.ui.text.IJavaColorConstants;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

import de.enough.polish.plugin.eclipse.polishEditor.IPolishConstants;
import de.enough.polish.plugin.eclipse.utils.States;
import de.enough.polish.plugin.eclipse.utils.TokenStore;


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
  
    private TokenStore tokenStore;
    
    // TODO: A state should be more like a stack instead of a bitfield. Problem with stack approch:
    // No garantieed poping at EOL. So a reset is needed.
    private States states;
    
    
    //TODO: Encapsulate this into a tokenizer
    
    // ####################################################
    // Symbols.
    //public static final String POLISH_DIRECTIVE_REGEX = "(//#def|//#else|//#ifdef|//#if|//#elif|//#endif|//#debug|//#style)";
    public static final String POLISH_DIRECTIVE_REGEX = "(//#[a-zA-Z][a-zA-Z]*)";
    public static final String POLISH_ASSIGNMENT_REGEX = "(//#=)";
    public static final String POLISH_COMMENT_REGEX = "(//# )";
    public static final String JAVA_KEYWORD_REGEX = "(this|class|extends|implements|private|public|protected|final|static|boolean|int|float|double|true|false|void)";
    public static final String JAVA_COMMENT_REGEX = "(//)";
    public static final String DOLLAR_LCB_REGEX = "(\\$\\{)";
    public static final String RCB_REGEX = "(\\})";
    public static final String WITHESPACE_REGEX = "([ 	]+)";
    public static final String NAME_REGEX = "([a-zA-Z\\.]+)";
    public static final String STRING_REGEX = "(\")";
    public static final String CATCH_ALL_REGEX = "(\\S+)";
 
    //TODO: Make a function to generate the regEx string automaticly.
    private String REG_EX_PATTERN =
        POLISH_DIRECTIVE_REGEX + "|" +
        POLISH_ASSIGNMENT_REGEX + "|" +
        POLISH_COMMENT_REGEX + "|" +
        JAVA_KEYWORD_REGEX + "|" +
        JAVA_COMMENT_REGEX + "|" +
        DOLLAR_LCB_REGEX + "|" +
        RCB_REGEX + "|" +
        WITHESPACE_REGEX + "|" +
        NAME_REGEX  + "|" +
        STRING_REGEX + "|" +
        CATCH_ALL_REGEX
        ;
          
    public static final int POLISH_DIRECTIVE_SYMBOL = 1;
    public static final int POLISH_ASSIGNMENT_SYMBOL = 2;
    public static final int POLISH_COMMENT_SYMBOL = 3;
    public static final int JAVA_KEYWORD_SYMBOL = 4;
    public static final int JAVA_COMMENT_SYMBOL = 5;
    public static final int DOLLAR_LCB_SYMBOL = 6;
    public static final int RCB_SYMBOL = 7;
    public static final int WHITESPACE_SYMBOL = 8;
    public static final int NAME_SYMBOL = 9;
    public static final int STRING_SYMBOL = 10;
    
    // ####################################################
    // States.
    public static final int JAVA_COMMENT_STATE = 1;
    public static final int POLISH_STATE = 2;
    public static final int JAVA_CODE_STATE = 4;
    public static final int STRING_STATE = 8;
    public static final int POLISH_FUNCTION_STATE = 16;

    private int evalCount = 0;

    
    // ####################################################
    // Constructor.
     public PolishDirectiveRule(TokenStore tokenStore) {
        
        this.pattern = Pattern.compile(this.REG_EX_PATTERN);
        this.matcher = this.pattern.matcher("");
        
        this.tokenStore = tokenStore;
        this.states = new States();
    }
    
    public IToken evaluate(ICharacterScanner scanner) {
        System.out.println("DEBUG:PolishDirectiveRule.evaluate(...):enter.i:"+ ++this.evalCount);
        int c =  Integer.MIN_VALUE; // Clean initialization.
        int readCount = 0; // number of times read was called.
        this.delimiters = scanner.getLegalLineDelimiters();

        // Read the the whole line.
        StringBuffer wholeCommentLineAsStringBuffer = new StringBuffer(200);
        c = scanner.read();
        readCount++;
        
        while( ! charIsEOLorEOF(c)) {
            wholeCommentLineAsStringBuffer.append((char)c);
            c = scanner.read();
            readCount++;
        }
        
        String restOfLine = wholeCommentLineAsStringBuffer.toString();
        
        //Reset the scanner so length calculation remain correct.
        for(int i = 0; i < readCount; i++) {
            scanner.unread();
        }
        this.matcher.reset(restOfLine);
        
        boolean matcherMatchedSomething;
        matcherMatchedSomething = this.matcher.lookingAt();
		
		if(!matcherMatchedSomething){
		
		    // We havent matched anything. The default UNDEFINED token is returned.
            
            for(int i = 0; i < readCount;i++) {
                scanner.read();
            }
            
			return Token.UNDEFINED;
		}
		// Set the state default to the polish default color.
		this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_DEFAULT));
	
		// Set the defaultToken in regard to the current state.
		// TODO: This is problematic because both can be active at the same time thus overriding each other.
		if(this.states.isInState(POLISH_STATE)) {
		    this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_DEFAULT));
		}
		if(this.states.isInState(JAVA_CODE_STATE)) {
		    this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,this.tokenStore.getToken(IJavaColorConstants.JAVA_DEFAULT));
		}
		if(this.states.isInState(JAVA_COMMENT_STATE)) {
		    this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,this.tokenStore.getToken(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT));
		}
		if(this.states.isInState(STRING_STATE)) {
		    this.tokenStore.addToken(IPolishConstants.POLISH_COLOR_STATE_DEFAULT,this.tokenStore.getToken(IJavaColorConstants.JAVA_STRING));
		}
		
		IToken resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_STATE_DEFAULT);
		
//		We have something and it is at the offset. Find out what it is.
		// TODO: Maybe extract the loop, obtain index and value of the symbol and
		// go on with the result, i.e. make a tokenizer.
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
				if(i == POLISH_DIRECTIVE_SYMBOL) { 
				    System.out.println("DEBUG:PolishDirectiveRule.evaluate(...):value:"+value);
				    //this.wholeCommentLine = wholeCommentLineAsStringBuffer.toString();
			        this.states.setState(POLISH_STATE);
				    resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_DIRECTIVE);
				    break;
				}
				
				if(i == NAME_SYMBOL) {
				    
				    if(this.states.isInState(POLISH_STATE)) { // Color only names within a polish directive.
				        // Distinquish between variable and symbol by semantic matching.
				        if(isSymbolName(value)) {
				            resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_SYMBOL);
				            break;
				        }
				        if(isVariableName(value)) {
				            resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_VARIABLE);
				            break;
				        }
				    }
				    break;
				}
				if(i == JAVA_COMMENT_SYMBOL) {
				  
				    this.states.setState(JAVA_COMMENT_STATE);
				    resultToken = this.tokenStore.getToken(IJavaColorConstants.JAVA_SINGLE_LINE_COMMENT);
				    break;
				}
				if(i == DOLLAR_LCB_SYMBOL) {
				    
				    if( ! this.states.isInState(JAVA_COMMENT_STATE)) { // Color in every state except java comments.
				        this.states.addState(POLISH_FUNCTION_STATE);
				        resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION);
				    }
				    break;
				}
				if(i == RCB_SYMBOL) {
				   
				    if( ! this.states.isInState(JAVA_COMMENT_STATE)) {
				        this.states.removeState(POLISH_FUNCTION_STATE);
				        resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_FUNCTION_PUNCTATION);
				    }
				    break;
				}
				
				
				if(i == POLISH_COMMENT_SYMBOL) {
				    
				    //this.wholeCommentLine = wholeCommentLineAsStringBuffer.toString();
			        this.states.setState(JAVA_CODE_STATE);
				    resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_DIRECTIVE);
				    break;
				}
				if(i == POLISH_ASSIGNMENT_SYMBOL) {
				    
				    //this.wholeCommentLine = wholeCommentLineAsStringBuffer.toString();
			        this.states.setState(JAVA_CODE_STATE);
				    resultToken = this.tokenStore.getToken(IPolishConstants.POLISH_COLOR_DIRECTIVE);
				    break;
				}
				if(i == JAVA_KEYWORD_SYMBOL) {
				    if(this.states.isInState(JAVA_CODE_STATE)) {
				        resultToken = this.tokenStore.getToken(IJavaColorConstants.JAVA_KEYWORD);
				    }
				    break;
				}
				if(i == STRING_SYMBOL) {
				    if(this.states.isInState(STRING_STATE) && (!this.states.isInState(JAVA_COMMENT_STATE))) {
				        this.states.removeState(STRING_STATE);
				        resultToken = this.tokenStore.getToken(IJavaColorConstants.JAVA_STRING);
				        break;
				    }
				    
				    if( ! this.states.isInState(STRING_STATE) && (!this.states.isInState(JAVA_COMMENT_STATE))) {
				        this.states.addState(STRING_STATE);
				        resultToken = this.tokenStore.getToken(IJavaColorConstants.JAVA_STRING);
				        break;
				    }
				    break;
				}

				//System.out.println("PolishDirectiveRule.evaluate(...):Symbol not processed:"+value+"X.i:"+i);
				break;
			}
 		}
        return resultToken;
    }

    
  
    private boolean isVariableName(String value) {
        // TODO ricky implement isVariableName
        return false;
    }

 
    private boolean isSymbolName(String value) {
        // TODO ricky implement isSymbolName
        return false;
    }

    private boolean charIsEOLorEOF(int c) {
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
