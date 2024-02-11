package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspPassStmt extends AspSmallStmt{
        
    AspPassStmt(int n) {
        super(n);
    }

    static AspPassStmt parse(Scanner s) {
        enterParser("pass stmt");

        AspPassStmt aps = new AspPassStmt(s.curLineNum());
        skip(s, passToken);

        leaveParser("pass stms");
        return aps;
    }

    @Override
    void prettyPrint() {
        prettyWrite("pass");
    }

    @Override       //Som i Dags kode fra forelesning, uke 44
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        trace("pass");
        return null;
    }

}
