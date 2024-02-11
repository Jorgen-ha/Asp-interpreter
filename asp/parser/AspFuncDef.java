package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFuncDef extends AspCompoundStmt{
    public ArrayList<AspName> names = new ArrayList<>();
    public AspName funcName;
    public AspSuite suite;

    AspFuncDef(int n) {
        super(n);
    }

    static AspFuncDef parse(Scanner s) {
        enterParser("func def");
        AspFuncDef afd = new AspFuncDef(s.curLineNum());

        skip(s, defToken);
        afd.funcName = AspName.parse(s);
        skip(s,leftParToken);

        while (true){
            if (s.curToken().kind == rightParToken){
                break;
            }
            afd.names.add(AspName.parse(s));
            if (s.curToken().kind != commaToken){
                break;
            }
            skip(s, commaToken);
        }

        skip(s, rightParToken);
        skip(s, colonToken);
        afd.suite = AspSuite.parse(s);

        leaveParser("func def");
        return afd;
    }

    @Override
    void prettyPrint() {
        prettyWrite("def ");
        funcName.prettyPrint();
        prettyWrite(" (");
        for (AspName name : names.subList(0, names.size())){
            if (names.indexOf(name) > 1){
                prettyWrite(", ");
            }
            name.prettyPrint();
        }
        prettyWrite("): ");
        suite.prettyPrint();
        prettyWriteLn();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeFunc rf = new RuntimeFunc(this, curScope, funcName.nameStr);
        curScope.assign(funcName.nameStr, rf);
        trace("def " + funcName.nameStr);
        return rf;
    }

}
