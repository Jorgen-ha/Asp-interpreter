package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspNoneLiteral extends AspAtom{
    TokenKind noneLit;
    AspNoneLiteral(int n) {
        super(n);
    }

    static AspNoneLiteral parse(Scanner s) {
        enterParser("none");

        AspNoneLiteral anl = new AspNoneLiteral(s.curLineNum());
        anl.noneLit = s.curToken().kind;

        skip(s, noneToken);

        leaveParser("none");
        return anl;
    }

    @Override
    void prettyPrint() {
        prettyWrite("None");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeNoneValue();
    }

}
