package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspWhileStmt extends AspCompoundStmt{
    ArrayList<AspWhileStmt> whileStmts = new ArrayList<>();
    AspExpr test;
    AspSuite body;

    AspWhileStmt(int n) {
        super(n);
    }

    static AspWhileStmt parse(Scanner s){
        enterParser("while stmt");

        AspWhileStmt aws = new AspWhileStmt(s.curLineNum());

        skip(s, whileToken);
        aws.test = AspExpr.parse(s);
        skip(s, colonToken);
        aws.body = AspSuite.parse(s);

        leaveParser("while stmt");

        return aws;
    }

    @Override
    void prettyPrint() {
        prettyWrite("while ");
        test.prettyPrint();
        prettyWrite(": ");
        body.prettyPrint();
    }

    @Override       //Som i Dags kode fra forelesning, uke 44
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        while (true){
            RuntimeValue rv = test.eval(curScope);
            if (!rv.getBoolValue("while loop test", this)) break;
            trace("while True: ...");
            body.eval(curScope);
        }
        trace("while False:");
        return null;
    }

}
