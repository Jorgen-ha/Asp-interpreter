package no.uio.ifi.asp.parser;


import no.uio.ifi.asp.runtime.*;
import no.uio.ifi.asp.scanner.*;
import static no.uio.ifi.asp.scanner.TokenKind.*;

public class AspBooleanLiteral extends AspAtom{
   
    Boolean booleanLiteral;

    AspBooleanLiteral(int n) {
        super(n);
    }

    static AspBooleanLiteral parse(Scanner s) {
        enterParser("boolean literal");

        AspBooleanLiteral abl = new AspBooleanLiteral(s.curLineNum());
        abl.booleanLiteral = s.curToken().kind == trueToken;

            if(s.curToken().kind == trueToken ){
                skip(s, trueToken);

            } else if(s.curToken().kind == falseToken){
                skip(s, falseToken);
            }

        leaveParser("boolean literal");
        return abl;
    }

    @Override
    void prettyPrint() {
        if(booleanLiteral){
            prettyWrite("True");
        }else{
            prettyWrite("False");
        }
    }

    @Override
    RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        return new RuntimeBoolValue(booleanLiteral);
    }
}
