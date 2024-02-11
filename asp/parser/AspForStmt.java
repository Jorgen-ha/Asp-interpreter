package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspForStmt extends AspCompoundStmt{
    AspName name;
    AspExpr expr;
    AspSuite suite;

    AspForStmt(int n) {
        super(n);
    }

    static AspForStmt parse(Scanner s) {
        enterParser("for stmt");
        AspForStmt afs = new AspForStmt(s.curLineNum());

        skip(s, forToken);
        afs.name = AspName.parse(s);
        skip(s, inToken);
        afs.expr = AspExpr.parse(s);
        skip(s, colonToken);
        afs.suite = AspSuite.parse(s);

        leaveParser("for stmt");
        return afs;
    }

    @Override
    void prettyPrint() {
        prettyWrite("for ");
        name.prettyPrint();
        prettyWrite(" in ");
        expr.prettyPrint();
        prettyWrite(": ");
        suite.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = expr.eval(curScope);

        if (rv instanceof RuntimeListValue){
            ArrayList<RuntimeValue> list = ((RuntimeListValue) rv).list;
            int c = 1;
            for (RuntimeValue element : list){
                trace("For #" + c + ": " + name.nameStr + " = " + element);
                curScope.assign(name.nameStr, element);
                suite.eval(curScope);
                c++;
            }
        }
        return null;
    }

}
