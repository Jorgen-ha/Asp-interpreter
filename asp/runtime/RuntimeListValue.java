package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeListValue extends RuntimeValue {
    public ArrayList<RuntimeValue> list;

    public RuntimeListValue(ArrayList<RuntimeValue> v) {
	    list = v;
    }

    @Override
    String typeName() {
	    return "list";
    }

     @Override
    public String showInfo() {
	    return list.toString();
    } 

    @Override
    public String toString() {
	    StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (RuntimeValue element : this.list){
            sb.append(element).append(", ");
        }
        if (sb.length() > 1){
            sb.delete(sb.length()-2, sb.length());
        }
        sb.append("]");
        return sb.toString();
    } 

    @Override
    public boolean getBoolValue(String what, AspSyntax where) {
	    return !list.isEmpty();
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
        return null;  // Required by the compiler
    }

    @Override
    public RuntimeValue evalSubscription(RuntimeValue rv, AspSyntax where){
        if (rv instanceof RuntimeIntValue){
            long i = rv.getIntValue("subscription", where);
            if (i < 0 || i >= list.size()){
                runtimeError("List index " + i + " out of range", where);
            }
            return list.get((int)i);
        }
        runtimeError("List index must be an integer.", where);
        return null; //Required by the compilator
    }

    @Override
    public RuntimeValue evalMultiply(RuntimeValue v, AspSyntax where){
        if(v instanceof RuntimeIntValue){
            ArrayList<RuntimeValue> values = new ArrayList<>();
             for(int i = 0; i < v.getIntValue("* operand", where); i++){
                 values.addAll(this.list);
             }
            return new RuntimeListValue(values);
        }
        runtimeError("Type error for *.", where);
        return null; //Required by the compiler
    }

    @Override
    public void evalAssignElem(RuntimeValue inx, RuntimeValue val, AspSyntax where) {
        list.set((int)inx.getIntValue("assign element", where), val);
    }

    @Override
    public RuntimeValue evalNot(AspSyntax where) {
        return new RuntimeBoolValue(!this.getBoolValue("not operand", where));
    }
}
