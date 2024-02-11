package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspExpr extends AspSyntax {
    ArrayList<AspAndTest> andTests = new ArrayList<>();

    AspExpr(int n) {
	    super(n);
    }

    public static AspExpr parse(Scanner s) {
        enterParser("expr");

        AspExpr ae = new AspExpr(s.curLineNum());
        while(true){
            ae.andTests.add(AspAndTest.parse(s));
            if(s.curToken().kind != orToken) break;
            skip(s, orToken);
        }

        leaveParser("expr");
        return ae;
    }

    @Override
    public void prettyPrint() {
        int nprinted = 0;

        for(AspAndTest aat : andTests){
            if(nprinted > 0){
                prettyWrite(" or ");
            }
            aat.prettyPrint(); nprinted++;
        }
    }


    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = andTests.get(0).eval(curScope);
        for (int i = 1; i < andTests.size(); i++){
            if (rv.getBoolValue("or operand", this)){
                return rv;
            }
            rv = andTests.get(i).eval(curScope);
        }
        return rv;
    }
}
