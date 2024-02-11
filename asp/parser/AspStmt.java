package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.scanner.*;


abstract public class AspStmt extends AspSyntax{

    AspStmt(int n){
        super(n);
    }

    static AspStmt parse(Scanner s) {
        enterParser("stmt");
        
        AspStmt as = null;

        switch (s.curToken().kind){
            case forToken:
            case ifToken:
            case whileToken:
            case defToken:
                as = AspCompoundStmt.parse(s); break;
            case nameToken:
            case andToken:
            case globalToken:
            case passToken:
            case returnToken:
            case leftBracketToken:
            case leftBraceToken:
            case integerToken:
            case stringToken:
            case trueToken:
            case falseToken:
                as = AspSmallStmtList.parse(s); break;
        }

        leaveParser("stmt");
        return as;

    }
}
