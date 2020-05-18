package code_generation;

import AST.AST;
import semantics.SemanticAnalysis;
import supporting_structures.Register;
import supporting_structures.ScopeVar;

import java.util.*;

public class CodeGen {
    private static List <String> blockData = new ArrayList<>();
    private static List <String> blockText = new ArrayList<>();
    private static List <String> blockBss = new ArrayList<>();

    private static List<String> createdVar = new ArrayList<>();
    private static HashMap<String, String> stringForFunction = new HashMap<>();
    private static List<Register> listRegisters = new ArrayList<>();
    private static String lastCreatedVariable = "non";
    private static int conditionalJumpCount = 0;

    public static void analysis(List<AST> listNodes) {
//        for (AST s : listNodes) { AST.print_tree(s, 4, 100); }
        for(AST node : listNodes) {
            switch (node.getTypeToken()) {
                case ("Block class"):
                    List<AST> bodyClass = node.getChildren().get(1).getChildren();
                    analysis(bodyClass);
                    break;
                case ("Block function"):
                    List<AST> declarationFunction = node.getChildren().get(0).getChildren();
                    List<AST> bodyFunction = node.getChildren().get(1).getChildren();
                    function_declaration_processing(declarationFunction);
                    analysis(bodyFunction);
                    if(SemanticAnalysis.function_name_definition(declarationFunction).equalsIgnoreCase("main")) {
                        blockText.add("\t\tmovl\t$0, %eax");
                        blockText.add("\t\tleave");
                        blockText.add("\t\tret");
                    }
                    break;
                case ("Block for"):
                    block_for_processing(node.getChildren());
                    break;
                case ("Block if"):
                    block_if_processing(node.getChildren());
                    break;
                case ("Call func fin"):
                    function_call_processing(node.getChildren().get(0).getChildren());
                    break;
                case ("Var creat fin"):
                case ("Creat and assign"):
                case ("Creat several var fin"):
                    var_creat_processing(node.getChildren());
                    break;
                case ("Crement"):
                    crement_processing(node.getChildren());
                    break;
                case ("Assign operation"):
                    assign_processing(node, get_type_var(node.getChildren().get(0)));
                    break;
            }
        }
    }

    public static void crement_processing(List<AST> listNodes) {
        AST variable = listNodes.get(0);
        AST operator = listNodes.get(1);
        switch (operator.getToken()) {
            case ("--"):
                blockText.add("\t\tdec" + dimension(get_type_var(variable)) + "\t" + generatedOperand(variable));
                break;
            case ("++"):
                blockText.add("\t\tinc" + dimension(get_type_var(variable)) + "\t" + generatedOperand(variable));
                break;
        }
    }

    public static void block_for_processing(List<AST> listNodes) {
        AST firstPartFor = listNodes.get(0).getChildren().get(1).getChildren().get(1);
        AST conditionFor = listNodes.get(0).getChildren().get(1).getChildren().get(2).getChildren().get(0);
        AST lastPartFor = listNodes.get(0).getChildren().get(1).getChildren().get(3);
        String nameConditionJump = get_name_condition_jump();
        String startCycle = get_name_condition_jump();

        analysis(Collections.singletonList(firstPartFor));
        blockText.add("\t\tjmp \t" + nameConditionJump);

        blockText.add(startCycle + ":");
        analysis(listNodes.get(1).getChildren());

        analysis(Collections.singletonList(lastPartFor));
        blockText.add(nameConditionJump + ":");
        generation_condition_cycle(startCycle, conditionFor.getChildren());
    }

    public static void generation_condition_cycle(String nameJump, List<AST> logicalExpr) {
        AST operand1 = logicalExpr.get(0);
        AST operator = logicalExpr.get(1);
        AST operand2 = logicalExpr.get(2);
        String reg1 = get_free_register_name();
        String reg2 = get_free_register_name();

        register_value(operand1, reg1);
        register_value(operand2, reg2);

        blockText.add("\t\tcmpl\t%" + reg2 + ", %" + reg1);
        switch (operator.getToken()) {
            case ("<"):
                blockText.add("\t\tjl \t" + nameJump + "\n");
                break;
            case (">"):
                blockText.add("\t\tjg \t" + nameJump + "\n");
                break;
            case ("=="):
                blockText.add("\t\tje \t" + nameJump + "\n");
                break;
            case ("!="):
                blockText.add("\t\tjne \t" + nameJump + "\n");
                break;
        }
        register_exemption(reg1);
        register_exemption(reg2);
    }

    public static void register_value(AST operand, String nameReg) {
        switch (operand.getTypeToken()) {
            case ("Arithmetic expression"):
                List <AST> exprPriority = prioritizing_arithmetic_expressions(operand.getChildren());
                blockText.addAll(arithmetic_expression_processing(exprPriority.get(0), nameReg, "int"));
                break;
            default:
                blockText.add("\t\tmovl\t" + generatedOperand(operand) + ", %" + nameReg);
        }
    }

    public static void block_if_processing (List<AST> listNodes) {
        List<AST> logicalExpr = get_logical_expr(listNodes.get(0).getChildren());
        assert logicalExpr != null;
        String nameConditionJump = get_name_condition_jump();
        AST operand1 = logicalExpr.get(0);
        AST operator = logicalExpr.get(1);
        AST operand2 = logicalExpr.get(2);
        String reg1 = get_free_register_name();
        String reg2 = get_free_register_name();

        blockText.add("\t\t# if " + generatedCommentExpr(operand1, operator, operand2));

        register_value(operand1, reg1);
        register_value(operand2, reg2);

        blockText.add("\t\tcmpl\t%" + reg2 + ", %" + reg1);
        switch (logicalExpr.get(1).getToken()) {
            case ("<"):
                blockText.add("\t\tjnl \t" + nameConditionJump + "\n");
                break;
            case (">"):
                blockText.add("\t\tjng \t" + nameConditionJump + "\n");
                break;
            case ("=="):
                blockText.add("\t\tjne \t" + nameConditionJump + "\n");
                break;
        }
        register_exemption(reg1);
        register_exemption(reg2);

        analysis(listNodes.get(1).getChildren());
        blockText.add(nameConditionJump + ":\n");
    }

    public static String get_name_condition_jump() {
        conditionalJumpCount++;
        return "condition_jump_" + conditionalJumpCount;
    }

    public static List<AST> get_logical_expr(List<AST> listNodes) {
        for(AST node : listNodes) {
            if("Logical expression".equals(node.getTypeToken())) {
                return node.getChildren();
            }
            else if(!node.getChildren().isEmpty()) {
                return get_logical_expr(node.getChildren());
            }
        }
        return null;
    }

    public static String get_string_for_function(String string_for_func) {
        if (!stringForFunction.containsKey(string_for_func)) {
            String name = "str" + stringForFunction.size();
            blockData.add(name + ":");
            blockData.add("\t\t.string " + string_for_func);
            stringForFunction.put(string_for_func, name);
        }
        return stringForFunction.get(string_for_func);
    }

    public static void assign_processing(AST node, String type) {
//        for (AST s : node.getChildren()) { AST.print_tree(s, 4, 100); }
        AST variable = node.getChildren().get(0);
        AST value = node.getChildren().get(2);
        if("Id".equals(value.getTypeToken())) {
            String regBuf = get_free_register_name();
            if (checkDeclaredVar(variable.getToken()))
                blockBss.addAll(asm_variable_declaration_generation(type, "", variable));
            blockText.add("\t\tmov" + dimension(type) + " \t" + generatedOperand(value) + ", %" + regBuf);
            blockText.add("\t\tmov" + dimension(type) + " \t%" + regBuf + ", " + lastCreatedVariable + "\n");
            register_exemption(regBuf);
        } else if(!"Arithmetic expression".equals(value.getToken())) {
            if (checkDeclaredVar(variable.getToken())) {
                blockData.addAll(asm_variable_declaration_generation(type, value.getToken(), variable));
            } else {
                blockText.add("\t\tmov" + dimension(type) + " \t" + generatedOperand(value) + ", " + lastCreatedVariable + "\n");
            }
        } else {
            List <AST> exprPriority = prioritizing_arithmetic_expressions(value.getChildren());
            String nameReg = get_free_register_name();
            if (checkDeclaredVar(variable.getToken())) {
                blockBss.addAll(asm_variable_declaration_generation(type, "", variable));
            }
            blockText.addAll(arithmetic_expression_processing(exprPriority.get(0), nameReg, type));
            blockText.add("\t\tmov" + dimension(type) + "\t%" + nameReg + ", " + lastCreatedVariable + "\n");
            register_exemption(nameReg);
        }
    }

    public static boolean checkDeclaredVar(String nameVar) {
        if (createdVar.contains(nameVar)) {
            return false;
        }
        createdVar.add(nameVar);
        return true;
    }

    public static void var_creat_processing (List<AST> listNodes) {
        List <String> generatedCode;
        String type = listNodes.get(0).getToken();
        for(AST variable : listNodes) {
            switch (variable.getTypeToken()) {
                case ("Assign call func"):
                    blockText.add("\t\tmov $" + get_string_for_function("\"%d\"") + ", %rdi\t# scanf");
                    generatedCode = asm_variable_declaration_generation(type, "", variable.getChildren().get(0));
                    blockBss.addAll(generatedCode);
                    blockText.add("\t\tleaq " + lastCreatedVariable + ", %rsi");
                    blockText.add("\t\tcall scanf\n");
                    break;
                case ("Enum var"):
                    generatedCode = asm_variable_declaration_generation(type, "", variable.getChildren().get(0));
                    blockBss.addAll(generatedCode);
                    break;
                case ("Assign operation with comma"):
                case ("Assign operation"):
                    assign_processing(variable, type);
                    break;
                case ("Id"):
                    generatedCode = asm_variable_declaration_generation(type, "", variable);
                    blockBss.addAll(generatedCode);
                    break;
            }
        }
    }

    public static List<String> arithmetic_expression_processing (AST expr, String nameReg, String dataType) {
        List<String> generatedCode = new ArrayList<>();
        AST leftOperand, rightOperand, operator;
        leftOperand = expr.getChildren().get(0);
        rightOperand = expr.getChildren().get(2);
        operator = expr.getChildren().get(1);

        if(!leftOperand.getChildren().isEmpty() && !rightOperand.getChildren().isEmpty()) {
            List<String> bufGeneratedCode = arithmetic_expression_processing(leftOperand, nameReg, dataType);
            generatedCode.addAll(bufGeneratedCode);

            String nameReg2 = get_free_register_name();

            bufGeneratedCode = arithmetic_expression_processing(rightOperand, nameReg2, dataType);
            generatedCode.addAll(bufGeneratedCode);

//            for (String s : generatedCode) { System.out.println(s);}
//            System.out.println();

            leftOperand.setToken(nameReg);
            leftOperand.setTypeToken("reg");
            rightOperand.setToken(nameReg2);
            rightOperand.setTypeToken("reg");

            arithmetic_operation_processing(leftOperand, operator, rightOperand, generatedCode, dataType, nameReg);

            register_exemption(nameReg2);
        } else if(!leftOperand.getChildren().isEmpty()) {
            generatedCode = arithmetic_expression_processing(leftOperand, nameReg, dataType);
            arithmetic_operation_processing(null, operator, rightOperand, generatedCode, dataType, nameReg);
        } else if(!rightOperand.getChildren().isEmpty()) {
            generatedCode = arithmetic_expression_processing(rightOperand, nameReg, dataType);
            arithmetic_operation_processing(leftOperand, operator, null, generatedCode, dataType, nameReg);
        } else {
            arithmetic_operation_processing(leftOperand, operator, rightOperand, generatedCode, dataType, nameReg);
        }

        return generatedCode;
    }

    public static void arithmetic_operation_processing (AST operand1, AST operator, AST operand2,
                                                        List<String> generatedCode, String dataType, String nameReg) {
        String comment = generatedCommentExpr(operand1, operator, operand2);
        String nameReg2 = get_free_register_name();
        if(operand1 != null && operand2 != null) {
            switch (operator.getToken()) {
                case ("+"):
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) +
                            ", %" + nameReg + comment);
                    generatedCode.add("\t\tadd" + dimension(dataType) + "\t" + generatedOperand(operand2) +
                            ", %" + nameReg);
                    break;
                case ("-"):
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) +
                            ", %" + nameReg + comment);
                    generatedCode.add("\t\tsub" + dimension(dataType) + "\t" + generatedOperand(operand2) +
                            ", %" + nameReg);
                    break;
                case ("*"):
                    if(!nameReg.equals("eax")) {
                        String nameReg3 = get_free_register_name();
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg2 + comment);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) + ", %eax");
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand2) + ", %" + nameReg3);
                        generatedCode.add("\t\tmul" + dimension(dataType) + "\t%" + nameReg3);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg2 + ", %eax");
                        register_exemption(nameReg2);
                        register_exemption(nameReg3);
                    } else {
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) + ", %" + nameReg + comment);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand2) + ", %" + nameReg2);
                        generatedCode.add("\t\tmul" + dimension(dataType) + "\t%" + nameReg2);
                    }
                    break;
                case ("/"):
                    if(!nameReg.equals("eax")) {
                        String nameReg3 = get_free_register_name();
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg2 + comment);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) + ", %eax");
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand2) + ", %" + nameReg3);
                        generatedCode.add("\t\txor\t%edx, %edx");
                        generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg3);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg2 + ", %eax");
                        register_exemption(nameReg2);
                        register_exemption(nameReg3);
                    } else {
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) + ", %" + nameReg + comment);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand2) + ", %" + nameReg2);
                        generatedCode.add("\t\txor\t%edx, %edx");
                        generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg2);
                    }
                    break;
                case ("%"):
                    if(!nameReg.equals("eax")) {
                        String nameReg3 = get_free_register_name();
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg2 + comment);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) + ", %eax");
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand2) + ", %" + nameReg3);
                        generatedCode.add("\t\txor \t%edx, %edx");
                        generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg3);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%edx" + ", %" + nameReg);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg2 + ", %eax");
                        register_exemption(nameReg2);
                        register_exemption(nameReg3);
                    } else {
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand1) + ", %" + nameReg + comment);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand2) + ", %" + nameReg2);
                        generatedCode.add("\t\txor \t%edx, %edx");
                        generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg2);
                        generatedCode.add("\t\tmov" + dimension(dataType) + "\t%edx, %eax");
                    }
                    break;
            }
        } else if(operand1 == null) {
            arithmetic_expr_and_var_processing(operand2, operator, generatedCode, dataType, nameReg, comment, nameReg2);
        } else {
            arithmetic_expr_and_var_processing(operand1, operator, generatedCode, dataType, nameReg, comment, nameReg2);
        }
        register_exemption(nameReg2);
    }

    public static void arithmetic_expr_and_var_processing(AST operand, AST operator, List<String> generatedCode,
                                                      String dataType, String nameReg, String comment, String nameReg2) {
        switch (operator.getToken()) {
            case ("+"):
                generatedCode.add("\t\tadd" + dimension(dataType) + "\t" + generatedOperand(operand) +
                        ", %" + nameReg + comment);
                break;
            case ("-"):
                generatedCode.add("\t\tsub" + dimension(dataType) + "\t" + generatedOperand(operand) +
                        ", %" + nameReg + comment);
                break;
            case ("*"):
                if(!nameReg.equals("eax")) {
                    String nameReg3 = get_free_register_name();
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg2 + comment);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg + ", %eax");
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand) + ", %" + nameReg3);
                    generatedCode.add("\t\tmul" + dimension(dataType) + "\t%" + nameReg3);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg2 + ", %eax");
                    register_exemption(nameReg2);
                    register_exemption(nameReg3);
                } else {
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand) + ", %" + nameReg2 + comment);
                    generatedCode.add("\t\tmul" + dimension(dataType) + "\t%" + nameReg2);
                }
                break;
            case ("/"):
                if(!nameReg.equals("eax")) {
                    String nameReg3 = get_free_register_name();
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg2 + comment);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg + ", %eax");
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand) + ", %" + nameReg3);
                    generatedCode.add("\t\txor\t%edx, %edx");
                    generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg3);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg2 + ", %eax");
                    register_exemption(nameReg2);
                    register_exemption(nameReg3);
                } else {
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand) + ", %" + nameReg2 + comment);
                    generatedCode.add("\t\txor \t%edx, %edx");
                    generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg2);
                }
                break;
            case ("%"):
                if(!nameReg.equals("eax")) {
                    String nameReg3 = get_free_register_name();
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%eax" + ", %" + nameReg2 + comment);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg + ", %eax");
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand) + ", %" + nameReg3);
                    generatedCode.add("\t\txor \t%edx, %edx");
                    generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg3);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%edx" + ", %" + nameReg);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%" + nameReg2 + ", %eax");
                    register_exemption(nameReg2);
                    register_exemption(nameReg3);
                } else {
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t" + generatedOperand(operand) + ", %" + nameReg2 + comment);
                    generatedCode.add("\t\txor \t%edx, %edx");
                    generatedCode.add("\t\tdiv" + dimension(dataType) + "\t%" + nameReg2);
                    generatedCode.add("\t\tmov" + dimension(dataType) + "\t%edx, %eax");
                }
                break;
        }
    }

    public static String generatedCommentExpr (AST operand1, AST operator, AST operand2) {
        String operandToken1, operandToken2;
        if(operand1 == null) {
            operandToken1 = "$";
        } else {
            operandToken1 = operand1.getToken();
        }
        if(operand2 == null) {
            operandToken2 = "$";
        } else {
            operandToken2 = operand2.getToken();
        }
        return "\t# " + operandToken1 + " " + operator.getToken() + " " + operandToken2;
    }

    public static String generatedOperand (AST operand) {
        if(operand.getTypeToken().equals("reg")) {
            return "%" + operand.getToken();
        } else if(operand.getTypeToken().equals("Id")) {
            return operand.getToken();
        } else {
            return "$" + operand.getToken();
        }
    }

    public  static String dimension (String dataType) {
        if ("double".equals(dataType)) {
            return "q";
        }
        return "l";
    }

    public static void function_call_processing (List<AST> listNodes) {
        String nameFunction = listNodes.get(0).getToken();
        switch (nameFunction){
            case ("Console.Write"):
            case ("Console.WriteLine"):
                AST argument = listNodes.get(1).getChildren().get(1);
//                generatedCode = asm_variable_declaration_generation("StringLiteral", "", argument);
//                blockData.addAll(generatedCode);
                switch (argument.getTypeToken()) {
                    case("StringLiteral"):
                        blockText.add("\t\tmov \t$" + get_string_for_function(argument.getToken()) + ", %rdi\t# " + nameFunction + " " + argument.getToken());
                        blockText.add("\t\tcall\tprintf \n");
                        break;
                    case ("Id"):
                        blockText.add("\t\tmov\t$" + get_string_for_function("\"%d\\n\"") + ", %rdi\t# " + nameFunction + " " + argument.getToken());
                        blockText.add("\t\tmov\t" + generatedOperand(argument) + ", %rsi");
                        blockText.add("\t\tcall\tprintf\n");
                        break;
                }
                break;
        }
    }

    public static List<String> asm_variable_declaration_generation(String type, String value, AST variable) {
        List<String> generatedCodeDecl = new ArrayList<>();
        String declaredVar;
        switch (type) {
//
//            case ("StringLiteral"):
//                declaredVar = get_name_declared_var("str");
//                lastCreatedVariable = declaredVar;
//                generatedCodeDecl.add("\n" + declaredVar + ":");
//                switch (node.getTypeToken()){
//                    case ("StringLiteral"):
//                        generatedCodeDecl.add("\t\t.string " + node.getToken());
//                        return generatedCodeDecl;
//                    case ("Id"):
//                        generatedCodeDecl.add("\t\t.string " + get_str_for_printf(get_type_var(node)));
//                        return generatedCodeDecl;
//                }
//                break;

            case ("int"):
                declaredVar = variable.getToken();
                lastCreatedVariable = declaredVar;
                generatedCodeDecl.add("\n" + declaredVar + ":");
                if("".equals(value)) {
                    generatedCodeDecl.add("\t\t.space 4");
                } else {
                    generatedCodeDecl.add("\t\t.int " + value);
                }
                return generatedCodeDecl;
        }
        return new ArrayList<>();
    }

    public static void function_declaration_processing (List<AST> listNodes) {
        if(SemanticAnalysis.function_name_definition(listNodes).equalsIgnoreCase("main")) {
            blockText.add("\n.globl  main");
            blockText.add(".type  main, @function");
            blockText.add("\nmain:");
            blockText.add("\nmain:");
            blockText.add("\t\tpushq %rbp");
            blockText.add("\t\tmovq %rsp, %rbp\n");
        }
    }

    public List<String> get_assembler_code() {
        List <String> finalList = new ArrayList<>();

        blockData.add("\n");
        blockBss.add("\n");
        blockText.add("\n");

        finalList.addAll(blockData);
        finalList.addAll(blockBss);
        finalList.addAll(blockText);
        return finalList;
    }

    public CodeGen() {
        blockData.add(".data\n");
        blockBss.add(".bss");
        blockText.add(".text");

        listRegisters.add(new Register("eax", false));
        listRegisters.add(new Register("ebx", false));
        listRegisters.add(new Register("ecx", false));
        listRegisters.add(new Register("ebp", false));
        listRegisters.add(new Register("esi", false));
        listRegisters.add(new Register("edi", false));
    }

    public static String get_type_var(AST var) {
        HashMap<String, List<ScopeVar>> table = SemanticAnalysis.getTable();
        String nameVar = var.getToken();
        if (table.containsKey(nameVar)) {
            return  table.get(nameVar).get(0).getType();
        }
        return "";
    }

    public static List<AST> prioritizing_arithmetic_expressions (List<AST> listNodes) {
        boolean firstPassFlag = false;
        List<AST> list = tree_alignment(listNodes);
        String nameNode;
        for (int i = 0; i < list.size(); i++) {
            String operation = list.get(i).getToken();
            switch (operation) {
                case("+"):
                    nameNode = "Add";
                    break;
                case("-"):
                    nameNode = "Sub";
                    break;
                case("*"):
                    nameNode = "Mul";
                    break;
                case("/"):
                    nameNode = "Div";
                    break;
                case("%"):
                    nameNode = "Mod";
                    break;
                default:
                    nameNode = "";
            }
            if(!nameNode.equals("")) {
                if ("Mul".equals(nameNode) || "Div".equals(nameNode) || "Mod".equals(nameNode)) {
                    add_to_node(list, i - 1, i + 1, nameNode);
                    i -= 2;
                }
                else if (firstPassFlag) {
                    add_to_node(list, i - 1, i + 1, nameNode);
                    i -= 2;
                }
            }
            if(list.size() != 1 && i == list.size() - 1 && !firstPassFlag) {
                firstPassFlag = true;
                i = 0;
            }
        }
        return list;
    }

    public static void add_to_node(List<AST> listNodes, int startingIndex, int endIndex, String tokenTypeNewName) {
        AST node = new AST(tokenTypeNewName, tokenTypeNewName, new ArrayList<>());
        for(int i = startingIndex; i <= endIndex; i++) {
            AST child = new AST(listNodes.get(i));
            node.add_children(child);
        }
        listNodes.remove(startingIndex + 2);
        listNodes.remove(startingIndex + 1);
        listNodes.set(startingIndex, node);
        System.out.println();
    }

    public static List<AST> tree_alignment(List<AST> listNodes) {
        if (listNodes.get(0).getTypeToken().equals("Arithmetic expression")) {
            List<AST> bufList = new ArrayList<AST>(tree_alignment(listNodes.get(0).getChildren()));
            bufList.add(listNodes.get(1));
            bufList.add(listNodes.get(2));
            return bufList;
        } else {
            return listNodes;
        }
    }

    public  static String get_free_register_name () {
        for(Register reg : listRegisters) {
            if(!reg.isEmployment()) {
                reg.setEmployment(true);
                return reg.getName();
            }
        }
        return "Non";
    }

    public  static void register_exemption (String nameReg) {
        for(Register reg : listRegisters) {
            if(reg.getName().equals(nameReg)) {
                reg.setEmployment(false);
            }
        }
    }

}
