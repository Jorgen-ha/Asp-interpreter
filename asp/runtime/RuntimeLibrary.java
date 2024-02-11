package no.uio.ifi.asp.runtime;

import java.util.ArrayList;
import java.util.Scanner;
import no.uio.ifi.asp.parser.AspSyntax;

public class RuntimeLibrary extends RuntimeScope {
    private Scanner in = new Scanner(System.in);

    public RuntimeLibrary() {

        //float
        assign("float", new RuntimeFunc("float"){
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where){
                checkNumParams(actualParams, 1, "float", where);
                RuntimeValue rv = actualParams.get(0);
                double floatValue = 0.0;

                if (rv instanceof RuntimeStringValue){
                    try {
                        floatValue = Double.parseDouble(rv.getStringValue("float", where));
                    } catch (Exception e){
                        runtimeError("String " + rv.showInfo() + " is not a legal float", where);
                    }
                } else if (rv instanceof RuntimeIntValue){
                    floatValue = (double) rv.getIntValue("float", where);
                } else if (rv instanceof RuntimeFloatValue){
                    floatValue = rv.getFloatValue("float", where);
                } else{
                    runtimeError("Type error: parameter to float is neither a number nor a string", where);
                }

                return new RuntimeFloatValue(floatValue);
            }
        });

        //input
        assign("input", new RuntimeFunc("input"){
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where){
                checkNumParams(actualParams, 1, "input", where);
                System.out.print(actualParams.get(0));
                return new RuntimeStringValue(in.nextLine());
            }
        });
        
        //int
        assign("int", new RuntimeFunc("int"){
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where) {
                checkNumParams(actualParams, 1, "int", where);
                RuntimeValue rv = actualParams.get(0);
                long intValue = 0;
            
                if (rv instanceof RuntimeStringValue){
                    try {
                        intValue = Long.parseLong(rv.getStringValue("int", where));
                    } catch (Exception e){
                        runtimeError("String " + rv.showInfo() + " is not a legal int", where);
                    }
                } else if (rv instanceof RuntimeFloatValue){
                    intValue = (long) rv.getFloatValue("int", where);
                } else if (rv instanceof RuntimeIntValue){
                    intValue = rv.getIntValue("int", where);
                } else{
                    runtimeError("Type error: parameter to int is neither a number nor a string", where);
                }
            
                return new RuntimeIntValue(intValue);
            }
        });
            

        //len - Som i Dags kode fra forlesning, uke 45
        assign("len", new RuntimeFunc("len"){
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where){
                checkNumParams(actualParams, 1, "len", where);
                return actualParams.get(0).evalLen(where);
            }
        });

        //print - Som i Dags kode fra forelesning, uke 45
        assign("print", new RuntimeFunc("print"){
            @Override
            public RuntimeValue evalFuncCall( ArrayList<RuntimeValue> actualParams, AspSyntax where){
                for (int i = 0; i < actualParams.size(); i++){
                    if (i > 0){
                        System.out.print(" ");
                    }   
                    System.out.print(actualParams.get(i).toString()); 
                }        
                System.out.println();
                return new RuntimeNoneValue();
            }
        });

        //range
        assign("range", new RuntimeFunc("range"){
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where){
                checkNumParams(actualParams, 2, "range", where);
                long start = actualParams.get(0).getIntValue("range", where);
                long end = actualParams.get(1).getIntValue("range", where);
                ArrayList<RuntimeValue> range = new ArrayList<>();

                for (long i = start; i < end; i++){
                    range.add(new RuntimeIntValue(i));
                }
                return new RuntimeListValue(range);
            }
        });

        //str
        assign("str", new RuntimeFunc("str"){
            @Override
            public RuntimeValue evalFuncCall(ArrayList<RuntimeValue> actualParams, AspSyntax where){
                checkNumParams(actualParams, 1 , "str", where);
                return new RuntimeStringValue(actualParams.get(0).toString());
            }
        });
    }

    //Som i Dags kode fra forelesning, uke 45
    private void checkNumParams(ArrayList<RuntimeValue> actArgs, int nCorrect, String id, 
                                                                               AspSyntax where) {
        if (actArgs.size() != nCorrect)
            RuntimeValue.runtimeError("Wrong number of parameters to " + id + "!", where);
        }
}



