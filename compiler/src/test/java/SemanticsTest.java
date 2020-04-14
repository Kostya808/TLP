import AST.AST;
import lexer.Lexer;
import org.junit.Assert;
import parser.Parser;
import semantics.SemanticAnalysis;
import supporting_structures.Token;

import java.util.ArrayList;
import java.util.List;

public class SemanticsTest {
    @org.junit.jupiter.api.Test
    void check_arith_expr1() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr;

        strExpr = "1+2";
        String partitionResult = Lexer.space_splitting(strExpr);
        String[] subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "DecimalInteger";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr2() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr;

        strExpr = "1+2+3+4";
        String partitionResult = Lexer.space_splitting(strExpr);
        String[] subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "DecimalInteger";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr3() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr;
        strExpr = "1+2+3+4.0";
        String partitionResult = Lexer.space_splitting(strExpr);
        String[] subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);

//        for (AST s : list) { AST.print_tree(s, 4, 100); }

        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");

//        for (AST s : list) { AST.print_tree(s, 4, 100); }

        String expected = "NotAnInteger";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr4_with_String() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr;

        strExpr = "1+\"2\"";
        String partitionResult = Lexer.space_splitting(strExpr);
        String[] subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr5_with_two_string() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr;

        strExpr = "\"1\"+\"2\"";
        String partitionResult = Lexer.space_splitting(strExpr);
        String[] subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "string";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr6_with_scope() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr = "public static void Main ( ) { int a = 15 ; int b = a ; }";
        String[] subStrSpace = strExpr.split(" ");

        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);
        SemanticAnalysis.analysis(list, "Level", 0);
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
//        SemanticAnalysis.print_table();

        AST node = new AST();
        node.setToken("a");
        String actual = SemanticAnalysis.scope_contains_var("Level -> 1a", node);

        String expected = "int";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr7_with_scope() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr = "public static void Main ( ) { int a = 15 ; int b = a ; }";
        String[] subStrSpace = strExpr.split(" ");

        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }

        Parser.node_list_analysis(list);
        SemanticAnalysis.analysis(list, "Level", 0);
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
//        SemanticAnalysis.print_table();

        AST node = new AST();
        node.setToken("a");

        String actual = SemanticAnalysis.scope_contains_var("Level -> 1a -> 2a", node);
        String expected = "int";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr8_with_scope() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr = "public static void Main ( ) { int a = 15 ; int b = a ; }";
        String[] subStrSpace = strExpr.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }
        Parser.node_list_analysis(list);
        SemanticAnalysis.analysis(list, "Level", 0);
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
//        SemanticAnalysis.print_table();

        List<AST> listExpr = new ArrayList<AST>();
        String expr = "1+a";
        String partitionResult = Lexer.space_splitting(expr);
        subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(listExpr, token);
        }
        String actual = SemanticAnalysis.check_arithmetic_expression(listExpr, "Level -> 1a -> 2a");

        String expected = "DecimalInteger";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr9_with_scope() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr = "public static void Main ( ) { int a = 15 ; int b = a ; }";
        String[] subStrSpace = strExpr.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }
        Parser.node_list_analysis(list);
        SemanticAnalysis.analysis(list, "Level", 0);
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
//        SemanticAnalysis.print_table();

        List<AST> listExpr = new ArrayList<AST>();
        String expr = "b+a";
        String partitionResult = Lexer.space_splitting(expr);
        subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(listExpr, token);
        }
        String actual = SemanticAnalysis.check_arithmetic_expression(listExpr, "Level -> 1a -> 2a");

        String expected = "DecimalInteger";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr10_with_scope() {
        List<AST> list = new ArrayList<AST>();
        Token token;
        String strExpr = "public static void Main ( ) { int a = 15 ; int b = a ; }";
        String[] subStrSpace = strExpr.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }
        Parser.node_list_analysis(list);
        SemanticAnalysis.analysis(list, "Level", 0);
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
//        SemanticAnalysis.print_table();

        List<AST> listExpr = new ArrayList<AST>();
        String expr = "1.0+a";
        String partitionResult = Lexer.space_splitting(expr);
        subStrSpace = partitionResult.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(listExpr, token);
        }
        String actual = SemanticAnalysis.check_arithmetic_expression(listExpr, "Level -> 1a -> 2a");

//        for (AST s : listExpr) { AST.print_tree(s, 4, 100); }

        String expected = "NotAnInteger";
        Assert.assertEquals(expected, actual);
    }
}



