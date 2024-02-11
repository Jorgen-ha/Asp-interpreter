package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;


public class AspTerm extends AspSyntax{
    ArrayList<AspTermOpr> aspTermOprs = new ArrayList<>();
    ArrayList<AspFactor> aspFactors = new ArrayList<>();

    AspTerm(int n) {
        super(n);
    }

    static AspTerm parse(Scanner s) {
        enterParser("term");

        AspTerm at = new AspTerm(s.curLineNum());
        while (true){
            at.aspFactors.add(AspFactor.parse(s));
            if (!s.isTermOpr()) break;
            at.aspTermOprs.add(AspTermOpr.parse(s));
        }

        leaveParser("term");
        return at;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < aspFactors.size(); i++) {
            aspFactors.get(i).prettyPrint();
            if (i != aspTermOprs.size()) {
                aspTermOprs.get(i).prettyPrint();
            }
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = aspFactors.get(0).eval(curScope);
        for(int i = 1; i < aspFactors.size(); i++){
            TokenKind k = aspTermOprs.get(i-1).termOpr;
            switch(k){
                case minusToken:
                    rv = rv.evalSubtract(aspFactors.get(i).eval(curScope), this); break;
                case plusToken:
                    rv = rv.evalAdd(aspFactors.get(i).eval(curScope), this); break;
                default:
                    Main.panic("Illegal term operator: " + k + "!");
            }
        }
        return rv;
    }

}
