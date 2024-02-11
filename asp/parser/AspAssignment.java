package no.uio.ifi.asp.parser;

import java.util.ArrayList;
import no.uio.ifi.asp.main.*;
import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspAssignment extends AspSmallStmt{
    AspName name;
    AspExpr expr;
    ArrayList<AspSubscription> subs = new ArrayList<>();

    AspAssignment(int n) {
        super(n);
    }

    static AspAssignment parse(Scanner s) {
        enterParser("assignment");
        
        AspAssignment aa = new AspAssignment(s.curLineNum());
        aa.name = AspName.parse(s);
        if(s.curToken().kind == equalToken){
            skip(s, equalToken);
            aa.expr = AspExpr.parse(s);
        }else{
            while(true){
                aa.subs.add(AspSubscription.parse(s));
                if(s.curToken().kind == equalToken) break;
            }
            skip(s, equalToken);
            aa.expr = AspExpr.parse(s);
        }

        leaveParser("assignment");
        return aa;
    }

    @Override
    void prettyPrint() {
        name.prettyPrint();
        for(AspSubscription sub : subs){
            sub.prettyPrint();
        }
        prettyWrite(" = ");
        expr.prettyPrint();
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        RuntimeValue rv = expr.eval(curScope);

        if (subs.isEmpty()){
            if (curScope.hasGlobalName(name.nameStr)){
                Main.globalScope.assign(name.nameStr, rv);    
            } else {
                curScope.assign(name.nameStr, rv);
            }
            trace(name.nameStr + " = " + rv.showInfo());
        } else {
            String traceString = name.nameStr;
            RuntimeValue a = name.eval(curScope);
            RuntimeValue inx = subs.get(0).eval(curScope);
            
            for(int i = 1; i < subs.size()-1; i++){
                traceString += "[" + inx.showInfo() + "]";
                a = a.evalSubscription(inx, this);
                inx = subs.get(i).eval(curScope);
            }

            traceString += "[" + inx.showInfo() + "]";
            a.evalAssignElem(inx, rv, this);
            traceString += " = " + rv.showInfo();
            trace(traceString);
        }
        return null;
    }
}
