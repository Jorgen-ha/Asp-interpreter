package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspPrimary extends AspSyntax{
    AspAtom atom;
    AspPrimarySuffix primarySuffix;

    AspPrimary(int n) {
        super(n);
    }

    static AspPrimary parse(Scanner s) {
        enterParser("primary");

        AspPrimary ap = new AspPrimary(s.curLineNum());
        ap.atom = AspAtom.parse(s);
        if (s.curToken().kind == leftParToken || s.curToken().kind == leftBracketToken){
            ap.primarySuffix = AspPrimarySuffix.parse(s);
        }
        

        leaveParser("primary");
        return ap;
    }

    @Override
    void prettyPrint() {
        atom.prettyPrint();
        primarySuffix.prettyPrint();
        
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = atom.eval(curScope);
            if (primarySuffix instanceof AspSubscription){
                rv = rv.evalSubscription(primarySuffix.eval(curScope), this);
            } else if (primarySuffix instanceof AspArguments){
                RuntimeListValue parameters = (RuntimeListValue) primarySuffix.eval(curScope);
                trace("Call function " + rv.showInfo() + " with parameters " + parameters.toString());
                
                rv = rv.evalFuncCall(parameters.list, this);
            }
        
        return rv;
    }

}
