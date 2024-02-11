package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.*;


abstract public class AspSmallStmt extends AspSyntax{

    AspSmallStmt(int n){
        super(n);
    }

    static AspSmallStmt parse(Scanner s) {
        enterParser("small stmt");

        AspSmallStmt ass = null;
        switch (s.curToken().kind){
            case nameToken:
                if(s.anyEqualToken()){
                    ass = AspAssignment.parse(s); break;
                } else {
                    ass = AspExprStmt.parse(s); break;
                }
            case globalToken:
                ass = AspGlobalStmt.parse(s); break;
            case passToken:
                ass = AspPassStmt.parse(s); break;
            case returnToken:
                ass = AspReturnStmt.parse(s); break;
            case integerToken:
            case leftBracketToken:
            case leftBraceToken:
            case stringToken:
            case trueToken:
            case falseToken:
                ass = AspExprStmt.parse(s); break;
        }
        
        leaveParser("small stmt");
        return ass;
    }
}
