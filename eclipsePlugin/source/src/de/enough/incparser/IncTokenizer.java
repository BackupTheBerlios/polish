/*
 * Created on May 10, 2006 at 3:25:19 PM.
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
package de.enough.incparser;


/**
 * match... checks the current position and starts from there. Return boolean if
 * if could match anything. Always advances the position if it could match anything.
 * 
 * is... checks an abitary character. There are always two versions. The one with
 * one less parameter uses the current position.
 * <br>Copyright Enough Software 2005
 * <pre>
 * history
 *        May 10, 2006 - rickyn creation
 * </pre>
 * @author Richard Nkrumah, Richard.Nkrumah@enough.de
 */
public class IncTokenizer {

    public class CharacterEvent{
        
        private long offset;
        private long length;
        private String changedText;
        
        public CharacterEvent(long offset, long length, String changedText) {
            this.offset = offset;
            this.length = length;
            this.changedText = changedText;
        }
        public String getChangedText() {
            return this.changedText;
        }
        public void setChangedText(String changedText) {
            this.changedText = changedText;
        }
        public long getLength() {
            return this.length;
        }
        public void setLength(long length) {
            this.length = length;
        }
        public long getOffset() {
            return this.offset;
        }
        public void setOffset(long offset) {
            this.offset = offset;
        }
    }
    
    private String sourceText = "";
//    private List allTokens = new LinkedList();
    private int pos = -1;
    private int startIndex;
    private int endIndex;
    private int type;
    
    
    
    public String getSourceText() {
        return this.sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public void newCharacterEvent(CharacterEvent characterEvent) {
        // - See if which token become dirty with this characterEvent
    }
    
    public Token nextToken() {
        if(this.pos >= this.sourceText.length()) {
            return null;
        }
        loop:
        while(true) {
            if( ! hasPeekChar()) {
                return null;
            }
            char character = peekNextChar();
            consume();
            switch(character) {
                case ' ':
                case '\t':
                case '\n':{matchWhitespace();break loop;}
                case 'a':
                case 'b':
                case 'c':{matchName();break loop;}
                default:{return new Token(this.pos,1,character+"",Types.UNKNOWN);}
            }
        }
        
        Token token = new Token(this.startIndex,
                         this.endIndex-this.startIndex+1,
                         this.sourceText.substring(this.startIndex,this.endIndex),
                         this.type);
        token = handleSpecialToken(token);
        return token;
    }
    
    
    /**
     * Alters the token to return. May be used for keyword regognition.
     * @param token
     * @return May be null if this method decides to.
     */
    protected Token handleSpecialToken(Token token) {
        return token;
    }
    
    protected boolean matchWhitespace() {
        if( ! isWhitespace()) {
            return false;
        }
        this.startIndex = this.pos;
        while(hasPeekChar() && isWhitespace(peekNextChar())) {
            consume();
        }
        this.endIndex = this.pos;
        this.type = Types.WHITESPACE;
        return true;
    }

    protected boolean matchName() {
        if( ! isRange('a','c')) {
            return false;
        }
        this.startIndex = this.pos;
        while(hasPeekChar() && isRange(peekNextChar(),'a','c')){
            consume();
        }
        this.endIndex = this.pos;
        this.type = Types.NAME;
        return true;
    }

//    protected boolean matchRange(char c1, char c2){
//        char charAtPos = this.sourceText.charAt(this.pos);
//        if (charAtPos < c1 || charAtPos > c2) {
//            return false;
//        }
//        this.pos++;
//        return true;
//    }
    
    protected boolean isWhitespace() {
        return isWhitespace(get());
    }
    
    protected boolean isWhitespace(char character) {
        if(is(character,' ') || is(character,'\t') || is(character,'\n')) {
            return true;
        }
        return false;
    }
    
    protected boolean isRange(char character,char lowerBound, char upperBound) {
        if (character < lowerBound || character > upperBound) {
            return false;
        }
        return true;
    }
    
    protected boolean isRange(char lowerBound, char upperBound) {
        char character = get();
        if (character < lowerBound || character > upperBound) {
            return false;
        }
        return true;
    }
    
    protected boolean is(char character, char lookedForCaracter) {
        if(character == lookedForCaracter) {
            return true;
        }
        return false;
    }
    protected boolean is(char lookedForCaracter) {
        char character = get();
        if(character == lookedForCaracter) {
            return true;
        }
        return false;
    }
    
    /**
     * This method does not check if the offset is out of bounds.
     * @return the next character
     */
    protected char peekNextChar() {
        return this.sourceText.charAt(this.pos+1);
    }
    
    protected boolean hasPeekChar() {
        return this.pos < this.sourceText.length()-1;
    }
    
    protected void consume() {
        this.pos++;
    }
    
    protected boolean match(char character) {
        if(this.sourceText.charAt(this.pos) == character) {
            this.pos++;
            return true;
        }
        return false;
    }
    
    protected char get() {
        return this.sourceText.charAt(this.pos);
    }
}
