package semantics;

import AST.AST;
import supporting_structures.ScopeVar;

import java.util.*;

public class SemanticAnalysis {
    private static HashMap<String, List<ScopeVar>> table = new HashMap<>();

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
//                    System.out.println(nameVar + " " + scope);
                    break;
                case ("Assign operation"):
                    check_assign_op(node.getChildren(), scope);
                    break;
            }
        }
    }

    public static void check_assign_op(List<AST> listNodes, String scope) {
        if(listNodes.get(2).getTypeToken().equals("Arithmetic expression")) {
            check_arithmetic_expression(listNodes.get(2).getChildren(), scope);
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
                    case "Id":
                        return type_comparison(typeVar1, data_type_def(scope_contains_var(scope, node2)), node1, node2, scope);
                }
                break;
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
                    case "Id":
                        return type_comparison(typeVar1, data_type_def(scope_contains_var(scope, node2)), node1, node2, scope);
                }
                break;
            case ("StringLiteral"):
                if(typeVar2.equals("StringLiteral")) {
                    return "string";
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
        if(!node.getTypeToken().equals("Id")) {
            if (type.equals("NotAnInteger")) {
                if (node.getChildren().isEmpty()) {
                    node.setTypeToken(type);
                    node.setToken(node.getToken() + ".0");
                } else {
                    type_conversion(type, node.getChildren().get(0));
                    type_conversion(type, node.getChildren().get(2));
                }
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

    public static HashMap<String, List<ScopeVar>> getTable() {
        return table;
    }
}