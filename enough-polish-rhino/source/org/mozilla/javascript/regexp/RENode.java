package org.mozilla.javascript.regexp;

class RENode {

    RENode(byte op)
    {
        this.op = op;
    }

    byte            op;         /* r.e. op bytecode */
    RENode          next;       /* next in concatenation order */
    RENode          kid;        /* first operand */

    RENode          kid2;       /* second operand */
    int             num;        /* could be a number */
    int             parenIndex; /* or a parenthesis index */

                                /* or a range */
    int             min;
    int             max;
    int             parenCount;
    boolean         greedy;

                                /* or a character class */
    int             startIndex;
    int             kidlen;     /* length of string at kid, in chars */
    int             bmsize;     /* bitmap size, based on max char code */
    int             index;      /* index into class list */

                                /* or a literal sequence */
    char            chr;        /* of one character */
    int             len;     /* or many (via the index) */
    int             flatIndex;  /* which is -1 if not sourced */

}
