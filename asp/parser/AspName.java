package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspName extends AspAtom{
    public String nameStr;

    AspName(int n) {
        super(n);
    }

    static AspName parse(Scanner s) {
        enterParser("name");

        AspName an = new AspName(s.curLineNum());
        an.nameStr = s.curToken().name;
        skip(s, nameToken);

        leaveParser("name");
        return an;
    }

    @Override
    void prettyPrint() {
        prettyWrite(nameStr);
    }

    @Override       //Som i Dags kode fra forelesning, uke 44
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return curScope.find(nameStr, this);
    }
}
