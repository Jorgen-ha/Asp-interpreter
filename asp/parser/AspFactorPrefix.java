package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactorPrefix extends AspSyntax{
    TokenKind factorPrefix;

    AspFactorPrefix(int n) {
        super(n);
    }

    static AspFactorPrefix parse(Scanner s) {
        enterParser("factor prefix");

        AspFactorPrefix afp = new AspFactorPrefix(s.curLineNum());
        afp.factorPrefix = s.curToken().kind;

        switch(s.curToken().kind){
            case minusToken:
                skip(s, minusToken); break;
            case plusToken:
                skip(s, plusToken); break;
        }

        leaveParser("factor prefix");
        return afp;
    }

    @Override
    void prettyPrint() {
        prettyWrite(factorPrefix + " ");

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return null;
    }

}
