package code_generation;

import AST.AST;
import semantics.SemanticAnalysis;
import supporting_structures.ScopeVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CodeGen {
    private static List <String> blockData = new ArrayList<>();
    private static List <String> blockText = new ArrayList<>();
    private static List <String> blockBss = new ArrayList<>();

    private static HashMap<String, Integer> createdVar = new HashMap<>();

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
                case ("Creat several fin"):
                    var_creat_fin_processing(node.getChildren());
                    break;
            }
        }
    }
    public static void var_creat_fin_processing (List<AST> listNodes) {
        String type = listNodes.get(0).getToken();
        switch (type){
            //fffffffffffffffffff
            case ("int"):
                List <String> generatedCode = asm_variable_declaration_generation("int", "", listNodes.get(1));
                blockBss.addAll(generatedCode);
                break;
        }
    }
    public static void function_call_processing (List<AST> listNodes) {
        String nameFunction = listNodes.get(0).getToken();
        switch (nameFunction){
            case ("Console.Write"):
            case ("Console.WriteLine"):
                AST argument = listNodes.get(1).getChildren().get(1);
                switch (argument.getTypeToken()) {
                    case("StringLiteral"):
                        List <String> generatedCode = asm_variable_declaration_generation("StringLiteral", "", argument);
                        blockData.addAll(generatedCode);
                        blockText.add("\t\tpushl\t$" + lastCreatedVariable);
                        blockText.add("\t\tcall\tprintf");
                        blockText.add("\t\taddl\t$4, %esp\n");
                        break;
                    case ("Id"):
                        break;
                }
                break;
        }
    }

    public static List<String> asm_variable_declaration_generation(String type, String value, AST node) {
        List<String> generatedСodeDecl = new ArrayList<>();
        String declaredVar = "";
        switch (type) {
            case ("StringLiteral"):
                switch (node.getTypeToken()){
                    case ("StringLiteral"):
                        declaredVar = get_name_declared_var("str");
                        lastCreatedVariable = declaredVar;
                        generatedСodeDecl.add("\n" + declaredVar + ":");
                        generatedСodeDecl.add("\t\t.string " + node.getToken());
                        return generatedСodeDecl;
                                    }
                break;
            case ("int"):
                if("".equals(value)) {
                    declaredVar = get_name_declared_var(node.getToken());
                    lastCreatedVariable = declaredVar;
                    generatedСodeDecl.add("\n" + declaredVar + ":");
                    generatedСodeDecl.add("\t\t.space 4");
                    return generatedСodeDecl;
                } else {

                }
                break;
        }
        return new ArrayList<>();
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

        blockData.add("");
        blockBss.add("");
        blockText.add("");

        finalList.addAll(blockData);
        finalList.addAll(blockBss);
        finalList.addAll(blockText);
        return finalList;
    }

    public CodeGen() {
        blockData.add(".data");
        blockBss.add(".bss");
        blockText.add(".text");
    }
}
