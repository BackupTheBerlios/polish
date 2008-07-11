package org.mozilla.javascript.regexp;

class REBackTrackData {

    REBackTrackData(REGlobalData gData, int op, int pc)
    {
        previous = gData.backTrackStackTop;
        continuation_op = op;
        continuation_pc = pc;
        lastParen = gData.lastParen;
        if (gData.parens != null) {
            //parens = (long[])gData.parens.clone();
            parens = new long[gData.parens.length];
            System.arraycopy(gData.parens, 0, parens, 0, gData.parens.length);
        }
        cp = gData.cp;
        stateStackTop = gData.stateStackTop;
    }

    REBackTrackData previous;

    int continuation_op;                /* where to backtrack to */
    int continuation_pc;
    int lastParen;
    long[] parens;                      /* parenthesis captures */
    int cp;                             /* char buffer index */
    REProgState stateStackTop;          /* state of op that backtracked */
}

