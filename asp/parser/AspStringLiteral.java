package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;


public class AspStringLiteral extends AspAtom{
    String stringLiteral;

    AspStringLiteral(int n) {
        super(n);
    }

    static AspStringLiteral parse(Scanner s) {
        enterParser("string literal");
        AspStringLiteral asl = new AspStringLiteral(s.curLineNum());
        asl.stringLiteral = s.curToken().stringLit;

        skip(s, TokenKind.stringToken);

        leaveParser("string literal");
        return asl;
    }

    @Override
    void prettyPrint() {
        if (stringLiteral.indexOf('"') >= 0){
            stringLiteral = "'" + stringLiteral + "'";
        }else {
            stringLiteral = '"' + stringLiteral + '"';
        }
        prettyWrite(stringLiteral);
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeStringValue(stringLiteral);
    }

}
