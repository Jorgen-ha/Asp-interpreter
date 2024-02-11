package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspDictDisplay extends AspAtom{
    ArrayList<AspStringLiteral> strLits = new ArrayList<>();
    ArrayList<AspExpr> exprs = new ArrayList<>();


    AspDictDisplay(int n) {
        super(n);
    }

    static AspDictDisplay parse(Scanner s) {
        enterParser("dict display");

        AspDictDisplay add = new AspDictDisplay(s.curLineNum());
        skip(s, leftBraceToken);
        while(true){
            if (s.curToken().kind != rightBraceToken){
                add.strLits.add(AspStringLiteral.parse(s));
                skip(s, colonToken);
                add.exprs.add(AspExpr.parse(s));
                if(s.curToken().kind != commaToken) break;
                skip(s, commaToken);
            }else{
                break;
            }
        }
        skip(s, rightBraceToken);

        leaveParser("dict display");
        return add;
    }

    @Override
    void prettyPrint() {
        int dictEntries = strLits.size();

        prettyWrite("{");
        for(int i=0; i<dictEntries; i++){
            strLits.get(i).prettyPrint();
            prettyWrite(": ");
            exprs.get(i).prettyPrint();
            if(i < dictEntries-1){
                prettyWrite(", ");
            }
        }
        prettyWrite("}");

    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        LinkedHashMap<String, RuntimeValue> values = new LinkedHashMap<>();
        for (int i = 0; i < strLits.size(); i++){
            values.put(strLits.get(i).stringLiteral, exprs.get(i).eval(curScope));
        }
        RuntimeDictValue rdv = new RuntimeDictValue(values);
        return rdv;
    }

}
