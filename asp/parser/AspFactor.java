package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;


public class AspFactor extends AspSyntax{
    ArrayList<AspFactorPrefix> factPrefixs = new ArrayList<>();
    ArrayList<AspFactorOpr> factOprs = new ArrayList<>();
    ArrayList<AspPrimary> prims = new ArrayList<>();
    boolean fac = true;

    AspFactor(int n) {
        super(n);
    }

    static AspFactor parse(Scanner s) {
        enterParser("factor");

        AspFactor af = new AspFactor(s.curLineNum());

        while (true){
                if (s.isFactorPrefix()){
                    af.factPrefixs.add(AspFactorPrefix.parse(s));
                }
                af.factPrefixs.add(null);
                af.prims.add(AspPrimary.parse(s));

                if (!s.isFactorOpr()){
                    break;
                }
                af.factOprs.add(AspFactorOpr.parse(s));
        }

        leaveParser("factor");
        return af;
    }

    @Override
    void prettyPrint() {
        for (int i = 0; i < prims.size(); i++){
            if (factPrefixs.get(i) != null){
                factPrefixs.get(i).prettyPrint();
            }
            prims.get(i).prettyPrint();
            if (i != factOprs.size()){
                factOprs.get(i).prettyPrint();
            }
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = prims.get(0).eval(curScope);
        if (factPrefixs.get(0) != null){
            TokenKind tk = factPrefixs.get(0).factorPrefix;
            switch (tk){
                case minusToken:
                    rv = rv.evalNegate(this); break;
                case plusToken:
                    rv = rv.evalPositive(this); break;
            }
        }

        for (int i = 1; i < prims.size(); i++){
            RuntimeValue prim = prims.get(i).eval(curScope);
            if (factPrefixs.get(i) != null){
                TokenKind prefix = factPrefixs.get(i).factorPrefix;
                switch (prefix){
                    case minusToken:
                        prim = prim.evalNegate(this); break;
                    case plusToken:
                        prim = prim.evalPositive(this); break;
                }
            }
            TokenKind opr = factOprs.get(i-1).factorOpr;
            switch (opr){
                case astToken:
                    rv = rv.evalMultiply(prim, this); break;
                case slashToken:
                    rv = rv.evalDivide(prim, this); break;
                case percentToken:
                    rv = rv.evalModulo(prim, this); break;
                case doubleSlashToken:
                    rv = rv.evalIntDivide(prim, this); break;
                default:
                    Main.panic("Illegal term operator: " + opr + "!");
            }
        }
        return rv;
    }

}
