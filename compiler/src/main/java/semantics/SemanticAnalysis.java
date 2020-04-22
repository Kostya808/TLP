package semantics;

import AST.AST;
import supporting_structures.ScopeVar;

import java.util.*;

public class SemanticAnalysis {
    private static HashMap<String, List<ScopeVar>> table = new HashMap<>();

    // TODO: 22.04.2020 int b, x; 
    public static void analysis(List<AST> listNodes, String scope, int level) {
        int scopeCount = 1;
        for(AST node: listNodes) {
            switch (node.getTypeToken()){
                case ("Block class"):
                    if (!node.getChildren().isEmpty()) {
                        analysis(node.getChildren().get(1).getChildren(), scope + " " + level, level);
                    }
                    break;
                case ("Function declaration"):
                case ("Declaration for"):
                case ("Brace block"):
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
                case ("Creat and assign"):
                case ("Var creat fin"):
                case ("Var creat"):
                    AST varNode = name_definition(node.getChildren());
                    String typeVar = node_type_definition(node.getChildren());
                    assert varNode != null;
                    String nameVar = varNode.getToken();
                    ScopeVar var = new ScopeVar(varNode, scope, typeVar);
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
                    analysis(node.getChildren(), scope, level);
                    break;
                case ("Assign operation"):
                    boolean noError = check_assign_op(node.getChildren(), scope);
                    if(!noError) {
                        System.out.println("\nDifferent types of data " + get_info_node(node));
                    }
                    break;
            }
        }
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
            System.out.println("\nDifferent types of data " + get_info_node(listNodes.get(0))
                    + " and " + get_info_node(listNodes.get(2)));
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
        }
        return "";
    }

    public static String scope_contains_var(String scope, AST var) {
        if(var.getTypeToken().equals("Array element")) {
            if (!check_item_index(var.getChildren(), scope)) {
                System.out.println("Array index can only be an integer");
            }
            return scope_contains_var(scope, var.getChildren().get(0));
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
        System.out.println("\nVariable " + get_info_node(var) + " not declared!");
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
        }
        return null;
    }

    public static String node_type_definition(List<AST> list) {
        return list.get(0).getToken();
    }

    public static char getLetter(int number) {
        return (char) (((number - 1) % 26)+'a');
    }

    public static void print_table() {
        for (Map.Entry<String, List<ScopeVar>> entry : table.entrySet()) {
            System.out.print("\n" + entry.getKey() + ":\n");
            for (ScopeVar s : entry.getValue())
                System.out.println(s.getType() + " " + s.getScope() + " ");
        }
    }

    public static String get_info_node(AST node) {
        return "' " + node.getToken() + " ' <" + node.getRow() + " : " + node.getCol() + ">";
    }
}
