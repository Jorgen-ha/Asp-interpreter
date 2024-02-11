package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspListDisplay extends AspAtom{
    ArrayList<AspExpr> exprs = new ArrayList<>();

    AspListDisplay(int n) {
        super(n);
    }

    static AspListDisplay parse(Scanner s) {
        enterParser("list display");
        AspListDisplay ald = new AspListDisplay(s.curLineNum());

        skip(s, leftBracketToken);
        if (s.curToken().kind != rightBracketToken){
            while (true) {
                ald.exprs.add(AspExpr.parse(s));
                if (s.curToken().kind != commaToken) {
                    break;
                }
                skip(s, commaToken);
            }
        }

        skip(s, rightBracketToken);

        leaveParser("list display");
        return ald;
    }

    @Override
    void prettyPrint() {
        prettyWrite("[");
        for (int i = 0; i < exprs.size(); i++){
            exprs.get(i).prettyPrint();
            if (i < exprs.size()-1){
                prettyWrite(", ");
            }
        }
        prettyWrite("]");
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> values = new ArrayList<>();
        for (int i = 0; i < exprs.size(); i++){
            values.add(exprs.get(i).eval(curScope));
        }
        RuntimeListValue rlv = new RuntimeListValue(values);
        return rlv;
    }

}
