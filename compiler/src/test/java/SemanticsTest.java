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
        List<AST> list = create_tree_by_line("1 + 2");
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "DecimalInteger";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr2() {
        List<AST> list = create_tree_by_line("1 + 2 + 3 + 4");
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "DecimalInteger";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr3() {
        List<AST> list = create_tree_by_line("1 + 2 + 3 + 4.0");
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "NotAnInteger";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr4_with_String() {
        List<AST> list = create_tree_by_line("1 + \"2\"");
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr5_with_two_string() {
        List<AST> list = create_tree_by_line("\"1\" + \"2\"");
        String actual = SemanticAnalysis.check_arithmetic_expression(list, "");
        String expected = "StringLiteral";

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr6_with_scope() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        AST node = new AST();
        node.setToken("a");
        String actual = SemanticAnalysis.scope_contains_var("Level -> 1a", node);

        String expected = "int";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr7_with_scope() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        AST node = new AST();
        node.setToken("a");
        String actual = SemanticAnalysis.scope_contains_var("Level -> 1a -> 2a", node);
        String expected = "int";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr8_with_scope() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr = create_tree_by_line("1 + a");
        String actual = SemanticAnalysis.check_arithmetic_expression(listExpr, "Level -> 1a -> 2a");

        String expected = "DecimalInteger";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr9_with_scope() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; int b ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr = create_tree_by_line("b + a");
        String actual = SemanticAnalysis.check_arithmetic_expression(listExpr, "Level -> 1a -> 2a");

        String expected = "DecimalInteger";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_arith_expr10_with_scope() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; int b ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr = create_tree_by_line("1.0 + a");
        String actual = SemanticAnalysis.check_arithmetic_expression(listExpr, "Level -> 1a -> 2a");

        String expected = "NotAnInteger";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_assign_op1() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a = 15 ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr =create_tree_by_line("a = 1 ;");
        boolean actual = SemanticAnalysis.check_assign_op(listExpr.get(0).getChildren(), "Level -> 1a -> 2a");
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_assign_op2() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a = 15 ; string b ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr =create_tree_by_line("a = b ;");
        boolean actual = SemanticAnalysis.check_assign_op(listExpr.get(0).getChildren(), "Level -> 1a -> 2a");
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_assign_op3() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { string a = 15 ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr =create_tree_by_line("a = \"str\" ;");
        boolean actual = SemanticAnalysis.check_assign_op(listExpr.get(0).getChildren(), "Level -> 1a -> 2a");
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_assign_op4() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr =create_tree_by_line("a = \"1.52\" ;");

        boolean actual = SemanticAnalysis.check_assign_op(listExpr.get(0).getChildren(), "Level -> 1a -> 2a");
        boolean expected = false;
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_assign_arith_expr1() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { int a ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr = create_tree_by_line("a = 1.52 * 2.86 + 3.1223 - 4.83 ;");

        boolean actual = SemanticAnalysis.check_assign_op(listExpr.get(0).getChildren(), "Level -> 1a -> 2a");
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_assign_arith_expr2() {
        List<AST> list = create_tree_by_line("public static void Main ( ) { string a ; string b = \"s\" ; }");
        SemanticAnalysis.analysis(list, "Level", 0);

        List<AST> listExpr = create_tree_by_line("a = b + \"uper!\" ;");

        boolean actual = SemanticAnalysis.check_assign_op(listExpr.get(0).getChildren(), "Level -> 1a -> 2a");
        boolean expected = true;
        Assert.assertEquals(expected, actual);
    }

    List<AST> create_tree_by_line(String str) {
        Token token;
        List<AST> list = new ArrayList<AST>();
        String[] subStrSpace = str.split(" ");
        for(String s : subStrSpace) {
            token = new Token(s, Lexer.ownership_check(s), 0, 0);
            Parser.add_to_the_list_of_nodes(list, token);
        }
        Parser.node_list_analysis(list);
        return list;
    }
}



