package code_generation;

import AST.AST;
import semantics.SemanticAnalysis;
import supporting_structures.DeclaredVar;
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
                    String nameFunction = SemanticAnalysis.function_name_definition(declarationFunction);
                    if(!nameFunction.equalsIgnoreCase("main")) {
                        renaming_variables(nameFunction, node.getChildren());
                    }
                    function_declaration_processing(declarationFunction);
                    analysis(bodyFunction);
//                    if(nameFunction.equalsIgnoreCase("main")) {
                        blockText.add("\t\tleave");
                        blockText.add("\t\tret\n");
//                    }
                    break;
                case ("Block for"):
                    block_for_processing(node.getChildren());
                    break;
                case ("Block if else"):
                    block_if_else_processing(node.getChildren());
                    break;
                case ("Block if"):
                    block_if_processing(node.getChildren());
                    break;
                case ("Call func fin"):
                    function_call_processing(node.getChildren().get(0).getChildren());
                    break;
                case ("Typed id"):
                case ("Var creat fin"):
                case ("Creat and assign"):
                case ("Creat several var fin"):
                    var_creat_processing(node.getChildren());
                    break;
                case ("Crement"):
                    crement_processing(node.getChildren());
                    break;
                case ("Crement fin"):
                    crement_processing(node.getChildren().get(0).getChildren());
                    break;
                case ("Assign call func"):
                case ("Assign operation"):
                    assign_processing(node, get_type_var(node.getChildren().get(0)));
                    break;
                case ("Memory assign"):
                    memory_assign_processing(node.getChildren());
                    break;
                case ("Return fin"):
                    return_processing(node.getChildren());
                    break;
            }
        }
    }

    public static void return_processing(List<AST> listNodes) {
        AST returnVariable = listNodes.get(1);
        String type = get_type_var(returnVariable);
        blockText.add("\t\tmov" + dimension(type) + " \t" + generatedOperand(returnVariable) + ", %edx");
    }

    public static void renaming_variables(String nameFunction, List<AST> listNodes) {
        for (AST node : listNodes) {
            switch (node.getTypeToken()) {
                case ("Call func fin"):
                    renaming_variables(nameFunction, node.getChildren().get(0).getChildren().get(1).getChildren());
                    break;
                case ("Id"):
                    if(!node.getToken().equals(nameFunction) && !node.getToken().equals("Console.ReadLine")) {
                        node.setToken(node.getToken() + "_" + nameFunction);
                    }
                    break;
                default:
                    if (!node.getChildren().isEmpty())
                        renaming_variables(nameFunction, node.getChildren());
            }
        }
//                for (AST s : listNodes) { AST.print_tree(s, 4, 100); }
    }

    public static void block_if_else_processing(List<AST> listNodes) {
        AST blockIf = listNodes.get(0);
        AST blockElse = listNodes.get(1);
        List<AST> logicalExpr = get_logical_expr(blockIf.getChildren());
        assert logicalExpr != null;
        String nameConditionJump1 = get_name_condition_jump();
        String nameConditionJump2 = get_name_condition_jump();
        AST operand1 = logicalExpr.get(0);
        AST operator = logicalExpr.get(1);
        AST operand2 = logicalExpr.get(2);
        String reg1 = get_free_register_name();
        String reg2 = get_free_register_name();
        String bufReg1 = reg1;
        String bufReg2 = reg2;
        String typeOperand = get_type_operand(operand1);
        blockText.add("\t\t# if " + generatedCommentExpr(operand1, operator, operand2));

        if(!typeOperand.equals("int")) {
            reg1 = bufReg1.charAt(1) + "h";
            reg2 = bufReg2.charAt(1) + "h";
        }

        register_value(operand1, reg1, typeOperand);
        register_value(operand2, reg2, typeOperand);

        blockText.add("\t\tcmp" + dimension(typeOperand) + "\t%" + reg2 + ", %" + reg1);
        switch (logicalExpr.get(1).getToken()) {
            case ("<"):
                blockText.add("\t\tjnl \t" + nameConditionJump1 + "\n");
                break;
            case (">"):
                blockText.add("\t\tjng \t" + nameConditionJump1 + "\n");
                break;
            case ("=="):
                blockText.add("\t\tjne \t" + nameConditionJump1 + "\n");
                break;
            case ("!="):
                blockText.add("\t\tje \t" + nameConditionJump1 + "\n");
                break;
        }
        if(!typeOperand.equals("int")) {
            reg1 = bufReg1;
            reg2 = bufReg2;
        }

        register_exemption(reg1);
        register_exemption(reg2);

        analysis(blockIf.getChildren().get(1).getChildren());
        blockText.add("\t\tjmp \t" + nameConditionJump2);
        blockText.add(nameConditionJump1 + ":\n");
        analysis(blockElse.getChildren().get(1).getChildren());
        blockText.add(nameConditionJump2 + ":\n");
    }

    public static void memory_assign_processing(List<AST> listNodes) {
//        AST dataType = listNodes.get(0).getChildren().get(0);
        AST nameArray = listNodes.get(0).getChildren().get(2);
        AST amountElements = listNodes.get(2).getChildren().get(2).getChildren().get(1);
        AST buf = new AST(nameArray);
        int size = 4 * Integer.parseInt(amountElements.getToken());
        blockBss.add("\n" + nameArray.getToken() + ":");
        blockBss.add("\t\t.space " + size);
        buf.setToken(buf.getToken() + ".Length");
        blockData.addAll(asm_variable_declaration_generation("int", amountElements.getToken(), buf));
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
        String type = get_type_operand(operand1);
        String reg1 = get_free_register_name();
        String reg2 = get_free_register_name();

        register_value(operand1, reg1, type);
        register_value(operand2, reg2, type);

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

    public static void register_value(AST operand, String nameReg, String type) {
        switch (operand.getTypeToken()) {
            case ("Arithmetic expression"):
                List <AST> exprPriority = prioritizing_arithmetic_expressions(operand.getChildren());
                blockText.addAll(arithmetic_expression_processing(exprPriority.get(0), nameReg, type));
                break;
            default:
                blockText.add("\t\tmov" + dimension(type) + "\t" + generatedOperand(operand) + ", %" + nameReg);
        }
    }

    public static String get_type_operand(AST operand) {
        switch (operand.getToken()) {
            case ("Array element"):
                return get_type_var(operand.getChildren().get(0));
            default:
                return "int";
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
        String bufReg1 = reg1;
        String bufReg2 = reg2;
        String typeOperand = get_type_operand(operand1);
        blockText.add("\t\t# if " + generatedCommentExpr(operand1, operator, operand2));

        if(!typeOperand.equals("int")) {
            reg1 = bufReg1.charAt(1) + "h";
            reg2 = bufReg2.charAt(1) + "h";
        }

        register_value(operand1, reg1, typeOperand);
        register_value(operand2, reg2, typeOperand);

        blockText.add("\t\tcmp" + dimension(typeOperand) + "\t%" + reg2 + ", %" + reg1);
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
            case ("!="):
                blockText.add("\t\tje \t" + nameConditionJump + "\n");
                break;
        }
        if(!typeOperand.equals("int")) {
            reg1 = bufReg1;
            reg2 = bufReg2;
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
//        System.out.println();
        AST variable = node.getChildren().get(0);
        AST value = node.getChildren().get(2);
        if("Id".equals(value.getTypeToken()) || "Array element".equals(value.getTypeToken())) {
            String regBuf = get_free_register_name();
            if (checkDeclaredVar(variable.getToken()) && !"Array element".equals(variable.getToken())) {
                blockBss.addAll(asm_variable_declaration_generation(type, "", variable));
            }
            blockText.add("\t\tmov" + dimension(type) + " \t" + generatedOperand(value) + ", %" + regBuf);
            blockText.add("\t\tmov" + dimension(type) + " \t%" + regBuf + ", " + generatedOperand(variable) + "\n");
            register_exemption(regBuf);
        } else if("StringLiteral".equals(value.getTypeToken())) {
            if (checkDeclaredVar(variable.getToken()) && !"Array element".equals(variable.getToken())) {
                blockData.addAll(asm_variable_declaration_generation(type, value.getToken(), variable));
            } else {
                String nameReg = get_free_register_name();
                int size = value.getToken().length() - 2;
                blockText.add("\t\tmovl \t" + get_string_for_function(value.getToken()) + ", %" + nameReg);
                blockText.add("\t\tmovl \t%" + nameReg + ", " + generatedOperand(variable));
                blockText.add("\t\tmovl \t$" + size + ", " + variable.getToken() + ".Length");
                register_exemption(nameReg);
            }
        } else if("Call func".equals(value.getTypeToken())) {
            String nameFunc = value.getChildren().get(0).getToken();
            if (checkDeclaredVar(variable.getToken()) && !"Array element".equals(variable.getToken())) {
                blockBss.addAll(asm_variable_declaration_generation(type, "", variable));
            }
            if(nameFunc.equals("Console.ReadLine")) {
                blockText.add("\t\tmov $" + get_string_for_function("\"%d\"") + ", %rdi\t# scanf");
                blockText.add("\t\tleaq " + generatedOperand(variable) + ", %rsi");
                blockText.add("\t\tcall scanf\n");
            } else {
                List<AST> listPassinVar = value.getChildren().get(1).getChildren();
                call_function_processing(nameFunc, listPassinVar);
                blockText.add("\t\tmov" + dimension(type) + " \t%edx" + ", " + generatedOperand(variable) + "\n");
            }
        } else if(!"Arithmetic expression".equals(value.getToken())) {
            if (checkDeclaredVar(variable.getToken()) && !"Array element".equals(variable.getToken())) {
                blockData.addAll(asm_variable_declaration_generation(type, value.getToken(), variable));
            } else {
                blockText.add("\t\tmov" + dimension(type) + " \t" + generatedOperand(value) + ", " + generatedOperand(variable) + "\n");
            }
        } else {
            List <AST> exprPriority = prioritizing_arithmetic_expressions(value.getChildren());
            String nameReg = get_free_register_name();
            if (checkDeclaredVar(variable.getToken())  && !"Array element".equals(variable.getToken())) {
                blockBss.addAll(asm_variable_declaration_generation(type, "", variable));
            }
            blockText.addAll(arithmetic_expression_processing(exprPriority.get(0), nameReg, type));
            blockText.add("\t\tmov" + dimension(type) + "\t%" + nameReg + ", " + generatedOperand(variable) + "\n");
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
        String type = listNodes.get(0).getToken();
        for(AST variable : listNodes) {
            switch (variable.getTypeToken()) {
                case ("Assign call func"):
                case ("Assign operation with comma"):
                case ("Assign operation"):
                    assign_processing(variable, type);
                    break;
                case ("Enum var"):
                    if(checkDeclaredVar(variable.getToken()))
                        blockBss.addAll(asm_variable_declaration_generation(type, "", variable.getChildren().get(0)));
                    break;
                case ("Id"):
                    if(checkDeclaredVar(variable.getToken()))
                        blockBss.addAll(asm_variable_declaration_generation(type, "", variable));
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
        switch (operand.getTypeToken()) {
            case "reg":
                return "%" + operand.getToken();
            case "Id":
                return operand.getToken();
            case "Array element":
                AST index = operand.getChildren().get(1).getChildren().get(1);
                String nameVar = operand.getChildren().get(0).getToken();
                blockText.add("\t\tmovl \t" + generatedOperand(index) + ", %edx");
                return nameVar + "(,%edx," + get_size_type(get_type_var(operand.getChildren().get(0))) + ")";
            default:
                return "$" + operand.getToken();
        }
    }

    public static String get_size_type(String type) {
        switch (type) {
            case ("double"):
                return "8";
            case ("string"):
                return "1";
            default:
                return "4";
        }
    }

    public  static String dimension (String dataType) {
        if ("double".equals(dataType)) {
            return "q";
        } else if("string".equals(dataType)) {
            return "b";
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
                    case ("Array element"):
                    case ("Id"):
                        String type;
                        String format;
                        if ("Array element".equals(argument.getToken())) {
                            type = get_type_var(argument.getChildren().get(0));
                            format = get_format(type);
                        } else {
                            type = get_type_var(argument);
                            format = get_format(type);
                            if("string".equals(type))
                                format = "%s";
                        }
                        if("Console.WriteLine".equals(nameFunction))
                            blockText.add("\t\tmov \t$" + get_string_for_function("\"" + format + "\\n\"") + ", %rdi\t# "
                                                                                + nameFunction + " " + argument.getToken());
                        else
                            blockText.add("\t\tmov \t$" + get_string_for_function("\"" + format + "\"") + ", %rdi\t# "
                                                                                + nameFunction + " " + argument.getToken());
                        blockText.add("\t\tmov \t" + generatedOperand(argument) + ", %rsi");
                        blockText.add("\t\tcall\tprintf\n");
                        break;
                }
                break;
            default:
                List<AST> listPassinVar = listNodes.get(1).getChildren();
                call_function_processing(nameFunction, listPassinVar);
        }
    }

    public static void call_function_processing(String nameFunction, List<AST> listPassinVar) {
        List<DeclaredVar> listVar = SemanticAnalysis.getDeclaredFunc().get(nameFunction);
        for(int i = 1; i < listPassinVar.size() - 1; i++) {
            listVar.get(i - 1).setName(listVar.get(i - 1).getName() + "_" + nameFunction);
            if(listPassinVar.get(i).getTypeToken().equals("Enum numb") || listPassinVar.get(i).getTypeToken().equals("Enum var"))
                passing_variables(listVar.get(i - 1), listPassinVar.get(i).getChildren().get(0));
            else
                passing_variables(listVar.get(i - 1), listPassinVar.get(i));
        }
        blockText.add("\t\tcall \t" + nameFunction + "\n");
    }

    public static void passing_variables(DeclaredVar declVar, AST pasVar) {
        String nameReg = get_free_register_name();
        if(checkDeclaredVar(declVar.getName())   && !"Array element".equals(declVar.getName())) {
            blockBss.addAll(asm_variable_declaration_generation(declVar.getType(), "", new AST(declVar.getName(), "Id")));
        }
        blockText.add("\t\t# " + pasVar.getToken() + " -> " + declVar.getName());
        switch (pasVar.getTypeToken()) {
            case ("DecimalInteger"):
                blockText.add("\t\tmovl \t" + generatedOperand(pasVar) + ", %" + nameReg);
                blockText.add("\t\tmovl \t" + "%" + nameReg + ", " + declVar.getName());
                break;
            case ("StringLiteral"):
                blockText.add("\t\tmovl \t$" + get_string_for_function(pasVar.getToken()) + ", %" + nameReg);
                blockText.add("\t\tmovl \t" + "%" + nameReg + ", " + declVar.getName());
                break;
            case ("Id"):
                String type = get_type_var(pasVar);
                if("string".equals(type))
                    blockText.add("\t\tmovl \t$" + generatedOperand(pasVar) + ", %" + nameReg);
                else
                    blockText.add("\t\tmovl \t" + generatedOperand(pasVar) + ", %" + nameReg);
                blockText.add("\t\tmovl \t" + "%" + nameReg + ", " + declVar.getName());
                break;
        }
        register_exemption(nameReg);
    }

    public static String get_format(String type) {
        switch (type) {
            case ("int"):
                return "%d";
            case ("double"):
                return "%f";
            case ("string"):
                return "%c";
            default:
                return "%d";
        }
    }

    public static List<String> asm_variable_declaration_generation(String type, String value, AST variable) {
        List<String> generatedCodeDecl = new ArrayList<>();
        String declaredVar;
        switch (type) {
            case ("string"):
                declaredVar = variable.getToken();
                checkDeclaredVar(declaredVar);
                generatedCodeDecl.add("\n" + declaredVar + ":");
                if("".equals(value)) {
                    generatedCodeDecl.add("\t\t.space 100");
                } else {
                    generatedCodeDecl.add("\t\t.string " + value);
                    int size = value.length() - 2;
                    generatedCodeDecl.addAll(asm_variable_declaration_generation("int", Integer.toString(size), new AST (declaredVar + ".Length", "Id")));
                }
                return generatedCodeDecl;
            case ("int"):
                declaredVar = variable.getToken();
                checkDeclaredVar(declaredVar);
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
        String nameFunction = SemanticAnalysis.function_name_definition(listNodes);
        if(nameFunction.equalsIgnoreCase("main")) {
            blockText.add("\n.globl  main");
            blockText.add(".type  main, @function");
            blockText.add("\nmain:");
        } else {
            blockText.add(nameFunction + ":");
            analysis(listNodes.get(3).getChildren());
        }
        blockText.add("\t\tpushq %rbp");
        blockText.add("\t\tmovq %rsp, %rbp\n");
    }

    public static List<String> get_assembler_code() {
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
        blockData.add(".data");
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
//        SemanticAnalysis.print_table();
        String nameVar = var.getToken();
        if (table.containsKey(nameVar)) {
            return  table.get(nameVar).get(0).getType();
        } else {
            for (Map.Entry<String, List<ScopeVar>> entry : table.entrySet()) {
                if(nameVar.startsWith(entry.getKey() + "_")) {
//                    System.out.println(var.getToken());
//                    System.out.println(table.get(entry.getKey()).get(0).getType());
                    return table.get(entry.getKey()).get(0).getType();
                }
            }
        }
        return "int";
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
        // System.out.println();
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

    public static void print_asm () {
        for (String s : get_assembler_code()) {
            System.out.println(s);
        }
    }
}
