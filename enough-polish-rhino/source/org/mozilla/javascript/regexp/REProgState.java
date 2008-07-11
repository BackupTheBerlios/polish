package org.mozilla.javascript.regexp;

class REProgState
{
    REProgState(REProgState previous, int min, int max, int index,
                REBackTrackData backTrack,
                int continuation_pc, int continuation_op)
    {
        this.previous = previous;
        this.min = min;
        this.max = max;
        this.index = index;
        this.continuation_op = continuation_op;
        this.continuation_pc = continuation_pc;
        this.backTrack = backTrack;
    }

    REProgState previous; // previous state in stack

    int min;                      /* current quantifier min */
    int max;                      /* current quantifier max */
    int index;                    /* progress in text */
    int continuation_op;
    int continuation_pc;
    REBackTrackData backTrack; // used by ASSERT_  to recover state
}

