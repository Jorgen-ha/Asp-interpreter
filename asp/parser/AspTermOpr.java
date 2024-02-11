package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspTermOpr extends AspSyntax{
    TokenKind termOpr;

    AspTermOpr(int n) {
        super(n);
    }

    static AspTermOpr parse(Scanner s) {
        enterParser("term opr");

        AspTermOpr ato = new AspTermOpr(s.curLineNum());
        ato.termOpr = s.curToken().kind;

        switch(s.curToken().kind){
            case minusToken:
                skip(s, minusToken); break;
            case plusToken:
                skip(s, plusToken); break;
        }

        leaveParser("term opr");
        return ato;
    }

    @Override
    void prettyPrint() {
        prettyWrite(" " + termOpr + " ");

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return null;
    }

}
