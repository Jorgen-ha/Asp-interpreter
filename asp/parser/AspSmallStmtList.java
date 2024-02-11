package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspSmallStmtList extends AspStmt{
    ArrayList<AspSmallStmt> smallStmts = new ArrayList<>();
    boolean semi = false;

    AspSmallStmtList(int n) {
        super(n);
    }

    static AspSmallStmtList parse(Scanner s) {
        enterParser("small stmt list");

        AspSmallStmtList assl = new AspSmallStmtList(s.curLineNum());
        while(true){
            assl.smallStmts.add(AspSmallStmt.parse(s));
            if(s.curToken().kind != semicolonToken) break;
            skip(s, semicolonToken);
        }

        if(s.curToken().kind != newLineToken){
            skip(s, semicolonToken);
            assl.semi = true;
            skip(s, newLineToken);
        }else{
            skip(s, newLineToken);
        }

        leaveParser("small stmt list");
        return assl;
    }

    @Override
    void prettyPrint() {
        int nprinted = 0;
        for(AspSmallStmt ass : smallStmts){
            if(nprinted > 0){
                prettyWrite("; ");
            }
            nprinted++;
            ass.prettyPrint();
        }
        prettyWriteLn();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for (AspSmallStmt stmt : smallStmts){
            stmt.eval(curScope);
        }
        return null;
    }

}
