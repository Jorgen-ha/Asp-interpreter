package no.uio.ifi.asp.runtime;

import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeStringValue extends RuntimeValue {
    
    String stringValue;

    public RuntimeStringValue(String v) {
	    stringValue = v;
    }

    @Override
    String typeName() {
        return "string";
    }

    @Override
    public String toString() {
        return stringValue;
    }

    @Override
    public String showInfo(){
        if(stringValue.contains("\'")){
            stringValue = stringValue.replace("\'", "");    //Removes "'" if found
            return "\'" + stringValue + "\'";               //Puts the front and back "'" back
        }else{
            stringValue = stringValue.replace("\"", "");    // Removes '"' if found
            return "\"" + stringValue + "\"";               // Puts the front and back '"' back
        }    
    }

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
        return !stringValue.equals("");
    }

    @Override
    public String getStringValue(String what, AspSyntax where) {
        return stringValue;
    }

    @Override
    public RuntimeValue evalAdd(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeStringValue){
            return new RuntimeStringValue(stringValue + v.getStringValue("+ operand", where));
        }
        runtimeError("Type error for +.", where);
        return null; // Required by the compiler
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeIntValue){
            return new RuntimeStringValue(stringValue.repeat(
                        Math.toIntExact(v.getIntValue("* operand", where))));
        }
        runtimeError("Type error for *.", where);
        return null; // Required by the compiler
    }



    @Override
    public RuntimeValue evalEqual(RuntimeValue v, AspSyntax where) {
        if(v instanceof RuntimeStringValue){
            return new RuntimeBoolValue(stringValue.equals(v.getStringValue("== operand", where)));
        }else if(v instanceof RuntimeNoneValue){
            return new RuntimeBoolValue(false);
        }
        runtimeError("Type error for ==.", where);
        return null;  // Required by the compiler
    }

    
    @Override
    public RuntimeValue evalNotEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(!stringValue.equals(v.getStringValue("!= operand", where)));
        }else if (v instanceof RuntimeNoneValue) {
            return new RuntimeBoolValue(false);
        }
        runtimeError("Type error for !=.", where);
        return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalLess(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(stringValue.length() < (v.getStringValue("< operand", where)).length());
        }
        runtimeError("Type error for <.", where);
        return null; // Required by the compiler
    }

    @Override
    public RuntimeValue evalLessEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(stringValue.length() <= (v.getStringValue("<= operand", where)).length());
        }
        runtimeError("Type error for <=.", where);
        return null; // Required by the compiler
    }

    @Override
    public RuntimeValue evalGreater(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(stringValue.length() > (v.getStringValue("> operand", where)).length());
        }
        runtimeError("Type error for >.", where);
        return null; // Required by the compiler
    }

    @Override
    public RuntimeValue evalGreaterEqual(RuntimeValue v, AspSyntax where) {
        if (v instanceof RuntimeStringValue) {
            return new RuntimeBoolValue(stringValue.length() >= (v.getStringValue(">= operand", where)).length());
        }
        runtimeError("Type error for >=.", where);
        return null; // Required by the compiler
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue v, AspSyntax where){
        if(v instanceof RuntimeIntValue){
            long i = v.getIntValue("subscription", where);
            if(i >= stringValue.length() || i < 0){           //Index compensating for front and back " or '
                runtimeError("String index " + i + " is out of range!", where);
            }
            return new RuntimeStringValue(Character.toString(stringValue.charAt((int)i)));
                                                            // Index compensating for front and back " or '
        }
        runtimeError("Type error for subscription", where);
        return null;
    }

    @Override
    public RuntimeValue evalLen(AspSyntax where){
        return new RuntimeIntValue(stringValue.length());
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!this.getBoolValue("not operand", where));
    }
}
