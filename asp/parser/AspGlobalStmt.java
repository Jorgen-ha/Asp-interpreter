package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspGlobalStmt extends AspSmallStmt{
    ArrayList<AspName> names = new ArrayList<>();

    AspGlobalStmt(int n) {
        super(n);
    }

    static AspGlobalStmt parse(Scanner s) {
        enterParser("global stmt");

        AspGlobalStmt ags = new AspGlobalStmt(s.curLineNum());
        skip(s, globalToken);
        while(true){
            ags.names.add(AspName.parse(s));
            if(s.curToken().kind != commaToken) break;
            skip(s, commaToken);
        }

        leaveParser("global stmt");
        return ags;
    }

    @Override
    public void prettyPrint() {
        prettyWrite("global ");
        for(AspName name : names){
            name.prettyPrint();
        }
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        for (AspName name : names){
            if (!curScope.hasGlobalName(name.nameStr)){
                curScope.registerGlobalName(name.nameStr);
                name.eval(curScope);
            }

        }
        return null;
    }
}
