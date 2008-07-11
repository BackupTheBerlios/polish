package org.mozilla.javascript.regexp;

class REGlobalData {
    boolean multiline;
    RECompiled regexp;              /* the RE in execution */
    int lastParen;                  /* highest paren set so far */
    int skipped;                    /* chars skipped anchoring this r.e. */

    int cp;                         /* char buffer index */
    long[] parens;                  /* parens captures */

    REProgState stateStackTop;       /* stack of state of current ancestors */

    REBackTrackData backTrackStackTop;  /* last matched-so-far position */


    /**
     * Get start of parenthesis capture contents, -1 for empty.
     */
    int parens_index(int i)
    {
        return (int)(parens[i]);
    }

    /**
     * Get length of parenthesis capture contents.
     */
    int parens_length(int i)
    {
        return (int)(parens[i] >>> 32);
    }

    void set_parens(int i, int index, int length)
    {
        parens[i] = (index & 0xffffffffL) | ((long)length << 32);
    }

}

