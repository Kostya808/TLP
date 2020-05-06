package code_generation;

import AST.AST;
import semantics.SemanticAnalysis;
import supporting_structures.Register;
import supporting_structures.ScopeVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeGen {
    private static List <String> blockData = new ArrayList<>();
    private static List <String> blockText = new ArrayList<>();
    private static List <String> blockBss = new ArrayList<>();

    private static HashMap<String, Integer> createdVar = new HashMap<>();
    private static List<Register> listRegisters = new ArrayList<>();
    private static String lastCreatedVariable = "non";

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
                    break;
                case ("Call func fin"):
                    function_call_processing(node.getChildren().get(0).getChildren());
                    break;
                case ("Var creat fin"):
                case ("Creat and assign"):
                case ("Creat several var fin"):
                    var_creat_processing(node.getChildren());
                    break;
            }
        }
    }
    public static void var_creat_processing (List<AST> listNodes) {
        List <String> generatedCode;
        String type = listNodes.get(0).getToken();
        for(AST variable : listNodes) {
            switch (variable.getTypeToken()) {
                case ("Enum var"):
                    generatedCode = asm_variable_declaration_generation(type, "", variable.getChildren().get(0));
                    blockBss.addAll(generatedCode);
                    break;
                case ("Assign operation with comma"):
                case ("Assign operation"):
                    AST value = variable.getChildren().get(2);
                    if(!"Arithmetic expression".equals(value.getToken())) {
                        generatedCode = asm_variable_declaration_generation(type, value.getToken(), variable.getChildren().get(0));
                        blockData.addAll(generatedCode);
                    }
                    else {
                        List <AST> exprPriority = prioritizing_arithmetic_expressions(value.getChildren());
                        for (AST s : exprPriority) { AST.print_tree(s, 4, 100); }
                        generatedCode = arithmetic_expression_processing(exprPriority, get_free_register_name());
                    }
                    break;
                case ("Id"):
                    generatedCode = asm_variable_declaration_generation(type, "", variable);
                    blockBss.addAll(generatedCode);
                    break;
            }
        }
    }

    public static List<String> arithmetic_expression_processing (List<AST> listNodes, String nameReg) {
        List<String> generatedCode = new ArrayList<>();
        AST leftOperand, rightOperand, operator;
        leftOperand = listNodes.get(0).getChildren().get(0);
        rightOperand = listNodes.get(0).getChildren().get(2);
        operator = listNodes.get(0).getChildren().get(1);

        if(!leftOperand.getChildren().isEmpty()) {

        } else if(!rightOperand.getChildren().isEmpty()) {

        } else {

        }

        return generatedCode;
    }

    public static List<String> arithmetic_operation_processing (AST operand1, AST operator, AST operand2,
                                                                                List<String> generatedCode) {
        List<String> generatedCodeOperation = new ArrayList<>(generatedCode);
        switch (operator.getToken()) {
            case ("+"):
                if(operand1.getTypeToken().equals("Id")) {

                } else {

                }
                break;
            case ("-"):
                break;
            case ("*"):
                break;
            case ("/"):
                break;
            case ("%"):
                break;
        }
        return generatedCode;
    }

    public static void function_call_processing (List<AST> listNodes) {
        List <String> generatedCode;
        String nameFunction = listNodes.get(0).getToken();
        switch (nameFunction){
            case ("Console.Write"):
            case ("Console.WriteLine"):
                AST argument = listNodes.get(1).getChildren().get(1);
                generatedCode = asm_variable_declaration_generation("StringLiteral", "", argument);
                blockData.addAll(generatedCode);
                switch (argument.getTypeToken()) {
                    case("StringLiteral"):
                        blockText.add("\t\tpushl\t$" + lastCreatedVariable);
                        blockText.add("\t\tcall\tprintf");
                        blockText.add("\t\taddl\t$4, %esp\n");
                        break;
                    case ("Id"):
                        blockText.add("\t\tpushl\t" + argument.getToken() + "_1");
                        blockText.add("\t\tpushl\t$" + lastCreatedVariable);
                        blockText.add("\t\tcall\tprintf");
                        blockText.add("\t\taddl\t$8, %esp\n");
                        break;
                }
                break;
        }
    }

    public static List<String> asm_variable_declaration_generation(String type, String value, AST node) {
        List<String> generatedCodeDecl = new ArrayList<>();
        String declaredVar = "";
        switch (type) {

            case ("StringLiteral"):
                declaredVar = get_name_declared_var("str");
                lastCreatedVariable = declaredVar;
                generatedCodeDecl.add("\n" + declaredVar + ":");
                switch (node.getTypeToken()){
                    case ("StringLiteral"):
                        generatedCodeDecl.add("\t\t.string " + node.getToken());
                        return generatedCodeDecl;
                    case ("Id"):
                        generatedCodeDecl.add("\t\t.string " + get_str_for_printf(get_type_var(node)));
                        return generatedCodeDecl;
                }
                break;

            case ("int"):
                declaredVar = get_name_declared_var(node.getToken());
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

    public static String get_str_for_printf(String type) {
        switch (type){
            case ("int"):
                return "\"%d\\n\"";
            case ("double"):
                return "\"%f\\n\"";
        }
        return "";
    }

    public static void function_declaration_processing (List<AST> listNodes) {
        if(SemanticAnalysis.function_name_definition(listNodes).equalsIgnoreCase("main")) {
            blockText.add("\n.globl  main");
            blockText.add(".type  main, @function");
            blockText.add("\nmain:");
        }
    }

    public static String get_name_declared_var(String nameVar) {
        if (createdVar.containsKey(nameVar)) {
            int lastCreated = createdVar.get(nameVar);
            lastCreated++;
            createdVar.put(nameVar, lastCreated);
            return nameVar + "_" + lastCreated;
        }
        else {
            createdVar.put(nameVar, 1);
            return nameVar + "_1";
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
        blockData.add(".data");
        blockBss.add(".bss");
        blockText.add(".text");

        listRegisters.add(new Register("eax", false));
        listRegisters.add(new Register("ebx", false));
        listRegisters.add(new Register("ecx", false));
        listRegisters.add(new Register("edx", false));
        listRegisters.add(new Register("esp", false));
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
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
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
                return reg.getName();
            }
        }
        return "Non";
    }
}
