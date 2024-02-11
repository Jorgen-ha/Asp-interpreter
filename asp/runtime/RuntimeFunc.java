package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import no.uio.ifi.asp.parser.AspFuncDef;
import no.uio.ifi.asp.parser.AspName;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeFunc extends RuntimeValue {
    AspFuncDef def;
    RuntimeScope defScope;
    String name;

    public RuntimeFunc(String def){
        name = def;
    }

    public RuntimeFunc(AspFuncDef afd, RuntimeScope rs, String defName){
        def = afd;
        defScope = rs;
        name = defName;
    }

    @Override
    public String typeName(){
        return "Function";
    }

    @Override
    public String showInfo(){
        return name;
    }

    @Override
    public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actParameters, AspSyntax where){
        ArrayList<AspName> formParameters = def.names;

        if (actParameters.size() != formParameters.size()){
            runtimeError("Number of arguments does not match. Expected: " + 
                         formParameters.size(), where);
        }

        RuntimeScope newScope = new RuntimeScope(defScope);

        for (int i = 0; i < actParameters.size(); i++){
            newScope.assign(formParameters.get(i).nameStr, actParameters.get(i));
        }

        // Som i Dags kode fra forelesning, uke 45
        try {
            def.suite.eval(newScope);
        } catch (RuntimeReturnValue rrv){
            return rrv.value;
        }
        return new RuntimeNoneValue();
    }
}