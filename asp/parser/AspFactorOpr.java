package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspFactorOpr extends AspSyntax{
    TokenKind factorOpr;

    AspFactorOpr(int n) {
        super(n);
    }

    static AspFactorOpr parse(Scanner s) {
    enterParser("factor opr");

    AspFactorOpr afo = new AspFactorOpr(s.curLineNum());
    afo.factorOpr = s.curToken().kind;

    switch(s.curToken().kind){
        case astToken:
            skip(s, astToken); break;
        case slashToken:
            skip(s, slashToken); break;
        case percentToken:
            skip(s, percentToken); break;
        case doubleSlashToken:
            skip(s, doubleSlashToken); break;
    }

    leaveParser("factor opr");
    return afo;

    }

    @Override
    void prettyPrint() {
        prettyWrite(" " + factorOpr + " ");

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return null;
    }

}
