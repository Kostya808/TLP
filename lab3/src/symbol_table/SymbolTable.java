package symbol_table;

import AST.AST;
import supporting_structures.ScopeVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
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
                    assert varNode != null;
                    String nameVar = varNode.getToken();
                    ScopeVar var = new ScopeVar(varNode, scope);
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

    public static char getLetter(int number) {
        return (char) (((number - 1) % 26)+'a');
    }

    public static HashMap<String, List<ScopeVar>> getTable() {
        return table;
    }

    public static void print_table() {
        for (Map.Entry<String, List<ScopeVar>> entry : table.entrySet()) {
            System.out.print("\n" + entry.getKey() + ":\n");
            for (ScopeVar s : entry.getValue())
                System.out.println(s.getScope() + " ");
        }
    }
}
