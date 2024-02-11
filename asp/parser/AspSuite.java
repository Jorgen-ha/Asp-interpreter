package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;


public class AspSuite extends AspSyntax{
    ArrayList<AspStmt> stmts = new ArrayList<>();
    AspSmallStmtList stmtList;
    boolean newline = false;

    AspSuite(int n) {
        super(n);
    }

    static AspSuite parse(Scanner s) {
        enterParser("suite");

        AspSuite as = new AspSuite(s.curLineNum());
        if(s.curToken().kind == newLineToken){
            as.newline = true;
            skip(s, newLineToken);
            skip(s, indentToken);
            while(true){
                as.stmts.add(AspStmt.parse(s));
                if(s.curToken().kind == dedentToken) break;
            }
            skip(s, dedentToken);
        }else{
            as.stmtList = AspSmallStmtList.parse(s);
        }

        leaveParser("suite");
        return as;
    }

    @Override
    void prettyPrint() {
        if(newline){
            prettyWriteLn();
            prettyIndent();
            for(AspStmt stmt : stmts){
                stmt.prettyPrint();
            }
            prettyDedent();
        }else{
            stmtList.prettyPrint();
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        if (stmtList != null){
            stmtList.eval(curScope);
        } else {
            for(AspStmt as : stmts){
                as.eval(curScope);
            }
        }
        return null;
    }
}
