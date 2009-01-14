package org.mozilla.javascript.regexp;

import org.mozilla.javascript.Context;

class CompilerState {

    CompilerState(Context cx, char[] source, int length, int flags)
    {
        this.cx = cx;
        this.cpbegin = source;
        this.cp = 0;
        this.cpend = length;
        this.flags = flags;
        this.parenCount = 0;
        this.classCount = 0;
        this.progLength = 0;
    }

    Context     cx;
    char        cpbegin[];
    int         cpend;
    int         cp;
    int         flags;
    int         parenCount;
    int         parenNesting;
    int         classCount;   /* number of [] encountered */
    int         progLength;   /* estimated bytecode length */
    RENode      result;
}
