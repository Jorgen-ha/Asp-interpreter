package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspAndTest extends AspSyntax {
    ArrayList<AspNotTest> notTests = new ArrayList<>();

    AspAndTest(int n) {
        super(n);
    }
    
    public static AspAndTest parse(Scanner s){
        enterParser("and test");

        AspAndTest aat = new AspAndTest(s.curLineNum());
        while (true){
            aat.notTests.add(AspNotTest.parse(s));
            if(s.curToken().kind != andToken) break;
            skip(s, andToken);
        }

        leaveParser("and test");
        return aat;
    }

    @Override
    public void prettyPrint() {
        int nprinted = 0;

        for(AspNotTest ant : notTests){
            if(nprinted > 0){
                prettyWrite(" and ");
            }
            ant.prettyPrint(); ++nprinted;
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue v = notTests.get(0).eval(curScope);
        for(int i = 1; i < notTests.size(); i++){
            if(!v.getBoolValue("and operand", this)){
                return v;
            }
            v = notTests.get(i).eval(curScope);
        }
        return v;
    }
}
