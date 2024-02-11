package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspIfStmt extends AspCompoundStmt{
    ArrayList<AspExpr> exprs = new ArrayList<>();
    ArrayList<AspSuite> suites = new ArrayList<>();
    int elifCount = 0;
    boolean elsePresent = false;

    AspIfStmt(int n) {
        super(n);
    }

    static AspIfStmt parse(Scanner s) {
        enterParser("if stmt");

        AspIfStmt ais = new AspIfStmt(s.curLineNum());
        skip(s, ifToken);
        while(true){
            ais.exprs.add(AspExpr.parse(s));
            skip(s, colonToken);
            ais.suites.add(AspSuite.parse(s));
            if(s.curToken().kind != elifToken) break;
            ais.elifCount++;
        }
        if(s.curToken().kind == elseToken){
            ais.elsePresent = true;
            skip(s, elseToken);
            skip(s, colonToken);
            ais.suites.add(AspSuite.parse(s));
        }

        leaveParser("if stmt");
        return ais;
    }

    @Override
    void prettyPrint() {
        prettyWrite("if ");
        exprs.get(0).prettyPrint();
        prettyWrite(": ");
        suites.get(0).prettyPrint();
        for(int i = 1; i<elifCount; i++){
            prettyWrite("elif ");
            exprs.get(i).prettyPrint();
            prettyWrite(": ");
            prettyIndent();
            suites.get(i).prettyPrint();
            prettyDedent();
        }
        if(elsePresent){
            prettyWrite("else: ");
            suites.get(suites.size()-1).prettyPrint();
        }

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        int testCount = 0;
        for (AspExpr exp : exprs){
            RuntimeValue rv = exp.eval(curScope);
            if (rv.getBoolValue("if test", this)){
                trace("if True alt #" + (testCount+1) + ":..." + rv.showInfo());
                suites.get(testCount).eval(curScope);
                return null;
            }
            testCount++;
        }
        if (suites.size() > exprs.size()){
            trace("else: ...");
            suites.get(suites.size()-1).eval(curScope);
        }
        return null;
    }

}
