package no.uio.ifi.asp.runtime;

import java.util.HashMap;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeDictValue extends RuntimeValue {
    HashMap<String, RuntimeValue> dict;

    public RuntimeDictValue(HashMap<String, RuntimeValue> v) {
	    dict = v;
    }

    @Override
    String typeName() {
	    return "dictionary";
    }

     @Override
    public String showInfo() {
	    StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (String key : dict.keySet()){
            sb.append(key).append(": ").append(dict.get(key)).append(", ");
        }
        if (sb.length() > 1){
            sb.delete(sb.length()-2, sb.length());
        }
        sb.append("}");
        return sb.toString();
    } 

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
	    return !dict.isEmpty();
    }

    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        runtimeError("Type error for ==.", where);
        return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(true);
        }
        runtimeError("Type error for !=.", where);
        return null; // Required by the compiler
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            String key = v.getStringValue("subscription", where);
            if (!dict.containsKey(key)){
                runtimeError("Dictionary key '" + key + "' is undefined!", where);
            }
            return dict.get(v.getStringValue("subscription", where));
        }
        runtimeError("Type error for subscription.", where);
        return null;  // Required by the compiler
    }

    
    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!this.getBoolValue("not operand", where));
    }
}
