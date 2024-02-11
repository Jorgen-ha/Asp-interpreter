package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspArguments extends AspPrimarySuffix{
    ArrayList<AspExpr> aspExprs = new ArrayList<>();

    AspArguments(int n) {
        super(n);
    }

    static AspArguments parse(Scanner s) {
        enterParser("arguments");

        AspArguments aa = new AspArguments(s.curLineNum());
        skip(s, leftParToken);

        if (s.curToken().kind != rightParToken){
            while (true){
                aa.aspExprs.add(AspExpr.parse(s));
                if (s.curToken().kind != commaToken){
                    break;
                }
                skip(s, commaToken);
            }
        }

        skip(s, rightParToken);
        leaveParser("arguments");
        return aa;
    }

    @Override
    void prettyPrint() {
        int nprinted = 0;
        prettyWrite("(");
        for (AspExpr aspExpr : aspExprs){
            if (nprinted > 0){
                prettyWrite(", ");
            }
        aspExpr.prettyPrint();
        nprinted++;
        }
        prettyWrite(")");

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        ArrayList<RuntimeValue> actParameters = new ArrayList<>();
        for (AspExpr ae : aspExprs){
            actParameters.add(ae.eval(curScope));
        }
        RuntimeListValue rlv = new RuntimeListValue(actParameters);
        return rlv;
    }
}
