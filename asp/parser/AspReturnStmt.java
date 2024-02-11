package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspReturnStmt extends AspSmallStmt{
    AspExpr expr;

    AspReturnStmt(int n) {
        super(n);
    }

    static AspReturnStmt parse(Scanner s) {
        enterParser("return stmt");

        AspReturnStmt ars = new AspReturnStmt(s.curLineNum());
        skip(s, returnToken);
        ars.expr = AspExpr.parse(s);

        leaveParser("return stmt");
        return ars;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("return ");
        expr.prettyPrint();
    }

    @Override   //Som i koden fra Dags forelesning, uke 45
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = expr.eval(curScope);
        trace("return " + rv.showInfo());
        throw new RuntimeReturnValue(rv, lineNum);
    }
}
