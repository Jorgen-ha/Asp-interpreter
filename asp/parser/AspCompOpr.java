package no.uio.ifi.asp.parser;


import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspCompOpr extends AspSyntax{
    TokenKind compOpr;

    AspCompOpr(int n) {
        super(n);
    }

    static AspCompOpr parse(Scanner s) {
        enterParser("comp opr");

        AspCompOpr aco = new AspCompOpr(s.curLineNum());
        aco.compOpr = s.curToken().kind;

        switch(s.curToken().kind){
            case lessToken:
                skip(s, lessToken); break;
            case greaterToken:
                skip(s, greaterToken); break;
            case doubleEqualToken:
                skip(s, doubleEqualToken); break;
            case greaterEqualToken:
                skip(s, greaterEqualToken); break;
            case lessEqualToken:
                skip(s, lessEqualToken); break;
            case notEqualToken:
                skip(s, notEqualToken); break;
        }

        leaveParser("comp opr");
        return aco;
    }


    @Override
    void prettyPrint() {
        prettyWrite(" " + compOpr + " ");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return null;
    }

}
