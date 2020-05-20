package parser;

import AST.AST;
import supporting_structures.StorageBrace;
import supporting_structures.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Parser {
    private static String[]arithmeticOperations = new String[] {"OperatorAddition", "OperatorSubtraction", "OperatorMultiplication",
                                            "OperatorDivision", "OperatorMod"};
    private static String[]assignmentOperations = new String[] {"OperatorAssignment", "OperatorAddAssign","OperatorSubAssign",
                                            "OperatorMultAssign", "OperatorDivAssign"};
    private static String[]accessModifiers = new String[] {"KeyWordPublic", "KeyWordPrivate"};
    private static String[]dataType = new String[] {"Void", "Int", "String", "Double"};
    private static String[]variable = new String[] {"Id", "DecimalInteger", "Arithmetic expression", "StringLiteral", "Array element",
                                            "Logical expression", "NotAnInteger"};
    private static String[]numbers = new String[] {"DecimalInteger", "StringLiteral", "NotAnInteger"};
    private static String[]passedArguments = new String[] {"DecimalInteger", "StringLiteral", "NotAnInteger", "Id", "Enum numb", "Enum var"};
    private static String[]Brace = new String[] {"LBrace", "LParen", "LSquareBracket", "RBrace", "RParen", "RSquareBracket"};
    private static String[]crement = new String[] {"OperatorIncrement", "OperatorDecrement"};
    private static String[]logicalOperation = new String[]{"OperatorMore", "OperatorSmaller", "OperatorLessOrEqual", "OperatorMoreOrEqual",
                                            "OperatorEq", "LogicalAnd", "LogicalOr", "OperatorNotEq"};
    private static String[]design = new String[] {"KeywordFor", "KeywordElse", "KeywordIf", "IdClassCall", "Id"};
    private static String[]declaration = new String[] {"Class declaration", "Function declaration", "Declaration for",
                                            "Declaration if", "Declaration else if", "Declaration else"};
    private static String[]unfinished = new String[] {"Call func", "Logical expression", "Crement", "Var creat", "Creat several var"};
    private static String[]finished = new String[] {"Crement fin", "Call func fin", "Block function", "Creat and assign", "Var creat fin",
                                            "Block for", "Block if", "Block else", "Block else if", "Block if else", "Assign operation", "Memory assign",
                                            "Assign operation", "Creat several var fin", "Assign call func"};

    private static List<StorageBrace> storBr = new ArrayList<>();
    private static List<String> errorsParser = new ArrayList<>();

    public static void node_list_analysis(List<AST> listNodes) {
        String newNameToken;
        for(int i = 0; i < listNodes.size(); i++) {
            if(Arrays.asList(Brace).contains(listNodes.get(i).getTypeToken())) {
                brace_processing(i, listNodes.get(i), listNodes);
            }
            if(i + 1 < listNodes.size()) {
                if (Arrays.asList(numbers).contains(listNodes.get(i).getTypeToken())) {
                    if (listNodes.get(i + 1).getTypeToken().equals("Comma")) {
                            node_fusion(listNodes, i, i + 1, "Enum numb");
                    }
                } else if (listNodes.get(i).getTypeToken().equals("Block if")) {
                    if (listNodes.get(i + 1).getTypeToken().equals("Block else")) {
                        node_fusion(listNodes, i, i + 1, "Block if else");
                    }
                }
                else if (listNodes.get(i).getTypeToken().equals("Id")) {
                    if (Arrays.asList(crement).contains(listNodes.get(i + 1).getTypeToken())) {
                        node_fusion(listNodes, i, i + 1, "Crement");
                    }
                    else if (listNodes.get(i + 1).getTypeToken().equals("Sq brack block")) {
                        node_fusion(listNodes, i, i + 1, "Array element");
                    }
                    else if (listNodes.get(i + 1).getTypeToken().equals("Comma")) {
                        if(i + 2 < listNodes.size())
                            node_fusion(listNodes, i, i + 1, "Enum var");
                    }
                    else if (listNodes.get(i + 1).getTypeToken().equals("Brace block call function") || listNodes.get(i + 1).getTypeToken().equals("Brace block")) {
                        node_fusion(listNodes, i, i + 1, "Call func");
                    }
                }
                else if (Arrays.asList(design).contains(listNodes.get(i).getTypeToken())) {
                    newNameToken = check_design(i, listNodes);
                    if(!newNameToken.equals("")) {
                        if(newNameToken.equals("Declaration else if"))
                            node_fusion(listNodes, i, i + 2, newNameToken);
                        else if(newNameToken.equals("Declaration else"))
                            node_fusion(listNodes, i, i, newNameToken);
                        else
                            node_fusion(listNodes, i, i + 1, newNameToken);
                    }
                }
                else if (Arrays.asList(declaration).contains(listNodes.get(i).getTypeToken())) {
                    newNameToken = check_block(i, listNodes);
                    if(!newNameToken.equals("")) {
                        node_fusion(listNodes, i, i + 1, newNameToken);
                    }
                }
                else if(Arrays.asList(dataType).contains(listNodes.get(i).getTypeToken())) {
                    newNameToken = check_variable_creation(i + 1, listNodes);
                    if (!newNameToken.equals("")) {
                        if(newNameToken.equals("Var creat fin"))
                            node_fusion(listNodes, i, i + 2, newNameToken);
                        else if(newNameToken.equals("Var creat"))
                            node_fusion(listNodes, i, i + 2, newNameToken);
                        else
                            node_fusion(listNodes, i, i + 1, newNameToken);
                    }
                }
                else if(listNodes.get(i).getTypeToken().equals("Creat several var") || listNodes.get(i).getTypeToken().equals("Var creat with comma")
                          || listNodes.get(i).getTypeToken().equals("Creat and assign with comma")) {
                    if(listNodes.get(i + 1).getTypeToken().equals("Enum var") || listNodes.get(i + 1).getTypeToken().equals("Assign operation with comma")) {
                        add_to_node(listNodes, i, i + 1, "Creat several var");
                    }
                    else if(listNodes.get(i + 1).getTypeToken().equals("Assign operation")) {
                        add_to_node(listNodes, i, i + 1, "Creat several var fin");
                    }
                    else if(i + 2 < listNodes.size()) {
                        if(listNodes.get(i + 1).getTypeToken().equals("Id") && listNodes.get(i + 2).getTypeToken().equals("Semicolon")) {
                            add_to_node(listNodes, i, i + 2, "Creat several var fin");
                        }
                    }
                    continue;
                }
                else if(Arrays.asList(unfinished).contains(listNodes.get(i).getTypeToken())) {
                    newNameToken = check_finished(i + 1, listNodes);
                    if (!newNameToken.equals("")) {
                        node_fusion(listNodes, i, i + 1, newNameToken);
                    }
                }
          }
            if(i + 2 < listNodes.size()) {
                if(Arrays.asList(variable).contains(listNodes.get(i).getTypeToken())) {
                    if(i + 3 < listNodes.size()) {
                        if (!listNodes.get(i + 3).getTypeToken().equals("Sq brack block") &&
                                !listNodes.get(i + 3).getTypeToken().equals("LSquareBracket") &&
                                !listNodes.get(i + 3).getTypeToken().equals("RSquareBracket")) {
                            newNameToken = check_operations(i + 1, listNodes);
                            if (!newNameToken.equals("")) {
                                node_fusion(listNodes, i, i + 2, newNameToken);
                            }
                        }
                    }
                }
                else if(Arrays.asList(accessModifiers).contains(listNodes.get(i).getTypeToken())) {
                    if(check_class_declarations(i + 1, listNodes)) {
                        node_fusion(listNodes, i, i + 2, "Class declaration");
                    }
                }
                else if(listNodes.get(i).getTypeToken().equals("KeyWordNew")) {
                    if(Arrays.asList(dataType).contains(listNodes.get(i + 1).getTypeToken())) {
                        if(listNodes.get(i + 2).getTypeToken().equals("Sq brack block")) {
                            node_fusion(listNodes, i, i + 2, "Mem alloc");
                        }
                    }
                }
            }
            if(i + 3  < listNodes.size()) {
                if(listNodes.get(i).getTypeToken().equals("Id") || listNodes.get(i).getTypeToken().equals("Array element") ||
                        listNodes.get(i).getTypeToken().equals("Var creat")) {
                    newNameToken = check_assignment(i + 1, listNodes);
                    if (!newNameToken.equals("")) {
                        node_fusion(listNodes, i, i + 3, newNameToken);
                    }
                }
                else if(Arrays.asList(accessModifiers).contains(listNodes.get(i).getTypeToken())) {
                    check_function_declaration(i + 1, listNodes);
                }
            }
        }
    }

    public static void brace_processing(int index, AST node, List<AST> listNodes) {
        String searchBracket, newName;
        StorageBrace brace = new StorageBrace(node.getTypeToken(), index);
        boolean accessories = false;
        for(StorageBrace s: storBr) {
            if(s.getBrace().equals(brace.getBrace()) && s.getIndBrace() == brace.getIndBrace()){
                accessories = true;
                break;
            }
        }
        if(!accessories) {
            storBr.add(brace);
            if (storBr.size() > 1) {
                switch (node.getTypeToken()) {
                    case ("RBrace"):
                        searchBracket = "LBrace";
                        break;
                    case ("RParen"):
                        searchBracket = "LParen";
                        break;
                    case ("RSquareBracket"):
                        searchBracket = "LSquareBracket";
                        break;
                    default:
                        searchBracket = "Error";
                }
                for (int i = storBr.size() - 2; i >= 0; i--) {
                    if (storBr.get(i).getBrace().equals(searchBracket)) {
                        newName = check_brace(storBr.get(i).getIndBrace(), index, listNodes);
                        if(!newName.equals(""))
                            node_fusion(listNodes, storBr.get(i).getIndBrace(), index , newName);
                        storBr.remove(storBr.size() - 1);
                        storBr.remove(i);
                        break;
                    }
                }
            }
        }
    }

    public static String check_brace(int startInd, int endInd, List<AST> listNodes) {
        String newName = "";
        switch (listNodes.get(startInd).getTypeToken()) {
            case ("LBrace"):
                for(int i = startInd  + 1; i < endInd; i++) {
                    if(!Arrays.asList(finished).contains(listNodes.get(i).getTypeToken())) {
                        newName = "Parenthesis block with error";
                        break;
                    }
                }
                if(newName.equals(""))
                    newName = "Parenthesis block";
                break;
            case ("LParen"):
                if(listNodes.get(startInd + 1).getTypeToken().equals("Typed id")) {
                    int checkFlag = 0;
                    for (int i = startInd + 1; i < endInd; i++) {
                        if(listNodes.get(i).getTypeToken().equals("Typed id"))
                            checkFlag++;
                        if(listNodes.get(i + 1).getTypeToken().equals("Comma"))
                            checkFlag++;
                    }
                    if(endInd - startInd - 1 == checkFlag)
                        return "Brace block function";
                }
                if(Arrays.asList(passedArguments).contains(listNodes.get(startInd + 1).getTypeToken())) {
                    int checkFlag = 0;
                    String node;
                    for (int i = startInd + 1; i < endInd; i++) {
                        node = listNodes.get(i).getTypeToken();
                        if(Arrays.asList(passedArguments).contains(node))
                            checkFlag++;
                    }
                    if(endInd - startInd - 1 == checkFlag)
                        return "Brace block call function";
                }
                if(endInd - startInd - 1 == 3) {
                    if(listNodes.get(startInd + 1).getTypeToken().equals("Creat and assign") ||
                            listNodes.get(startInd + 1).getTypeToken().equals("Assign operation")) {
                        if (listNodes.get(startInd + 2).getTypeToken().equals("Logical expression fin")) {
                            if (listNodes.get(startInd + 3).getTypeToken().equals("Crement") ||
                                    listNodes.get(startInd + 3).getTypeToken().equals("Assign operation")) {
                                newName = "Brace block for";
                            }
                        }
                    }
                }
                if(endInd - startInd - 1 == 1) {
                    if(listNodes.get(startInd + 1).getTypeToken().equals("Logical expression")){
                        newName = "Brace block with condition";
                        return newName;
                    }
                    if(Arrays.asList(variable).contains(listNodes.get(startInd + 1).getTypeToken()) ||
                            listNodes.get(startInd + 1).getTypeToken().equals("Var creat")) {
                        return "Brace block";
                    }
                }
                if(endInd - startInd - 1 == 0) {
                    newName = "Brace block";
                }
                if(newName.equals(""))
                    newName = "Brace block with error";
                break;
            case ("LSquareBracket"):
                if(endInd - startInd - 1 == 0) {
                    return "Sq brack block";
                }
                if(endInd - startInd - 1 > 1) {
                    return "Sq brack block with error";
                }
                if(!Arrays.asList(variable).contains(listNodes.get(startInd + 1).getTypeToken())) {
                    return "Sq brack block with error";
                }
                newName = "Sq brack block";
                break;
            default:
                newName = "";
        }
        return newName;
    }

    public static String check_finished(int operatorIndex, List<AST> listNodes) {
        if (listNodes.get(operatorIndex).getTypeToken().equals("Semicolon"))
            return listNodes.get(operatorIndex - 1).getTypeToken() + " fin";
        return "";
    }

    public static String check_design(int operatorIndex, List<AST> listNodes) {
        String newName = "";
        if(listNodes.get(operatorIndex).getTypeToken().equals("KeywordFor")){
            if (listNodes.get(operatorIndex + 1).getTypeToken().equals("Brace block for"))
                return "Declaration for";
        }
        if(listNodes.get(operatorIndex).getTypeToken().equals("KeywordIf")){
            if (listNodes.get(operatorIndex + 1).getTypeToken().equals("Brace block with condition"))
                return "Declaration if";
        }
        if(listNodes.get(operatorIndex).getTypeToken().equals("Id") || listNodes.get(operatorIndex).getTypeToken().equals("IdClassCall")){
            if (listNodes.get(operatorIndex + 1).getTypeToken().equals("Brace block call function") ||
                                 listNodes.get(operatorIndex + 1).getTypeToken().equals("Brace block"))
                return "Call func";
        }
        if (listNodes.get(operatorIndex + 1).getTypeToken().equals("Brace block"))
            return newName;
        if(operatorIndex + 2 < listNodes.size()) {
            if (listNodes.get(operatorIndex).getTypeToken().equals("KeywordElse")) {
                newName = "Declaration else";
                if (listNodes.get(operatorIndex + 1).getTypeToken().equals("KeywordIf")) {
                    newName = "Declaration else if";
                    if (listNodes.get(operatorIndex + 2).getTypeToken().equals("Brace block"))
                        return newName;
                }
                else {
                    return newName;
                }
            }
        }
        return "";
    }

    public static String check_block(int operatorIndex, List<AST> listNodes) {
        String newName = "";
        switch (listNodes.get(operatorIndex).getTypeToken()) {
            case ("Class declaration"):
                newName = "Block class";
                break;
            case ("Function declaration"):
                newName = "Block function";
                break;
            case ("Declaration for"):
                if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Assign operation") ||
                        listNodes.get(operatorIndex + 1).getTypeToken().equals("Crement fin"))
                    return "Block for";
                newName = "Block for";
                break;
            case ("Declaration if"):
                if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Assign operation") ||
                        listNodes.get(operatorIndex + 1).getTypeToken().equals("Crement fin"))
                    return "Block if";
                newName = "Block if";
                break;
            case ("Declaration else if"):
                if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Assign operation") ||
                        listNodes.get(operatorIndex + 1).getTypeToken().equals("Crement fin"))
                    return "Block else if";
                newName = "Block else if";
                break;
            case ("Declaration else"):
                if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Assign operation") ||
                        listNodes.get(operatorIndex + 1).getTypeToken().equals("Crement fin"))
                    return "Block else";
                newName = "Block else";
                break;
        }
        if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Parenthesis block"))
            return newName;
        return "";
    }

    public static void check_function_declaration(int operatorIndex, List<AST> listNodes) {
        int checkFlag = 0;
        int flagStatic = 0;
        if(listNodes.get(operatorIndex).getTypeToken().equals("ModifierStatic"))
            flagStatic = 1;
        if(listNodes.get(operatorIndex + flagStatic).getTypeToken().equals("Typed id"))
            checkFlag++;
        if(listNodes.get(operatorIndex + 1 + flagStatic).getTypeToken().equals("Brace block function") ||
                listNodes.get(operatorIndex + 1 + flagStatic).getTypeToken().equals("Brace block"))
            checkFlag++;
        if(checkFlag == 2)
            node_fusion(listNodes, operatorIndex - 1, operatorIndex - 1 + checkFlag + flagStatic,
                    "Function declaration");
    }

    public static boolean check_class_declarations(int operatorIndex, List<AST> listNodes) {
        int checkFlag = 0;
        if(listNodes.get(operatorIndex).getTypeToken().equals("KeywordClass"))
            checkFlag++;
        if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Id"))
            checkFlag++;
        return checkFlag == 2;
    }

    public static String check_assignment(int operatorIndex, List<AST> listNodes) {
        String newName = "";
        if(Arrays.asList(assignmentOperations).contains(listNodes.get(operatorIndex).getTypeToken())) {
            if(Arrays.asList(variable).contains(listNodes.get(operatorIndex + 1).getTypeToken())) {
                newName = "Assign operation";
            }
            else if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Call func"))
                    newName = "Assign call func";
            else if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Mem alloc"))
                newName = "Memory assign";
            else {
                return "";
            }
            if (listNodes.get(operatorIndex + 2).getTypeToken().equals("Semicolon"))
                return newName;
            else if(listNodes.get(operatorIndex + 2).getTypeToken().equals("Comma"))
                return newName + " with comma";

            else
                return "";
        }
        return newName;
    }

    public static String check_variable_creation(int operatorIndex, List<AST> listNodes) {
       if(operatorIndex + 1 < listNodes.size()) {
            if (listNodes.get(operatorIndex).getTypeToken().equals("Id")) {
                if (listNodes.get(operatorIndex + 1).getTypeToken().equals("Semicolon")) {
                    return "Var creat fin";
                }
                else if(Arrays.asList(assignmentOperations).contains(listNodes.get(operatorIndex + 1).getTypeToken())){
                    return "";
                }
                else  if(listNodes.get(operatorIndex + 1).getTypeToken().equals("Comma")) {
                    if (operatorIndex + 2 < listNodes.size()) {
                        if (Arrays.asList(dataType).contains(listNodes.get(operatorIndex + 2).getTypeToken()))
                            return "Typed id";
                    }
                    return "";
                }
                else
                    return "Typed id";
            }
            if (listNodes.get(operatorIndex).getTypeToken().equals("Sq brack block")) {
                if (listNodes.get(operatorIndex + 1).getTypeToken().equals("Id")) {
                    return "Var creat";
                }
            }
        }
        if (listNodes.get(operatorIndex).getTypeToken().equals("Enum var"))
            return "Var creat with comma";
        if (listNodes.get(operatorIndex).getTypeToken().equals("Assign operation with comma"))
            return "Creat and assign with comma";
        if( (listNodes.get(operatorIndex).getTypeToken().equals("Assign operation"))
                || ( listNodes.get(operatorIndex).getTypeToken().equals("Assign call func")) )
            return "Creat and assign";
        return "";
    }

    public static String check_operations(int operatorIndex, List<AST> listNodes) {
        String newName = "";
        if(Arrays.asList(arithmeticOperations).contains(listNodes.get(operatorIndex).getTypeToken()))
            newName = "Arithmetic expression";
        if(Arrays.asList(logicalOperation).contains(listNodes.get(operatorIndex).getTypeToken()))
            newName = "Logical expression";
        if(Arrays.asList(variable).contains(listNodes.get(operatorIndex + 1).getTypeToken()) ||
                listNodes.get(operatorIndex + 1).getTypeToken().equals("IdClassCall"))
            if(!newName.equals(""))
                return newName;
        return "";
    }

    public  static  void node_fusion(List<AST> listNodes, int startingIndex, int endIndex, String tokenTypeNewName) {
        int i;
        List<AST> children = new LinkedList<AST>();
        for(i = startingIndex; i <= endIndex; i++) {
            AST node = new AST(listNodes.get(i));
            children.add(node);
            if(i != startingIndex) {
                listNodes.remove(i);
                endIndex--;
                i--;
            }
        }
        listNodes.get(startingIndex).setToken(tokenTypeNewName);
        listNodes.get(startingIndex).setTypeToken(tokenTypeNewName);
        listNodes.get(startingIndex).setParent(null);
        listNodes.get(startingIndex).setChildren(children);

        node_list_analysis(listNodes);
    }

    public static void add_to_the_list_of_nodes(List<AST> listNodes, Token token) {
        AST nodeAST = new AST(token.getToken(), token.getTypeToken(), token.getStr(), token.getCol(), null, new ArrayList<>());
        listNodes.add(nodeAST);
    }

    public static void add_to_node(List<AST> listNodes, int startingIndex, int endIndex, String tokenTypeNewName) {
        int i;
        for(i = startingIndex + 1; i <= endIndex; i++) {
            AST node = new AST(listNodes.get(i));
            listNodes.get(startingIndex).add_children(node);
            listNodes.remove(i);
            endIndex--;
            i--;
        }
        listNodes.get(startingIndex).setTypeToken(tokenTypeNewName);
        listNodes.get(startingIndex).setToken(tokenTypeNewName);
        node_list_analysis(listNodes);
    }

    public static void check_error(List<AST> list) {
        for(int i = 0; i < list.size(); i++) {
            if(Arrays.asList(unfinished).contains(list.get(i).getTypeToken())) {
                add_error(list.get(i), "After expected ';'", 1);
                continue;
            }
            else if(Arrays.asList(Brace).contains(list.get(i).getTypeToken())) {
                add_error(list.get(i), "Unpaired", 1);
                continue;
            }
            else if(list.get(i).getTypeToken().equals("Sq brack block with error")) {
                add_error(list.get(i), "Expected variable or numb", 0);
                continue;
            }
            else if(list.get(i).getTypeToken().equals("Brace block with error")) {
                if(list.get(i - 1).getTypeToken().equals("KeywordIf")) {
                    add_error(list.get(i), "Inside brackets expected logical expression", 0);
                }
                else if(list.get(i - 1).getTypeToken().equals("KeywordFor")) {
                    add_error(list.get(i), "Inside brackets expected '(Var assign; Logic expression; Crement)'", 0);
                }
                continue;
            }
            else if(list.get(i).getTypeToken().equals("Parenthesis block with error")) {
                List<AST> bufList = new ArrayList<AST>();
                for (int j = 1; j < list.get(i).getChildren().size() - 1; j++) {
                    bufList.add(list.get(i).getChildren().get(j));
                }
                check_error(bufList);
                continue;
            }
            else if(Arrays.asList(dataType).contains(list.get(i).getTypeToken())) { //int
                if (get_node(list, i + 1).getTypeToken().equals("Id")) { // int a
                    if (!Arrays.asList(assignmentOperations).contains(get_node(list,i + 2).getTypeToken())) { //int a =
                        i += 1;
                        add_error(list.get(i), "After expected ';', '=', '+=', etc", 1);
                    }
                }
                else
                    add_error(list.get(i), "After expected name", 1);
                continue;
            }
            else if(list.get(i).getTypeToken().equals("Id")) { //a
                if (Arrays.asList(assignmentOperations).contains(get_node(list,i + 1).getTypeToken())) { //a =
                    i += 1;
                    if (Arrays.asList(variable).contains(get_node(list,i + 1).getTypeToken())) { //int a = 2
                        if (!Arrays.asList(arithmeticOperations).contains(get_node(list, i + 2).getTypeToken())) {
                            i += 1;
                            add_error(list.get(i), "After expected ';'", 1);
                        }
                    }
                    else
                        add_error(list.get(i), "After expected name or numb", 1);
                }
                else if(Arrays.asList(arithmeticOperations).contains(get_node(list,i + 1).getTypeToken())) {
                    i += 1;
                    if (Arrays.asList(variable).contains(get_node(list,i + 1).getTypeToken())) { //a + 2
                        if (!Arrays.asList(arithmeticOperations).contains(get_node(list, i + 2).getTypeToken())
                        && get_node(list, i + 2).getTypeToken().equals("Semicolon")) {
                            i += 1;
                            add_error(list.get(i + 1), "After expected ';'", 1);
                        }
                    }
                    else
                        add_error(list.get(i), "After expected name or numb", 1);
                }
                else
                    add_error(list.get(i), "After expected ';', '=', '+=', etc", 1);
                continue;
            }
            else if(Arrays.asList(variable).contains(list.get(i).getTypeToken())) { // 2
                if(Arrays.asList(arithmeticOperations).contains(get_node(list,i + 1).getTypeToken())) {
                    i += 1;
                    if (Arrays.asList(variable).contains(get_node(list,i + 1).getTypeToken())) { //int a + 2
                        if (!Arrays.asList(arithmeticOperations).contains(get_node(list, i + 1).getTypeToken())
                                && get_node(list, i + 1).getTypeToken().equals("Semicolon"))
                            add_error(list.get(i + 1), "After expected ';'", 1);
                    }
                    else
                        add_error(list.get(i), "After expected name or numb", 1);
                }
                else
                    add_error(list.get(i), "After expected ';', '+', '-', etc", 1);
                continue;
            }
            else if(Arrays.asList(declaration).contains(list.get(i).getTypeToken())) {
                add_error(list.get(i), "After expected body or body with error", 1);
                continue;
            }
            else if(Arrays.asList(design).contains(list.get(i).getTypeToken())) {
                add_error(list.get(i), "After expected block '(...)' or block '(...)' with error", 1);
            continue;
            }
            else if(!Arrays.asList(finished).contains(list.get(i).getTypeToken()) && !list.get(i).getTypeToken().equals("Block class"))
            add_error(list.get(i), "Not part of the design", -1);
        }
    }

    public static void add_error(AST node, String error, int rightRec) {
        AST procNode;
        if(rightRec == -1) {
            String errorToken = "PARSER: <" + node.getRow() + " : " + node.getCol() + " '" + node.getToken() + "'> ";
            errorsParser.add(errorToken + error);
            return;
        }
        if(!node.getChildren().isEmpty()) {
            if (rightRec == 1) {
                procNode = node.getChildren().get(node.getChildren().size() - 1);
                if (!procNode.getChildren().isEmpty()) {
                    add_error(procNode.getChildren().get(procNode.getChildren().size() - 1), error, rightRec);
                } else {
                    String errorToken = "PARSER: <" + procNode.getRow() + " : " + procNode.getCol() + " '" + procNode.getToken() + "'> ";
                    errorsParser.add(errorToken + error);
                }
            } else {
                procNode = node.getChildren().get(0);
                if (!procNode.getChildren().isEmpty()) {
                    add_error(procNode.getChildren().get(0), error, rightRec);
                } else {
                    String errorToken = "PARSER: <" + procNode.getRow() + " : " + procNode.getCol() + " '" + procNode.getToken() + "'> ";
                    errorsParser.add(errorToken + error);
                }
            }
        }
        else {
            String errorToken = "PARSER: <" + node.getRow() + " : " + node.getCol() + " '" + node.getToken() + "'> ";
            errorsParser.add(errorToken + error);
        }
    }

    public static void print_error() {
        if (!errorsParser.isEmpty()) {
            for (String error : errorsParser) {
                System.out.println(error);
            }
        }
        System.out.print("\n");
    }

    public static List<String> getErrors() {
        return errorsParser;
    }

    public static AST get_node(List<AST> list, int index) {
        if(index < list.size() && index >= 0) {
            return list.get(index);
        }
        else
            return new AST("", "", 0, 0, null, null);
    }
    public static String[] getDataType() {
        return dataType;
    }
}



