import AST.AST;
import lexer.Lexer;
import org.junit.Assert;
import parser.Parser;
import supporting_structures.Token;

import java.util.ArrayList;
import java.util.List;

public class ParserTest {
    @org.junit.jupiter.api.Test
    void op_assign_and_create_var() {
        List< AST > actual = new ArrayList<AST>();
        List< AST > expected = new ArrayList<AST>();
        String nameToken;

        System.out.println("1. a = b + c; \n");
        Token token = new Token("a", "Id", 1, 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "=", "OperatorAssignment", 1, 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "b", "Id", 1, 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "+", "OperatorAddition", 1, 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token("c", "Id", 1, 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ";", "Semicolon", 1, 6);
        Parser.add_to_the_list_of_nodes(actual, token);

        token = new Token( "Assign operation", "Assign operation", 1, 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("2. int a = b + c; \n");
        nameToken = "int";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "a";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "=";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "b";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "+";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "c";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 6);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ";";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 7);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Creat and assign";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("3. int a; \n");
        nameToken = "int";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "a";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ";";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Var creat fin";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("4. a += b + c - 2; \n");
        nameToken = "a";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "+=";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "b";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "+";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "c";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "-";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 6);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "2";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 7);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ";";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Assign operation";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("5. string[] args; \n");
        nameToken = "string";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "[";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "]";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "args";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ";";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Var creat fin";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("6. string[] args; \n");
        nameToken = "string";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "[";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "]";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "args";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Var creat";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());
    }

    @org.junit.jupiter.api.Test
    void call_func() {
        System.out.println("1. a=Lex.add();\n");
        List< AST > actual = new ArrayList<AST>();
        List< AST > expected = new ArrayList<AST>();
        Token token = new Token( "a", Lexer.ownership_check("a"),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "=", Lexer.ownership_check("="),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "Lex.add", Lexer.ownership_check("Lex.add"),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "(", Lexer.ownership_check("("),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ")", Lexer.ownership_check(")"),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ";", Lexer.ownership_check(";"),1 , 6);
        Parser.add_to_the_list_of_nodes(actual, token);

        token = new Token( "Assign call func", "Assign call func",1 , 7);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        System.out.println("\n2. int a=Lex.add();\n");
        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        token = new Token( "int", Lexer.ownership_check("int"),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "a", Lexer.ownership_check("a"),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "=", Lexer.ownership_check("="),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "Lex.add", Lexer.ownership_check("Lex.add"),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "(", Lexer.ownership_check("("),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ")", Lexer.ownership_check(")"),1 , 6);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ";", Lexer.ownership_check(";"),1 , 7);
        Parser.add_to_the_list_of_nodes(actual, token);

        token = new Token( "Creat and assign", "Creat and assign",1 , 8);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        System.out.println("\n3. Console.WriteLine(i);\n");

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        token = new Token( "Console.WriteLine", Lexer.ownership_check("Console.WriteLine"), 1, 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "(", Lexer.ownership_check("("), 1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( "i", Lexer.ownership_check("i"),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ")", Lexer.ownership_check(")"),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        token = new Token( ";", Lexer.ownership_check(";"),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);

        token = new Token( "Call func fin", "Call func fin",1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());
    }

    @org.junit.jupiter.api.Test
    void declaration() {
        String nameToken;
        List<AST> actual = new ArrayList<AST>();
        List<AST> expected = new ArrayList<AST>();
        System.out.println("1. for(int i = 0; i < 2; i++) \n");
        nameToken = "for";
        Token token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "(";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "int";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "i";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "=";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "0";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ";";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "i";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 6);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "<";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 7);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "2";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ";";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "i";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "++";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ")";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Declaration for";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);
        for (AST s : actual) { AST.print_tree(s, 4, 100); }

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("2. if(i < 5 && 2 > j) \n");
        nameToken = "if";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "(";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "i";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "<";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "5";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "&&";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "2";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 5);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ">";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 6);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "j";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 7);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ")";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 8);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Declaration if";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());

        actual = new ArrayList<AST>();
        expected = new ArrayList<AST>();
        System.out.println("3. public static void Main(string[] args) \n");
        nameToken = "public";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 1);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "static";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 2);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "void";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 3);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "Main";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = "(";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 4);
        Parser.add_to_the_list_of_nodes(actual, token);
        nameToken = ")";
        token = new Token( nameToken, Lexer.ownership_check(nameToken),1 , 7);
        Parser.add_to_the_list_of_nodes(actual, token);

        nameToken = "Function declaration";
        token = new Token( nameToken, nameToken,1 , 1);
        Parser.add_to_the_list_of_nodes(expected, token);

        print_test_data(actual, expected);

        Parser.node_list_analysis(actual);

        System.out.println("\nAfter conversion:");
        print_test_data(actual, expected);

        Assert.assertEquals(expected.get(0).getTypeToken(), actual.get(0).getTypeToken());
    }
//    @org.junit.jupiter.api.Test
//    void check_error() {
//        List<AST> list = new ArrayList<AST>();
//        Lexer lexer = new Lexer();
//        lexer.start("../nod with error.cs", list);
//        Parser.check_error(list);
//
////        for (AST s: list)
////            AST.print_tree(s, 4, 100);
//
//        List<String> actual = Parser.getErrors();
//        List<String> expected = new ArrayList<>();
//        expected.add("<1 : 14 'Program'> After expected body or body with error");
//        expected.add("<2 : 1 '{'> Unpaired");
//        expected.add("<3 : 29 ')'> After expected body or body with error");
//        expected.add("<5 : 17 '15'> After expected ';'");
//        expected.add("<9 : 33 ')'> After expected body or body with error");
//        expected.add("<11 : 13 'if'> After expected block '(...)' or block '(...)' with error");
//        expected.add("<11 : 16 '('> Inside brackets expected logical expression");
//        expected.add("<13 : 17 'if'> After expected block '(...)' or block '(...)' with error");
//        expected.add("<13 : 20 '('> Inside brackets expected logical expression");
//        expected.add("<15 : 40 ')'> After expected ';'");
//        expected.add("<21 : 20 '='> After expected name or numb");
//
//        print_two_list(expected, actual);
//
//        Assert.assertEquals(expected, actual);
//    }

    void print_test_data(List<AST> actual, List<AST> expected) {
        print_list(actual, "Actual");
        print_list(expected, "Expected");
    }


    void print_list(List<AST> list, String nameList) {
        System.out.println(nameList + ":");
        for(AST s: list)
            System.out.print("<" + s.getTypeToken() + "> ");
        System.out.print("\n");
    }
}
