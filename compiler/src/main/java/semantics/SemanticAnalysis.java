package semantics;

import AST.AST;
import supporting_structures.DeclaredVar;
import supporting_structures.ScopeVar;

import java.util.*;

public class SemanticAnalysis {
    private static HashMap<String, List<ScopeVar>> table = new HashMap<>();
    private static List<String> errorsSem = new ArrayList<>();
    private static HashMap<String, List<DeclaredVar>> declaredFunc = new HashMap<>();
    private static List<AST> rawNodes = new ArrayList<AST>();
    private static List<String> scopeRawNodes = new ArrayList<String>();

    public static void analysis(List<AST> listNodes, String scope, int level) {
        int scopeCount = 1;
        String nameVar;
        for(AST node: listNodes) {
            switch (node.getTypeToken()){
                case ("Block class"):
                    if (!node.getChildren().isEmpty()) {
                        analysis(node.getChildren().get(1).getChildren(), scope + " " + level, level);
                    }
                    break;
                case ("Function declaration"):
                    function_declaration_processing(node.getChildren());
                    if (!node.getChildren().isEmpty()) {
                        analysis(node.getChildren(), scope, level);
                    }
                    break;
                case ("Declaration for"):
                case ("Brace block"):
                case ("Brace block function"):
                case ("Brace block for"):
                case ("Parenthesis block"):
                case ("Memory assign"):
                    if (!node.getChildren().isEmpty()) {
                        analysis(node.getChildren(), scope, level);
                    }
                    break;
                case ("Block function"):
                case ("Block for"):
                case ("Block if"):
                case ("Block else if"):
                case ("Block else"):
                    if (!node.getChildren().isEmpty()) {
                        int buf = level + 1;
                        analysis(node.getChildren(), scope + " -> " + buf + getLetter(scopeCount), buf);
                        scopeCount++;
                    }
                    break;
                case ("Creat several var fin"):
                    String typeVariable = node_type_definition(node.getChildren());
                    for(AST childNode : node.getChildren()) {
                        switch (childNode.getTypeToken()) {
                            case ("Id"):
                                nameVar = childNode.getToken();
                                break;
                            case ("Enum var"):
                            case ("Assign operation"):
                            case ("Assign operation with comma"):
                                nameVar = childNode.getChildren().get(0).getToken();
                                break;
                            default:
                                nameVar = "";
                                break;
                        }
                        if(!nameVar.equals("")) {
                            ScopeVar var = new ScopeVar(childNode, scope, typeVariable);
                            add_to_table(nameVar, var);
                        }
                    }
                    analysis(node.getChildren(), scope, level);
                    break;
                case ("Typed id"):
                case ("Creat and assign"):
                case ("Var creat fin"):
                case ("Var creat"):
                    AST varNode = name_definition(node.getChildren());
                    String typeVar = node_type_definition(node.getChildren());
                    assert varNode != null;
                    nameVar = varNode.getToken();
                    ScopeVar var = new ScopeVar(varNode, scope, typeVar);
                    add_to_table(nameVar, var);
                    analysis(node.getChildren(), scope, level);
                    break;
                case ("Assign operation with comma"):
                case ("Assign operation"):
                    boolean noError = check_assign_op(node.getChildren(), scope);
                    if(!noError) {
                        add_error(node, "Different types of data", null);
                    }
                    break;
                case ("Call func fin"):
                    rawNodes.add(node);
                    scopeRawNodes.add(scope);
                    break;
            }
        }
        if(!rawNodes.isEmpty() && scope.equals("Level")) {
                for(int i = 0; i < rawNodes.size(); i++) {
                switch (rawNodes.get(i).getTypeToken()) {
                    case ("Call func fin"):
                        function_call_processing(rawNodes.get(i).getChildren().get(0).getChildren(), scopeRawNodes.get(i));
                        break;
                }
            }
        }
    }

    public static void function_call_processing(List<AST> list, String scope) {
        String funcNameCall = list.get(0).getToken(), dataTypePassedVar = "";
        if(declaredFunc.containsKey(funcNameCall)) {
            List <DeclaredVar> listDeclaredVar = declaredFunc.get(funcNameCall);
            List<AST> listPassedVar = list.get(1).getChildren();

            if(listPassedVar.size() - 2 != listDeclaredVar.size()) {
                add_error(list.get(1), "The number of function arguments passed and received is different", null);
                return;
            }
            int countElemDeclaredVar = 0;
            for(AST passedVar : listPassedVar) {
                if("Enum numb".equals(passedVar.getTypeToken()) || "Enum var".equals(passedVar.getTypeToken()))
                    passedVar = passedVar.getChildren().get(0);
                switch (passedVar.getTypeToken()) {
                    case ("LParen"):
                    case ("RParen"):
                        continue;
                    case ("Id"):
                        dataTypePassedVar = scope_contains_var(scope, passedVar);
                        break;
                    case "StringLiteral":
                    case "DecimalInteger":
                    case "NotAnInteger":
                        dataTypePassedVar = data_type_def(passedVar.getTypeToken());
                        break;
                }
                if(!dataTypePassedVar.equals("")) {
                    type_check_passed_var(passedVar, dataTypePassedVar, listDeclaredVar.get(countElemDeclaredVar).getType());
                }
                countElemDeclaredVar++;
            }
        } else {
            if(funcNameCall.equals("Console.WriteLine") || funcNameCall.equals("Console.Write")) {
                List<AST> listPassedVar = list.get(1).getChildren();
                if(listPassedVar.size() - 2 > 1) {
                    add_error(list.get(1), "Function 'Console.WriteLine' takes a string as input", null);
                    return;
                } else if (listPassedVar.size() - 2 == 1) {
                    String typePassedVar = listPassedVar.get(1).getTypeToken();
                    if ("Id".equals(typePassedVar)) {
                        scope_contains_var(scope, listPassedVar.get(1));
                    } else if("Arithmetic expression".equals(typePassedVar)) {
                        add_error(listPassedVar.get(1), "Function 'Console.WriteLine' takes a string as input", null);
                    }
                }
            }
            else if(funcNameCall.equals("Console.ReadLine")) {
                if(!list.get(1).getTypeToken().equals("Brace block"))
                    add_error(list.get(1), "After 'Console.ReadLine' expected '( )'", null);
            }
            else
                add_error(list.get(0), "No function declared", null);
        }
    }

    public static boolean type_check_passed_var(AST node, String typePassed, String typeTake) {
        switch (typePassed) {
            case ("double"):
                switch (typeTake) {
                    case ("int"):
                        type_conversion("DecimalInteger", node);
                        return true;
                    case ("double"):
                        return true;
                    case ("string"):
                        add_error(node, "Sent and received data types are different", null);
                        return false;
                }
                break;
            case ("int"):
                switch (typeTake) {
                    case ("int"):
                        return true;
                    case ("double"):
                        type_conversion("NotAnInteger", node);
                        return true;
                    case ("string"):
                        add_error(node, "Sent and received data types are different", null);
                        return false;
                }
                break;
            case ("string"):
                switch (typeTake) {
                    case ("int"):
                    case ("double"):
                        add_error(node, "Sent and received data types are different", null);
                        return false;
                    case ("string"):
                        return true;
                }
                break;
        }
        return false;
    }

    public static boolean check_assign_op(List<AST> listNodes, String scope) {
        String typeVar = scope_contains_var(scope, listNodes.get(0));
        switch (typeVar) {
            case ("double"):
                switch (listNodes.get(2).getTypeToken()) {
                    case ("Arithmetic expression"):
                        listNodes.get(2).setTypeToken(check_arithmetic_expression(listNodes.get(2).getChildren(), scope));
                        return check_assign_op(listNodes, scope);
                    case ("Array element"):
                    case ("Id"):
                        String dataType = scope_contains_var(scope, listNodes.get(2));
                        if (dataType.equals("int"))
                            type_conversion("NotAnInteger", listNodes.get(2));
                        return !dataType.equals("string") && !dataType.equals("");
                    case "StringLiteral":
                        return false;
                    case "DecimalInteger":
                        type_conversion("NotAnInteger", listNodes.get(2));
                        return true;
                    case "NotAnInteger":
                        return true;
                }
                break;
            case ("int"):
                switch (listNodes.get(2).getTypeToken()) {
                    case ("Arithmetic expression"):
                        listNodes.get(2).setTypeToken(check_arithmetic_expression(listNodes.get(2).getChildren(), scope));
                        return check_assign_op(listNodes, scope);
                    case ("Array element"):
                    case ("Id"):
                        String dataType = scope_contains_var(scope, listNodes.get(2));
                        if (dataType.equals("double"))
                            type_conversion("DecimalInteger", listNodes.get(2));
                        return !dataType.equals("string") && !dataType.equals("");
                    case ("StringLiteral"):
                        return false;
                    case ("DecimalInteger"):
                        return true;
                    case ("NotAnInteger"):
                        type_conversion("DecimalInteger", listNodes.get(2));
                        return true;
                }
                break;
            case ("string"):
                switch (listNodes.get(2).getTypeToken()) {
                    case ("Arithmetic expression"):
                        listNodes.get(2).setTypeToken(check_arithmetic_expression(listNodes.get(2).getChildren(), scope));
                        return check_assign_op(listNodes, scope);
                    case ("Array element"):
                    case ("Id"):
                        return scope_contains_var(scope, listNodes.get(2)).equals("string");
                    case "StringLiteral":
                        return true;
                    case "DecimalInteger":
                    case "NotAnInteger":
                        return false;
                }
                break;
        }
        return false;
    }

    public static boolean check_item_index(List<AST> listNodes, String scope) {
        AST nodeIndex = listNodes.get(1).getChildren().get(1);
        String typeItemIndex = nodeIndex.getTypeToken();

        switch (typeItemIndex) {
            case ("DecimalInteger"):
                return true;
            case ("Id"):
                String typeIndex = scope_contains_var(scope, nodeIndex);
                return typeIndex.equals("int");
            default:
                add_error(nodeIndex, "Array index can only be an integer", null);
                return false;
        }
    }

    public static String check_arithmetic_expression(List<AST> listNodes, String scope) {
        String typeVariable, comparResult = "";
        if(listNodes.get(0).getTypeToken().equals("Arithmetic expression")) {
            typeVariable = check_arithmetic_expression(listNodes.get(0).getChildren(), scope);
            if(!typeVariable.equals(""))
                comparResult = type_comparison(typeVariable, listNodes.get(2).getTypeToken(), listNodes.get(0), listNodes.get(2), scope);
        } else {
            comparResult = type_comparison(listNodes.get(0).getTypeToken(), listNodes.get(2).getTypeToken(), listNodes.get(0), listNodes.get(2), scope);
        }

        if(comparResult.equals("Error")) {
            add_error(listNodes.get(0), "Different types of data", listNodes.get(2));
            return "";
        }

        return comparResult;
    }

    public static String type_comparison(String typeVar1, String typeVar2, AST node1, AST node2, String scope) {
        switch (typeVar1) {
            case ("DecimalInteger"):
                switch (typeVar2) {
                    case "StringLiteral":
                        return "Error";
                    case "DecimalInteger":
                        return "DecimalInteger";
                    case "NotAnInteger":
                        type_conversion("NotAnInteger", node1);
                        return "NotAnInteger";
                    case ("Array element"):
                    case "Id":
                        return type_comparison(typeVar1, data_type_def(scope_contains_var(scope, node2)), node1, node2, scope);
                }
                break;
            case ("Array element"):
            case ("Id"):
                return type_comparison(data_type_def(scope_contains_var(scope, node1)), typeVar2, node1, node2, scope);
            case ("NotAnInteger"):
                switch (typeVar2) {
                    case "StringLiteral":
                        return "Error";
                    case "DecimalInteger":
                        type_conversion("NotAnInteger", node2);
                        return "NotAnInteger";
                    case "NotAnInteger":
                        return "NotAnInteger";
                    case ("Array element"):
                    case "Id":
                        return type_comparison(typeVar1, data_type_def(scope_contains_var(scope, node2)), node1, node2, scope);
                }
                break;
            case ("StringLiteral"):
                if(typeVar2.equals("StringLiteral")) {
                    return "StringLiteral";
                } else if(typeVar2.equals("Id") || typeVar2.equals("Array element")) {
                    return type_comparison(typeVar1, data_type_def(scope_contains_var(scope, node2)), node1, node2, scope);
                } else {
                    return "Error";
                }
        }
        return "Error";
    }

    public static String data_type_def(String dataType) {
        switch (dataType) {
            case ("int"):
                return "DecimalInteger";
            case ("double"):
                return "NotAnInteger";
            case ("string"):
                return "StringLiteral";
            case ("DecimalInteger"):
                return "int";
            case ("NotAnInteger"):
                return "double";
            case ("StringLiteral"):
                return "string";
        }
        return "";
    }

    public static String scope_contains_var(String scope, AST var) {
        if(var.getTypeToken().equals("Array element")) {
            check_item_index(var.getChildren(), scope);
            return scope_contains_var(scope, var.getChildren().get(0));
        }
        if(var.getToken().endsWith(".Length")) {
            return "int";
        }
        String nameVar = var.getToken();
        if (table.containsKey(nameVar)) {
            List<ScopeVar> listScopes = table.get(nameVar);
            for (ScopeVar sc : listScopes) {
                if (sc.getScope().equals(scope) || scope.contains(sc.getScope())) {
                    return sc.getType();
                }
            }
        }
        add_error(var, "Variable not declared", null);
        return "";
    }

    public static void type_conversion(String type, AST node) {
        if(node.getTypeToken().equals("Id") ||  node.getTypeToken().equals("Array element")) {
            switch (type) {
                case ("NotAnInteger"):
                    node.setTypeToken(node.getTypeToken() + " to double");
                    break;
                case ("DecimalInteger"):
                    node.setTypeToken(node.getTypeToken() + " to int");
                    break;
            }
        } else {
            switch (type) {
                case ("NotAnInteger"):
                    if (node.getChildren().isEmpty()) {
                        node.setTypeToken(type);
                        node.setToken(node.getToken() + ".0");
                    } else {
                        type_conversion(type, node.getChildren().get(0));
                        type_conversion(type, node.getChildren().get(2));
                    }
                    break;
                case ("DecimalInteger"):
                    if (node.getChildren().isEmpty()) {
                        node.setTypeToken(type);
                        node.setToken(node.getToken().substring(0, node.getToken().indexOf(".")));
                    } else {
                        type_conversion(type, node.getChildren().get(0));
                        type_conversion(type, node.getChildren().get(2));
                    }
                    break;
            }
        }
    }

    public static AST name_definition(List<AST> list) {
        for(AST node: list) {
            if(node.getTypeToken().equals("Id"))
                return node;
            if(node.getTypeToken().equals("Assign operation"))
                return name_definition(node.getChildren());
            if(node.getTypeToken().equals("Assign call func"))
                return name_definition(node.getChildren());
        }
        return null;
    }

    public static String node_type_definition(List<AST> list) {
        return list.get(0).getToken();
    }

    public static char getLetter(int number) {
        return (char) (((number - 1) % 26)+'a');
    }

    public static void add_error(AST node1, String error, AST node2) {
        String errorToken;
        if(node2 == null) {
            errorToken = "SEMANTICS: <" + node1.getRow() + " : " + node1.getCol() + " '" + node1.getToken() + "'> " + error;
        } else {
            errorToken = "SEMANTICS: <" + node1.getRow() + " : " + node1.getCol() + " '" + node1.getToken() + "'> " +
                    error + " <" + node2.getRow() + " : " + node2.getCol() + " '" + node2.getToken() + "'>";
        }
        errorsSem.add(errorToken);
    }

    public static void print_error(List<String> errors) {
        if (!errorsSem.isEmpty()) {
            errors.addAll(errorsSem);
        }
    }

    public static void add_to_table(String nameVar, ScopeVar var) {
        if (table.containsKey(nameVar)) {
            List <ScopeVar> buffer = table.get(nameVar);
            buffer.add(0, var);
            table.put(nameVar, buffer);
        }
        else {
            List <ScopeVar> buffer = new ArrayList<>();
            buffer.add(var);
            table.put(nameVar, buffer);
        }
    }

    public static void print_table() {
        for (Map.Entry<String, List<ScopeVar>> entry : table.entrySet()) {
            System.out.print("\n" + entry.getKey() + ":\n");
            for (ScopeVar s : entry.getValue())
                System.out.println(s.getType() + " " + s.getScope() + " ");
        }
    }

    public static void print_declared_func() {
        for (Map.Entry<String, List<DeclaredVar>> entry : declaredFunc.entrySet()) {
            System.out.print("\n" + entry.getKey() + ":\n");
            for (DeclaredVar var : entry.getValue())
                System.out.println(var.getType() + " " + var.getName() + " ");
        }
    }

    public static HashMap<String, List<DeclaredVar>> getDeclaredFunc() {
        return declaredFunc;
    }

    public static void function_declaration_processing(List<AST> list) {
        String nameFunction = function_name_definition(list);
        DeclaredVar var;
        List<DeclaredVar> listVar = new ArrayList<>();
        for (AST node : list) {
            if(node.getTypeToken().equals("Brace block function") || node.getTypeToken().equals("Brace block")) {
                for (AST nodeInBrace : node.getChildren()) {
                    if(nodeInBrace.getTypeToken().equals("Typed id")) {
                        var = new DeclaredVar(nodeInBrace.getChildren().get(0).getToken(), nodeInBrace.getChildren().get(1).getToken());
                        listVar.add(var);
                    }
                }
                declaredFunc.put(nameFunction, listVar);
                break;
            }
        }
    }

    public static String function_name_definition(final List<AST> list) {
        String name = "";
        for (AST node : list) {
            if(node.getTypeToken().equals("Typed id")) {
                name = node.getChildren().get(1).getToken();
                break;
            }
        }
        return name;
    }
    public static HashMap<String, List<ScopeVar>> getTable() {
        return table;
    }

}
