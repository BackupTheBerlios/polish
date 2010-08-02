// $ANTLR 3.2 Sep 23, 2009 12:02:23 css21.g 2010-07-27 13:50:34

import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
public class css21Parser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "CHARSET_SYM", "STRING", "SEMI", "IMPORT_SYM", "URI", "COMMA", "MEDIA_SYM", "LBRACE", "RBRACE", "IDENT", "PAGE_SYM", "COLON", "SOLIDUS", "PLUS", "GREATER", "MINUS", "HASH", "DOT", "LBRACKET", "STAR", "OPEQ", "INCLUDES", "DASHMATCH", "RBRACKET", "LPAREN", "RPAREN", "IMPORTANT_SYM", "NUMBER", "PERCENTAGE", "LENGTH", "EMS", "EXS", "ANGLE", "TIME", "FREQ", "HEXCHAR", "NONASCII", "UNICODE", "ESCAPE", "NMSTART", "NMCHAR", "NAME", "URL", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "COMMENT", "CDO", "CDC", "INVALID", "WS", "DIMENSION", "NL"
    };
    public static final int STAR=23;
    public static final int LBRACE=11;
    public static final int EOF=-1;
    public static final int MEDIA_SYM=10;
    public static final int LPAREN=28;
    public static final int LENGTH=33;
    public static final int IMPORTANT_SYM=30;
    public static final int LBRACKET=22;
    public static final int INCLUDES=25;
    public static final int TIME=37;
    public static final int RPAREN=29;
    public static final int NAME=45;
    public static final int GREATER=18;
    public static final int ESCAPE=42;
    public static final int COMMA=9;
    public static final int IDENT=13;
    public static final int DIMENSION=78;
    public static final int PLUS=17;
    public static final int FREQ=38;
    public static final int NL=79;
    public static final int RBRACKET=27;
    public static final int COMMENT=73;
    public static final int DOT=21;
    public static final int D=50;
    public static final int CHARSET_SYM=4;
    public static final int E=51;
    public static final int F=52;
    public static final int G=53;
    public static final int A=47;
    public static final int B=48;
    public static final int RBRACE=12;
    public static final int ANGLE=36;
    public static final int C=49;
    public static final int L=58;
    public static final int M=59;
    public static final int NMCHAR=44;
    public static final int IMPORT_SYM=7;
    public static final int N=60;
    public static final int O=61;
    public static final int H=54;
    public static final int I=55;
    public static final int J=56;
    public static final int K=57;
    public static final int NUMBER=31;
    public static final int U=67;
    public static final int HASH=20;
    public static final int HEXCHAR=39;
    public static final int T=66;
    public static final int W=69;
    public static final int V=68;
    public static final int Q=63;
    public static final int P=62;
    public static final int S=65;
    public static final int CDO=74;
    public static final int R=64;
    public static final int MINUS=19;
    public static final int SOLIDUS=16;
    public static final int SEMI=6;
    public static final int INVALID=76;
    public static final int CDC=75;
    public static final int Y=71;
    public static final int URL=46;
    public static final int PERCENTAGE=32;
    public static final int UNICODE=41;
    public static final int X=70;
    public static final int Z=72;
    public static final int URI=8;
    public static final int COLON=15;
    public static final int PAGE_SYM=14;
    public static final int WS=77;
    public static final int NMSTART=43;
    public static final int DASHMATCH=26;
    public static final int OPEQ=24;
    public static final int EMS=34;
    public static final int EXS=35;
    public static final int NONASCII=40;
    public static final int STRING=5;

    // delegates
    // delegators


        public css21Parser(TokenStream input) {
            this(input, new RecognizerSharedState());
        }
        public css21Parser(TokenStream input, RecognizerSharedState state) {
            super(input, state);
             
        }
        

    public String[] getTokenNames() { return css21Parser.tokenNames; }
    public String getGrammarFileName() { return "css21.g"; }



    // $ANTLR start "styleSheet"
    // css21.g:27:1: styleSheet : charSet ( imports )* bodylist EOF ;
    public final void styleSheet() throws RecognitionException {
        try {
            // css21.g:28:5: ( charSet ( imports )* bodylist EOF )
            // css21.g:28:9: charSet ( imports )* bodylist EOF
            {
            pushFollow(FOLLOW_charSet_in_styleSheet42);
            charSet();

            state._fsp--;
            if (state.failed) return ;
            // css21.g:29:9: ( imports )*
            loop1:
            do {
                int alt1=2;
                int LA1_0 = input.LA(1);

                if ( (LA1_0==IMPORT_SYM) ) {
                    alt1=1;
                }


                switch (alt1) {
            	case 1 :
            	    // css21.g:29:9: imports
            	    {
            	    pushFollow(FOLLOW_imports_in_styleSheet52);
            	    imports();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);

            pushFollow(FOLLOW_bodylist_in_styleSheet63);
            bodylist();

            state._fsp--;
            if (state.failed) return ;
            match(input,EOF,FOLLOW_EOF_in_styleSheet70); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "styleSheet"


    // $ANTLR start "charSet"
    // css21.g:37:1: charSet : ( CHARSET_SYM STRING SEMI | );
    public final void charSet() throws RecognitionException {
        try {
            // css21.g:38:5: ( CHARSET_SYM STRING SEMI | )
            int alt2=2;
            int LA2_0 = input.LA(1);

            if ( (LA2_0==CHARSET_SYM) ) {
                alt2=1;
            }
            else if ( (LA2_0==EOF||LA2_0==IMPORT_SYM||LA2_0==MEDIA_SYM||(LA2_0>=IDENT && LA2_0<=COLON)||(LA2_0>=HASH && LA2_0<=STAR)) ) {
                alt2=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;
            }
            switch (alt2) {
                case 1 :
                    // css21.g:38:9: CHARSET_SYM STRING SEMI
                    {
                    match(input,CHARSET_SYM,FOLLOW_CHARSET_SYM_in_charSet96); if (state.failed) return ;
                    match(input,STRING,FOLLOW_STRING_in_charSet98); if (state.failed) return ;
                    match(input,SEMI,FOLLOW_SEMI_in_charSet100); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // css21.g:40:5: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "charSet"


    // $ANTLR start "imports"
    // css21.g:45:1: imports : IMPORT_SYM ( STRING | URI ) ( medium ( COMMA medium )* )? SEMI ;
    public final void imports() throws RecognitionException {
        try {
            // css21.g:46:5: ( IMPORT_SYM ( STRING | URI ) ( medium ( COMMA medium )* )? SEMI )
            // css21.g:46:9: IMPORT_SYM ( STRING | URI ) ( medium ( COMMA medium )* )? SEMI
            {
            match(input,IMPORT_SYM,FOLLOW_IMPORT_SYM_in_imports128); if (state.failed) return ;
            if ( input.LA(1)==STRING||input.LA(1)==URI ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }

            // css21.g:46:33: ( medium ( COMMA medium )* )?
            int alt4=2;
            int LA4_0 = input.LA(1);

            if ( (LA4_0==IDENT) ) {
                alt4=1;
            }
            switch (alt4) {
                case 1 :
                    // css21.g:46:34: medium ( COMMA medium )*
                    {
                    pushFollow(FOLLOW_medium_in_imports137);
                    medium();

                    state._fsp--;
                    if (state.failed) return ;
                    // css21.g:46:41: ( COMMA medium )*
                    loop3:
                    do {
                        int alt3=2;
                        int LA3_0 = input.LA(1);

                        if ( (LA3_0==COMMA) ) {
                            alt3=1;
                        }


                        switch (alt3) {
                    	case 1 :
                    	    // css21.g:46:42: COMMA medium
                    	    {
                    	    match(input,COMMA,FOLLOW_COMMA_in_imports140); if (state.failed) return ;
                    	    pushFollow(FOLLOW_medium_in_imports142);
                    	    medium();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop3;
                        }
                    } while (true);


                    }
                    break;

            }

            match(input,SEMI,FOLLOW_SEMI_in_imports148); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "imports"


    // $ANTLR start "media"
    // css21.g:53:1: media : MEDIA_SYM medium ( COMMA medium )* LBRACE ruleSet RBRACE ;
    public final void media() throws RecognitionException {
        try {
            // css21.g:54:5: ( MEDIA_SYM medium ( COMMA medium )* LBRACE ruleSet RBRACE )
            // css21.g:54:7: MEDIA_SYM medium ( COMMA medium )* LBRACE ruleSet RBRACE
            {
            match(input,MEDIA_SYM,FOLLOW_MEDIA_SYM_in_media169); if (state.failed) return ;
            pushFollow(FOLLOW_medium_in_media171);
            medium();

            state._fsp--;
            if (state.failed) return ;
            // css21.g:54:24: ( COMMA medium )*
            loop5:
            do {
                int alt5=2;
                int LA5_0 = input.LA(1);

                if ( (LA5_0==COMMA) ) {
                    alt5=1;
                }


                switch (alt5) {
            	case 1 :
            	    // css21.g:54:25: COMMA medium
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_media174); if (state.failed) return ;
            	    pushFollow(FOLLOW_medium_in_media176);
            	    medium();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);

            match(input,LBRACE,FOLLOW_LBRACE_in_media188); if (state.failed) return ;
            pushFollow(FOLLOW_ruleSet_in_media202);
            ruleSet();

            state._fsp--;
            if (state.failed) return ;
            match(input,RBRACE,FOLLOW_RBRACE_in_media212); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "media"


    // $ANTLR start "medium"
    // css21.g:63:1: medium : IDENT ;
    public final void medium() throws RecognitionException {
        try {
            // css21.g:64:5: ( IDENT )
            // css21.g:64:7: IDENT
            {
            match(input,IDENT,FOLLOW_IDENT_in_medium232); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "medium"


    // $ANTLR start "bodylist"
    // css21.g:68:1: bodylist : ( bodyset )* ;
    public final void bodylist() throws RecognitionException {
        try {
            // css21.g:69:5: ( ( bodyset )* )
            // css21.g:69:7: ( bodyset )*
            {
            // css21.g:69:7: ( bodyset )*
            loop6:
            do {
                int alt6=2;
                int LA6_0 = input.LA(1);

                if ( (LA6_0==MEDIA_SYM||(LA6_0>=IDENT && LA6_0<=COLON)||(LA6_0>=HASH && LA6_0<=STAR)) ) {
                    alt6=1;
                }


                switch (alt6) {
            	case 1 :
            	    // css21.g:69:7: bodyset
            	    {
            	    pushFollow(FOLLOW_bodyset_in_bodylist255);
            	    bodyset();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop6;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "bodylist"


    // $ANTLR start "bodyset"
    // css21.g:72:1: bodyset : ( ruleSet | media | page );
    public final void bodyset() throws RecognitionException {
        try {
            // css21.g:73:5: ( ruleSet | media | page )
            int alt7=3;
            switch ( input.LA(1) ) {
            case IDENT:
            case COLON:
            case HASH:
            case DOT:
            case LBRACKET:
            case STAR:
                {
                alt7=1;
                }
                break;
            case MEDIA_SYM:
                {
                alt7=2;
                }
                break;
            case PAGE_SYM:
                {
                alt7=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;
            }

            switch (alt7) {
                case 1 :
                    // css21.g:73:7: ruleSet
                    {
                    pushFollow(FOLLOW_ruleSet_in_bodyset277);
                    ruleSet();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // css21.g:74:7: media
                    {
                    pushFollow(FOLLOW_media_in_bodyset285);
                    media();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // css21.g:75:7: page
                    {
                    pushFollow(FOLLOW_page_in_bodyset293);
                    page();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "bodyset"


    // $ANTLR start "page"
    // css21.g:78:1: page : PAGE_SYM ( pseudoPage )? LBRACE declaration SEMI ( declaration SEMI )* RBRACE ;
    public final void page() throws RecognitionException {
        try {
            // css21.g:79:5: ( PAGE_SYM ( pseudoPage )? LBRACE declaration SEMI ( declaration SEMI )* RBRACE )
            // css21.g:79:7: PAGE_SYM ( pseudoPage )? LBRACE declaration SEMI ( declaration SEMI )* RBRACE
            {
            match(input,PAGE_SYM,FOLLOW_PAGE_SYM_in_page317); if (state.failed) return ;
            // css21.g:79:16: ( pseudoPage )?
            int alt8=2;
            int LA8_0 = input.LA(1);

            if ( (LA8_0==COLON) ) {
                alt8=1;
            }
            switch (alt8) {
                case 1 :
                    // css21.g:79:16: pseudoPage
                    {
                    pushFollow(FOLLOW_pseudoPage_in_page319);
                    pseudoPage();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }

            match(input,LBRACE,FOLLOW_LBRACE_in_page330); if (state.failed) return ;
            pushFollow(FOLLOW_declaration_in_page344);
            declaration();

            state._fsp--;
            if (state.failed) return ;
            match(input,SEMI,FOLLOW_SEMI_in_page346); if (state.failed) return ;
            // css21.g:81:30: ( declaration SEMI )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( (LA9_0==IDENT) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // css21.g:81:31: declaration SEMI
            	    {
            	    pushFollow(FOLLOW_declaration_in_page349);
            	    declaration();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    match(input,SEMI,FOLLOW_SEMI_in_page351); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);

            match(input,RBRACE,FOLLOW_RBRACE_in_page363); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "page"


    // $ANTLR start "pseudoPage"
    // css21.g:85:1: pseudoPage : COLON IDENT ;
    public final void pseudoPage() throws RecognitionException {
        try {
            // css21.g:86:5: ( COLON IDENT )
            // css21.g:86:7: COLON IDENT
            {
            match(input,COLON,FOLLOW_COLON_in_pseudoPage384); if (state.failed) return ;
            match(input,IDENT,FOLLOW_IDENT_in_pseudoPage386); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "pseudoPage"


    // $ANTLR start "operator"
    // css21.g:89:1: operator : ( SOLIDUS | COMMA | );
    public final void operator() throws RecognitionException {
        try {
            // css21.g:90:5: ( SOLIDUS | COMMA | )
            int alt10=3;
            switch ( input.LA(1) ) {
            case SOLIDUS:
                {
                alt10=1;
                }
                break;
            case COMMA:
                {
                alt10=2;
                }
                break;
            case STRING:
            case URI:
            case IDENT:
            case PLUS:
            case MINUS:
            case HASH:
            case NUMBER:
            case PERCENTAGE:
            case LENGTH:
            case EMS:
            case EXS:
            case ANGLE:
            case TIME:
            case FREQ:
                {
                alt10=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 0, input);

                throw nvae;
            }

            switch (alt10) {
                case 1 :
                    // css21.g:90:7: SOLIDUS
                    {
                    match(input,SOLIDUS,FOLLOW_SOLIDUS_in_operator407); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // css21.g:91:7: COMMA
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_operator415); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // css21.g:93:5: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "operator"


    // $ANTLR start "combinator"
    // css21.g:95:1: combinator : ( PLUS | GREATER | );
    public final void combinator() throws RecognitionException {
        try {
            // css21.g:96:5: ( PLUS | GREATER | )
            int alt11=3;
            switch ( input.LA(1) ) {
            case PLUS:
                {
                alt11=1;
                }
                break;
            case GREATER:
                {
                alt11=2;
                }
                break;
            case IDENT:
            case COLON:
            case HASH:
            case DOT:
            case LBRACKET:
            case STAR:
                {
                alt11=3;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 11, 0, input);

                throw nvae;
            }

            switch (alt11) {
                case 1 :
                    // css21.g:96:7: PLUS
                    {
                    match(input,PLUS,FOLLOW_PLUS_in_combinator442); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // css21.g:97:7: GREATER
                    {
                    match(input,GREATER,FOLLOW_GREATER_in_combinator450); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // css21.g:99:5: 
                    {
                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "combinator"


    // $ANTLR start "unaryOperator"
    // css21.g:101:1: unaryOperator : ( MINUS | PLUS );
    public final void unaryOperator() throws RecognitionException {
        try {
            // css21.g:102:5: ( MINUS | PLUS )
            // css21.g:
            {
            if ( input.LA(1)==PLUS||input.LA(1)==MINUS ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "unaryOperator"


    // $ANTLR start "property"
    // css21.g:106:1: property : IDENT ;
    public final void property() throws RecognitionException {
        try {
            // css21.g:107:5: ( IDENT )
            // css21.g:107:7: IDENT
            {
            match(input,IDENT,FOLLOW_IDENT_in_property508); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "property"


    // $ANTLR start "ruleSet"
    // css21.g:110:1: ruleSet : selector ( COMMA selector )* LBRACE declaration SEMI ( declaration SEMI )* RBRACE ;
    public final void ruleSet() throws RecognitionException {
        try {
            // css21.g:111:5: ( selector ( COMMA selector )* LBRACE declaration SEMI ( declaration SEMI )* RBRACE )
            // css21.g:111:7: selector ( COMMA selector )* LBRACE declaration SEMI ( declaration SEMI )* RBRACE
            {
            pushFollow(FOLLOW_selector_in_ruleSet529);
            selector();

            state._fsp--;
            if (state.failed) return ;
            // css21.g:111:16: ( COMMA selector )*
            loop12:
            do {
                int alt12=2;
                int LA12_0 = input.LA(1);

                if ( (LA12_0==COMMA) ) {
                    alt12=1;
                }


                switch (alt12) {
            	case 1 :
            	    // css21.g:111:17: COMMA selector
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_ruleSet532); if (state.failed) return ;
            	    pushFollow(FOLLOW_selector_in_ruleSet534);
            	    selector();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);

            match(input,LBRACE,FOLLOW_LBRACE_in_ruleSet546); if (state.failed) return ;
            pushFollow(FOLLOW_declaration_in_ruleSet560);
            declaration();

            state._fsp--;
            if (state.failed) return ;
            match(input,SEMI,FOLLOW_SEMI_in_ruleSet562); if (state.failed) return ;
            // css21.g:113:30: ( declaration SEMI )*
            loop13:
            do {
                int alt13=2;
                int LA13_0 = input.LA(1);

                if ( (LA13_0==IDENT) ) {
                    alt13=1;
                }


                switch (alt13) {
            	case 1 :
            	    // css21.g:113:31: declaration SEMI
            	    {
            	    pushFollow(FOLLOW_declaration_in_ruleSet565);
            	    declaration();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    match(input,SEMI,FOLLOW_SEMI_in_ruleSet567); if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop13;
                }
            } while (true);

            match(input,RBRACE,FOLLOW_RBRACE_in_ruleSet579); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "ruleSet"


    // $ANTLR start "selector"
    // css21.g:117:1: selector : simpleSelector ( combinator simpleSelector )* ;
    public final void selector() throws RecognitionException {
        try {
            // css21.g:118:5: ( simpleSelector ( combinator simpleSelector )* )
            // css21.g:118:7: simpleSelector ( combinator simpleSelector )*
            {
            pushFollow(FOLLOW_simpleSelector_in_selector600);
            simpleSelector();

            state._fsp--;
            if (state.failed) return ;
            // css21.g:118:22: ( combinator simpleSelector )*
            loop14:
            do {
                int alt14=2;
                int LA14_0 = input.LA(1);

                if ( (LA14_0==IDENT||LA14_0==COLON||(LA14_0>=PLUS && LA14_0<=GREATER)||(LA14_0>=HASH && LA14_0<=STAR)) ) {
                    alt14=1;
                }


                switch (alt14) {
            	case 1 :
            	    // css21.g:118:23: combinator simpleSelector
            	    {
            	    pushFollow(FOLLOW_combinator_in_selector603);
            	    combinator();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_simpleSelector_in_selector605);
            	    simpleSelector();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop14;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "selector"


    // $ANTLR start "simpleSelector"
    // css21.g:121:1: simpleSelector : ( elementName ( ( esPred )=> elementSubsequent )* | ( ( esPred )=> elementSubsequent )+ );
    public final void simpleSelector() throws RecognitionException {
        try {
            // css21.g:122:5: ( elementName ( ( esPred )=> elementSubsequent )* | ( ( esPred )=> elementSubsequent )+ )
            int alt17=2;
            int LA17_0 = input.LA(1);

            if ( (LA17_0==IDENT||LA17_0==STAR) ) {
                alt17=1;
            }
            else if ( (LA17_0==COLON||(LA17_0>=HASH && LA17_0<=LBRACKET)) ) {
                alt17=2;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 17, 0, input);

                throw nvae;
            }
            switch (alt17) {
                case 1 :
                    // css21.g:122:7: elementName ( ( esPred )=> elementSubsequent )*
                    {
                    pushFollow(FOLLOW_elementName_in_simpleSelector624);
                    elementName();

                    state._fsp--;
                    if (state.failed) return ;
                    // css21.g:123:9: ( ( esPred )=> elementSubsequent )*
                    loop15:
                    do {
                        int alt15=2;
                        alt15 = dfa15.predict(input);
                        switch (alt15) {
                    	case 1 :
                    	    // css21.g:123:10: ( esPred )=> elementSubsequent
                    	    {
                    	    pushFollow(FOLLOW_elementSubsequent_in_simpleSelector640);
                    	    elementSubsequent();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    break loop15;
                        }
                    } while (true);


                    }
                    break;
                case 2 :
                    // css21.g:125:7: ( ( esPred )=> elementSubsequent )+
                    {
                    // css21.g:125:7: ( ( esPred )=> elementSubsequent )+
                    int cnt16=0;
                    loop16:
                    do {
                        int alt16=2;
                        switch ( input.LA(1) ) {
                        case HASH:
                            {
                            int LA16_2 = input.LA(2);

                            if ( (synpred2_css21()) ) {
                                alt16=1;
                            }


                            }
                            break;
                        case DOT:
                            {
                            int LA16_3 = input.LA(2);

                            if ( (synpred2_css21()) ) {
                                alt16=1;
                            }


                            }
                            break;
                        case LBRACKET:
                            {
                            int LA16_4 = input.LA(2);

                            if ( (synpred2_css21()) ) {
                                alt16=1;
                            }


                            }
                            break;
                        case COLON:
                            {
                            int LA16_5 = input.LA(2);

                            if ( (synpred2_css21()) ) {
                                alt16=1;
                            }


                            }
                            break;

                        }

                        switch (alt16) {
                    	case 1 :
                    	    // css21.g:125:8: ( esPred )=> elementSubsequent
                    	    {
                    	    pushFollow(FOLLOW_elementSubsequent_in_simpleSelector664);
                    	    elementSubsequent();

                    	    state._fsp--;
                    	    if (state.failed) return ;

                    	    }
                    	    break;

                    	default :
                    	    if ( cnt16 >= 1 ) break loop16;
                    	    if (state.backtracking>0) {state.failed=true; return ;}
                                EarlyExitException eee =
                                    new EarlyExitException(16, input);
                                throw eee;
                        }
                        cnt16++;
                    } while (true);


                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "simpleSelector"


    // $ANTLR start "esPred"
    // css21.g:128:1: esPred : ( HASH | DOT | LBRACKET | COLON );
    public final void esPred() throws RecognitionException {
        try {
            // css21.g:129:5: ( HASH | DOT | LBRACKET | COLON )
            // css21.g:
            {
            if ( input.LA(1)==COLON||(input.LA(1)>=HASH && input.LA(1)<=LBRACKET) ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "esPred"


    // $ANTLR start "elementSubsequent"
    // css21.g:132:1: elementSubsequent : ( HASH | cssClass | attrib | pseudo );
    public final void elementSubsequent() throws RecognitionException {
        try {
            // css21.g:133:5: ( HASH | cssClass | attrib | pseudo )
            int alt18=4;
            switch ( input.LA(1) ) {
            case HASH:
                {
                alt18=1;
                }
                break;
            case DOT:
                {
                alt18=2;
                }
                break;
            case LBRACKET:
                {
                alt18=3;
                }
                break;
            case COLON:
                {
                alt18=4;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 18, 0, input);

                throw nvae;
            }

            switch (alt18) {
                case 1 :
                    // css21.g:133:7: HASH
                    {
                    match(input,HASH,FOLLOW_HASH_in_elementSubsequent720); if (state.failed) return ;

                    }
                    break;
                case 2 :
                    // css21.g:134:7: cssClass
                    {
                    pushFollow(FOLLOW_cssClass_in_elementSubsequent728);
                    cssClass();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // css21.g:135:7: attrib
                    {
                    pushFollow(FOLLOW_attrib_in_elementSubsequent736);
                    attrib();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;
                case 4 :
                    // css21.g:136:7: pseudo
                    {
                    pushFollow(FOLLOW_pseudo_in_elementSubsequent744);
                    pseudo();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementSubsequent"


    // $ANTLR start "cssClass"
    // css21.g:139:1: cssClass : DOT IDENT ;
    public final void cssClass() throws RecognitionException {
        try {
            // css21.g:140:5: ( DOT IDENT )
            // css21.g:140:7: DOT IDENT
            {
            match(input,DOT,FOLLOW_DOT_in_cssClass765); if (state.failed) return ;
            match(input,IDENT,FOLLOW_IDENT_in_cssClass767); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "cssClass"


    // $ANTLR start "elementName"
    // css21.g:143:1: elementName : ( IDENT | STAR );
    public final void elementName() throws RecognitionException {
        try {
            // css21.g:144:5: ( IDENT | STAR )
            // css21.g:
            {
            if ( input.LA(1)==IDENT||input.LA(1)==STAR ) {
                input.consume();
                state.errorRecovery=false;state.failed=false;
            }
            else {
                if (state.backtracking>0) {state.failed=true; return ;}
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "elementName"


    // $ANTLR start "attrib"
    // css21.g:148:1: attrib : LBRACKET IDENT ( ( OPEQ | INCLUDES | DASHMATCH ) ( IDENT | STRING ) )? RBRACKET ;
    public final void attrib() throws RecognitionException {
        try {
            // css21.g:149:5: ( LBRACKET IDENT ( ( OPEQ | INCLUDES | DASHMATCH ) ( IDENT | STRING ) )? RBRACKET )
            // css21.g:149:7: LBRACKET IDENT ( ( OPEQ | INCLUDES | DASHMATCH ) ( IDENT | STRING ) )? RBRACKET
            {
            match(input,LBRACKET,FOLLOW_LBRACKET_in_attrib817); if (state.failed) return ;
            match(input,IDENT,FOLLOW_IDENT_in_attrib832); if (state.failed) return ;
            // css21.g:153:13: ( ( OPEQ | INCLUDES | DASHMATCH ) ( IDENT | STRING ) )?
            int alt19=2;
            int LA19_0 = input.LA(1);

            if ( ((LA19_0>=OPEQ && LA19_0<=DASHMATCH)) ) {
                alt19=1;
            }
            switch (alt19) {
                case 1 :
                    // css21.g:154:17: ( OPEQ | INCLUDES | DASHMATCH ) ( IDENT | STRING )
                    {
                    if ( (input.LA(1)>=OPEQ && input.LA(1)<=DASHMATCH) ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }

                    if ( input.LA(1)==STRING||input.LA(1)==IDENT ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;

            }

            match(input,RBRACKET,FOLLOW_RBRACKET_in_attrib1082); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "attrib"


    // $ANTLR start "pseudo"
    // css21.g:168:1: pseudo : COLON IDENT ( LPAREN ( IDENT )? RPAREN )? ;
    public final void pseudo() throws RecognitionException {
        try {
            // css21.g:169:5: ( COLON IDENT ( LPAREN ( IDENT )? RPAREN )? )
            // css21.g:169:7: COLON IDENT ( LPAREN ( IDENT )? RPAREN )?
            {
            match(input,COLON,FOLLOW_COLON_in_pseudo1095); if (state.failed) return ;
            match(input,IDENT,FOLLOW_IDENT_in_pseudo1110); if (state.failed) return ;
            // css21.g:171:17: ( LPAREN ( IDENT )? RPAREN )?
            int alt21=2;
            int LA21_0 = input.LA(1);

            if ( (LA21_0==LPAREN) ) {
                alt21=1;
            }
            switch (alt21) {
                case 1 :
                    // css21.g:173:21: LPAREN ( IDENT )? RPAREN
                    {
                    match(input,LPAREN,FOLLOW_LPAREN_in_pseudo1168); if (state.failed) return ;
                    // css21.g:173:28: ( IDENT )?
                    int alt20=2;
                    int LA20_0 = input.LA(1);

                    if ( (LA20_0==IDENT) ) {
                        alt20=1;
                    }
                    switch (alt20) {
                        case 1 :
                            // css21.g:173:28: IDENT
                            {
                            match(input,IDENT,FOLLOW_IDENT_in_pseudo1170); if (state.failed) return ;

                            }
                            break;

                    }

                    match(input,RPAREN,FOLLOW_RPAREN_in_pseudo1173); if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "pseudo"


    // $ANTLR start "declaration"
    // css21.g:177:1: declaration : property COLON expr ( prio )? ;
    public final void declaration() throws RecognitionException {
        try {
            // css21.g:178:5: ( property COLON expr ( prio )? )
            // css21.g:178:7: property COLON expr ( prio )?
            {
            pushFollow(FOLLOW_property_in_declaration1209);
            property();

            state._fsp--;
            if (state.failed) return ;
            match(input,COLON,FOLLOW_COLON_in_declaration1211); if (state.failed) return ;
            pushFollow(FOLLOW_expr_in_declaration1213);
            expr();

            state._fsp--;
            if (state.failed) return ;
            // css21.g:178:27: ( prio )?
            int alt22=2;
            int LA22_0 = input.LA(1);

            if ( (LA22_0==IMPORTANT_SYM) ) {
                alt22=1;
            }
            switch (alt22) {
                case 1 :
                    // css21.g:178:27: prio
                    {
                    pushFollow(FOLLOW_prio_in_declaration1215);
                    prio();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "declaration"


    // $ANTLR start "prio"
    // css21.g:181:1: prio : IMPORTANT_SYM ;
    public final void prio() throws RecognitionException {
        try {
            // css21.g:182:5: ( IMPORTANT_SYM )
            // css21.g:182:7: IMPORTANT_SYM
            {
            match(input,IMPORTANT_SYM,FOLLOW_IMPORTANT_SYM_in_prio1237); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "prio"


    // $ANTLR start "expr"
    // css21.g:185:1: expr : term ( operator term )* ;
    public final void expr() throws RecognitionException {
        try {
            // css21.g:186:5: ( term ( operator term )* )
            // css21.g:186:7: term ( operator term )*
            {
            pushFollow(FOLLOW_term_in_expr1258);
            term();

            state._fsp--;
            if (state.failed) return ;
            // css21.g:186:12: ( operator term )*
            loop23:
            do {
                int alt23=2;
                int LA23_0 = input.LA(1);

                if ( (LA23_0==STRING||(LA23_0>=URI && LA23_0<=COMMA)||LA23_0==IDENT||(LA23_0>=SOLIDUS && LA23_0<=PLUS)||(LA23_0>=MINUS && LA23_0<=HASH)||(LA23_0>=NUMBER && LA23_0<=FREQ)) ) {
                    alt23=1;
                }


                switch (alt23) {
            	case 1 :
            	    // css21.g:186:13: operator term
            	    {
            	    pushFollow(FOLLOW_operator_in_expr1261);
            	    operator();

            	    state._fsp--;
            	    if (state.failed) return ;
            	    pushFollow(FOLLOW_term_in_expr1263);
            	    term();

            	    state._fsp--;
            	    if (state.failed) return ;

            	    }
            	    break;

            	default :
            	    break loop23;
                }
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "expr"


    // $ANTLR start "term"
    // css21.g:189:1: term : ( ( unaryOperator )? ( NUMBER | PERCENTAGE | LENGTH | EMS | EXS | ANGLE | TIME | FREQ ) | STRING | IDENT ( LPAREN expr RPAREN )? | URI | hexColor );
    public final void term() throws RecognitionException {
        try {
            // css21.g:190:5: ( ( unaryOperator )? ( NUMBER | PERCENTAGE | LENGTH | EMS | EXS | ANGLE | TIME | FREQ ) | STRING | IDENT ( LPAREN expr RPAREN )? | URI | hexColor )
            int alt26=5;
            switch ( input.LA(1) ) {
            case PLUS:
            case MINUS:
            case NUMBER:
            case PERCENTAGE:
            case LENGTH:
            case EMS:
            case EXS:
            case ANGLE:
            case TIME:
            case FREQ:
                {
                alt26=1;
                }
                break;
            case STRING:
                {
                alt26=2;
                }
                break;
            case IDENT:
                {
                alt26=3;
                }
                break;
            case URI:
                {
                alt26=4;
                }
                break;
            case HASH:
                {
                alt26=5;
                }
                break;
            default:
                if (state.backtracking>0) {state.failed=true; return ;}
                NoViableAltException nvae =
                    new NoViableAltException("", 26, 0, input);

                throw nvae;
            }

            switch (alt26) {
                case 1 :
                    // css21.g:190:7: ( unaryOperator )? ( NUMBER | PERCENTAGE | LENGTH | EMS | EXS | ANGLE | TIME | FREQ )
                    {
                    // css21.g:190:7: ( unaryOperator )?
                    int alt24=2;
                    int LA24_0 = input.LA(1);

                    if ( (LA24_0==PLUS||LA24_0==MINUS) ) {
                        alt24=1;
                    }
                    switch (alt24) {
                        case 1 :
                            // css21.g:190:7: unaryOperator
                            {
                            pushFollow(FOLLOW_unaryOperator_in_term1286);
                            unaryOperator();

                            state._fsp--;
                            if (state.failed) return ;

                            }
                            break;

                    }

                    if ( (input.LA(1)>=NUMBER && input.LA(1)<=FREQ) ) {
                        input.consume();
                        state.errorRecovery=false;state.failed=false;
                    }
                    else {
                        if (state.backtracking>0) {state.failed=true; return ;}
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        throw mse;
                    }


                    }
                    break;
                case 2 :
                    // css21.g:201:7: STRING
                    {
                    match(input,STRING,FOLLOW_STRING_in_term1443); if (state.failed) return ;

                    }
                    break;
                case 3 :
                    // css21.g:202:7: IDENT ( LPAREN expr RPAREN )?
                    {
                    match(input,IDENT,FOLLOW_IDENT_in_term1451); if (state.failed) return ;
                    // css21.g:202:13: ( LPAREN expr RPAREN )?
                    int alt25=2;
                    int LA25_0 = input.LA(1);

                    if ( (LA25_0==LPAREN) ) {
                        alt25=1;
                    }
                    switch (alt25) {
                        case 1 :
                            // css21.g:203:17: LPAREN expr RPAREN
                            {
                            match(input,LPAREN,FOLLOW_LPAREN_in_term1474); if (state.failed) return ;
                            pushFollow(FOLLOW_expr_in_term1476);
                            expr();

                            state._fsp--;
                            if (state.failed) return ;
                            match(input,RPAREN,FOLLOW_RPAREN_in_term1478); if (state.failed) return ;

                            }
                            break;

                    }


                    }
                    break;
                case 4 :
                    // css21.g:205:7: URI
                    {
                    match(input,URI,FOLLOW_URI_in_term1501); if (state.failed) return ;

                    }
                    break;
                case 5 :
                    // css21.g:206:7: hexColor
                    {
                    pushFollow(FOLLOW_hexColor_in_term1509);
                    hexColor();

                    state._fsp--;
                    if (state.failed) return ;

                    }
                    break;

            }
        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "term"


    // $ANTLR start "hexColor"
    // css21.g:209:1: hexColor : HASH ;
    public final void hexColor() throws RecognitionException {
        try {
            // css21.g:210:5: ( HASH )
            // css21.g:210:7: HASH
            {
            match(input,HASH,FOLLOW_HASH_in_hexColor1530); if (state.failed) return ;

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }
        finally {
        }
        return ;
    }
    // $ANTLR end "hexColor"

    // $ANTLR start synpred1_css21
    public final void synpred1_css21_fragment() throws RecognitionException {   
        // css21.g:123:10: ( esPred )
        // css21.g:123:11: esPred
        {
        pushFollow(FOLLOW_esPred_in_synpred1_css21637);
        esPred();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred1_css21

    // $ANTLR start synpred2_css21
    public final void synpred2_css21_fragment() throws RecognitionException {   
        // css21.g:125:8: ( esPred )
        // css21.g:125:9: esPred
        {
        pushFollow(FOLLOW_esPred_in_synpred2_css21661);
        esPred();

        state._fsp--;
        if (state.failed) return ;

        }
    }
    // $ANTLR end synpred2_css21

    // Delegated rules

    public final boolean synpred2_css21() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred2_css21_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }
    public final boolean synpred1_css21() {
        state.backtracking++;
        int start = input.mark();
        try {
            synpred1_css21_fragment(); // can never throw exception
        } catch (RecognitionException re) {
            System.err.println("impossible: "+re);
        }
        boolean success = !state.failed;
        input.rewind(start);
        state.backtracking--;
        state.failed=false;
        return success;
    }


    protected DFA15 dfa15 = new DFA15(this);
    static final String DFA15_eotS =
        "\15\uffff";
    static final String DFA15_eofS =
        "\15\uffff";
    static final String DFA15_minS =
        "\1\11\1\uffff\1\0\3\15\1\uffff\1\0\1\30\1\0\1\5\1\0\1\33";
    static final String DFA15_maxS =
        "\1\27\1\uffff\1\0\3\15\1\uffff\1\0\1\33\1\0\1\15\1\0\1\33";
    static final String DFA15_acceptS =
        "\1\uffff\1\2\4\uffff\1\1\6\uffff";
    static final String DFA15_specialS =
        "\2\uffff\1\0\4\uffff\1\3\1\uffff\1\2\1\uffff\1\1\1\uffff}>";
    static final String[] DFA15_transitionS = {
            "\1\1\1\uffff\1\1\1\uffff\1\1\1\uffff\1\5\1\uffff\2\1\1\uffff"+
            "\1\2\1\3\1\4\1\1",
            "",
            "\1\uffff",
            "\1\7",
            "\1\10",
            "\1\11",
            "",
            "\1\uffff",
            "\3\12\1\13",
            "\1\uffff",
            "\1\14\7\uffff\1\14",
            "\1\uffff",
            "\1\13"
    };

    static final short[] DFA15_eot = DFA.unpackEncodedString(DFA15_eotS);
    static final short[] DFA15_eof = DFA.unpackEncodedString(DFA15_eofS);
    static final char[] DFA15_min = DFA.unpackEncodedStringToUnsignedChars(DFA15_minS);
    static final char[] DFA15_max = DFA.unpackEncodedStringToUnsignedChars(DFA15_maxS);
    static final short[] DFA15_accept = DFA.unpackEncodedString(DFA15_acceptS);
    static final short[] DFA15_special = DFA.unpackEncodedString(DFA15_specialS);
    static final short[][] DFA15_transition;

    static {
        int numStates = DFA15_transitionS.length;
        DFA15_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA15_transition[i] = DFA.unpackEncodedString(DFA15_transitionS[i]);
        }
    }

    class DFA15 extends DFA {

        public DFA15(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 15;
            this.eot = DFA15_eot;
            this.eof = DFA15_eof;
            this.min = DFA15_min;
            this.max = DFA15_max;
            this.accept = DFA15_accept;
            this.special = DFA15_special;
            this.transition = DFA15_transition;
        }
        public String getDescription() {
            return "()* loopback of 123:9: ( ( esPred )=> elementSubsequent )*";
        }
        public int specialStateTransition(int s, IntStream _input) throws NoViableAltException {
            TokenStream input = (TokenStream)_input;
        	int _s = s;
            switch ( s ) {
                    case 0 : 
                        int LA15_2 = input.LA(1);

                         
                        int index15_2 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_css21()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index15_2);
                        if ( s>=0 ) return s;
                        break;
                    case 1 : 
                        int LA15_11 = input.LA(1);

                         
                        int index15_11 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_css21()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index15_11);
                        if ( s>=0 ) return s;
                        break;
                    case 2 : 
                        int LA15_9 = input.LA(1);

                         
                        int index15_9 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_css21()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index15_9);
                        if ( s>=0 ) return s;
                        break;
                    case 3 : 
                        int LA15_7 = input.LA(1);

                         
                        int index15_7 = input.index();
                        input.rewind();
                        s = -1;
                        if ( (synpred1_css21()) ) {s = 6;}

                        else if ( (true) ) {s = 1;}

                         
                        input.seek(index15_7);
                        if ( s>=0 ) return s;
                        break;
            }
            if (state.backtracking>0) {state.failed=true; return -1;}
            NoViableAltException nvae =
                new NoViableAltException(getDescription(), 15, _s, input);
            error(nvae);
            throw nvae;
        }
    }
 

    public static final BitSet FOLLOW_charSet_in_styleSheet42 = new BitSet(new long[]{0x0000000000F0E480L});
    public static final BitSet FOLLOW_imports_in_styleSheet52 = new BitSet(new long[]{0x0000000000F0E480L});
    public static final BitSet FOLLOW_bodylist_in_styleSheet63 = new BitSet(new long[]{0x0000000000000000L});
    public static final BitSet FOLLOW_EOF_in_styleSheet70 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_CHARSET_SYM_in_charSet96 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_STRING_in_charSet98 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SEMI_in_charSet100 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORT_SYM_in_imports128 = new BitSet(new long[]{0x0000000000000120L});
    public static final BitSet FOLLOW_set_in_imports130 = new BitSet(new long[]{0x0000000000002040L});
    public static final BitSet FOLLOW_medium_in_imports137 = new BitSet(new long[]{0x0000000000000240L});
    public static final BitSet FOLLOW_COMMA_in_imports140 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_medium_in_imports142 = new BitSet(new long[]{0x0000000000000240L});
    public static final BitSet FOLLOW_SEMI_in_imports148 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_MEDIA_SYM_in_media169 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_medium_in_media171 = new BitSet(new long[]{0x0000000000000A00L});
    public static final BitSet FOLLOW_COMMA_in_media174 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_medium_in_media176 = new BitSet(new long[]{0x0000000000000A00L});
    public static final BitSet FOLLOW_LBRACE_in_media188 = new BitSet(new long[]{0x0000000000F0A000L});
    public static final BitSet FOLLOW_ruleSet_in_media202 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_RBRACE_in_media212 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_medium232 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_bodyset_in_bodylist255 = new BitSet(new long[]{0x0000000000F0E402L});
    public static final BitSet FOLLOW_ruleSet_in_bodyset277 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_media_in_bodyset285 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_page_in_bodyset293 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PAGE_SYM_in_page317 = new BitSet(new long[]{0x0000000000008800L});
    public static final BitSet FOLLOW_pseudoPage_in_page319 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_LBRACE_in_page330 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_declaration_in_page344 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SEMI_in_page346 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_declaration_in_page349 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SEMI_in_page351 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RBRACE_in_page363 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_pseudoPage384 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_pseudoPage386 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_SOLIDUS_in_operator407 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COMMA_in_operator415 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_PLUS_in_combinator442 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_GREATER_in_combinator450 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_unaryOperator0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_property508 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_selector_in_ruleSet529 = new BitSet(new long[]{0x0000000000000A00L});
    public static final BitSet FOLLOW_COMMA_in_ruleSet532 = new BitSet(new long[]{0x0000000000F0A000L});
    public static final BitSet FOLLOW_selector_in_ruleSet534 = new BitSet(new long[]{0x0000000000000A00L});
    public static final BitSet FOLLOW_LBRACE_in_ruleSet546 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_declaration_in_ruleSet560 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SEMI_in_ruleSet562 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_declaration_in_ruleSet565 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_SEMI_in_ruleSet567 = new BitSet(new long[]{0x0000000000003000L});
    public static final BitSet FOLLOW_RBRACE_in_ruleSet579 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_simpleSelector_in_selector600 = new BitSet(new long[]{0x0000000000F6A002L});
    public static final BitSet FOLLOW_combinator_in_selector603 = new BitSet(new long[]{0x0000000000F0A000L});
    public static final BitSet FOLLOW_simpleSelector_in_selector605 = new BitSet(new long[]{0x0000000000F6A002L});
    public static final BitSet FOLLOW_elementName_in_simpleSelector624 = new BitSet(new long[]{0x0000000000F0A002L});
    public static final BitSet FOLLOW_elementSubsequent_in_simpleSelector640 = new BitSet(new long[]{0x0000000000F0A002L});
    public static final BitSet FOLLOW_elementSubsequent_in_simpleSelector664 = new BitSet(new long[]{0x0000000000F0A002L});
    public static final BitSet FOLLOW_set_in_esPred0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HASH_in_elementSubsequent720 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_cssClass_in_elementSubsequent728 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_attrib_in_elementSubsequent736 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_pseudo_in_elementSubsequent744 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_DOT_in_cssClass765 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_cssClass767 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_set_in_elementName0 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_LBRACKET_in_attrib817 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_attrib832 = new BitSet(new long[]{0x000000000F000000L});
    public static final BitSet FOLLOW_set_in_attrib873 = new BitSet(new long[]{0x0000000000002020L});
    public static final BitSet FOLLOW_set_in_attrib981 = new BitSet(new long[]{0x0000000008000000L});
    public static final BitSet FOLLOW_RBRACKET_in_attrib1082 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_COLON_in_pseudo1095 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_IDENT_in_pseudo1110 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_LPAREN_in_pseudo1168 = new BitSet(new long[]{0x0000000020002000L});
    public static final BitSet FOLLOW_IDENT_in_pseudo1170 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RPAREN_in_pseudo1173 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_property_in_declaration1209 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_COLON_in_declaration1211 = new BitSet(new long[]{0x0000007F801A2120L});
    public static final BitSet FOLLOW_expr_in_declaration1213 = new BitSet(new long[]{0x0000000040000002L});
    public static final BitSet FOLLOW_prio_in_declaration1215 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IMPORTANT_SYM_in_prio1237 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_term_in_expr1258 = new BitSet(new long[]{0x0000007F801B2322L});
    public static final BitSet FOLLOW_operator_in_expr1261 = new BitSet(new long[]{0x0000007F801A2120L});
    public static final BitSet FOLLOW_term_in_expr1263 = new BitSet(new long[]{0x0000007F801B2322L});
    public static final BitSet FOLLOW_unaryOperator_in_term1286 = new BitSet(new long[]{0x0000007F80000000L});
    public static final BitSet FOLLOW_set_in_term1297 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STRING_in_term1443 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_IDENT_in_term1451 = new BitSet(new long[]{0x0000000010000002L});
    public static final BitSet FOLLOW_LPAREN_in_term1474 = new BitSet(new long[]{0x0000007F801A2120L});
    public static final BitSet FOLLOW_expr_in_term1476 = new BitSet(new long[]{0x0000000020000000L});
    public static final BitSet FOLLOW_RPAREN_in_term1478 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_URI_in_term1501 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_hexColor_in_term1509 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_HASH_in_hexColor1530 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_esPred_in_synpred1_css21637 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_esPred_in_synpred2_css21661 = new BitSet(new long[]{0x0000000000000002L});

}