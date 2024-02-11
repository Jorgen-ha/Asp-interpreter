package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;


public class AspComparison extends AspSyntax{
    ArrayList<AspCompOpr> aspCompOprs = new ArrayList<>();
    ArrayList<AspTerm> aspTerms = new ArrayList<>();


    AspComparison(int n) {
        super(n);
    }

    static AspComparison parse(Scanner s) {
        enterParser("comparison");

        AspComparison ac = new AspComparison(s.curLineNum());
        while (true){
            ac.aspTerms.add(AspTerm.parse(s));
            if (!s.isCompOpr()) break;
            ac.aspCompOprs.add(AspCompOpr.parse(s));
        }

        leaveParser("comparison");
        return ac;
    }


    @Override
    void prettyPrint() {
        for (int i = 0; i < aspTerms.size(); i++){
            aspTerms.get(i).prettyPrint();
            if(i != aspCompOprs.size()){
                aspCompOprs.get(i).prettyPrint();
            }
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = aspTerms.get(0).eval(curScope);
        for (int i = 1; i < aspTerms.size(); i++){
            TokenKind tk = aspCompOprs.get(i-1).compOpr;
            if (i != 1){
                rv = aspTerms.get(i-1).eval(curScope);
            }
            switch (tk){
                case greaterToken:
                    rv = rv.evalGreater(aspTerms.get(i).eval(curScope), this); break;
                case lessToken:
                    rv = rv.evalLess(aspTerms.get(i).eval(curScope), this); break;
                case greaterEqualToken:
                    rv = rv.evalGreaterEqual(aspTerms.get(i).eval(curScope), this); break;
                case lessEqualToken:
                    rv = rv.evalLessEqual(aspTerms.get(i).eval(curScope), this); break;
                case doubleEqualToken:
                    rv = rv.evalEqual(aspTerms.get(i).eval(curScope), this); break;
                case notEqualToken:
                    rv = rv.evalNotEqual(aspTerms.get(i).eval(curScope), this); break;
                default:
                    Main.panic("Illegal comparison operator: " + tk + "!");
                }
            if (!rv.getBoolValue("comparison operator", this)){
                return rv;
            }
        }
        return rv;
    }
}
