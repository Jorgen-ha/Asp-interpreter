package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspIntegerLiteral extends AspAtom{
    long integerLiteral;

    AspIntegerLiteral(int n) {
        super(n);
    }

    static AspIntegerLiteral parse(Scanner s) {
        enterParser("integer literal");

        AspIntegerLiteral ail = new AspIntegerLiteral(s.curLineNum());
        ail.integerLiteral = s.curToken().integerLit;

        skip(s, integerToken);

        leaveParser("integer literal");
        return ail;
    }

    @Override
    void prettyPrint() {
        prettyWrite(Long.toString(integerLiteral));

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeIntValue(integerLiteral);
    }
}
