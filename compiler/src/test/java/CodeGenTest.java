import AST.AST;
import lexer.Lexer;
import org.junit.Assert;
import code_generation.CodeGen;
import parser.Parser;
import supporting_structures.Token;

import java.util.ArrayList;
import java.util.List;

public class CodeGenTest {
    @org.junit.jupiter.api.Test
    void check_generated_operand1() {
        List<AST> listExpr = create_tree_by_line("array [ a ] ");
        String actual = CodeGen.generatedOperand(listExpr.get(0));
        String expected = "array(,%ecx,4)";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_generated_operand2() {
        List<AST> listExpr = create_tree_by_line("variable");
        String actual = CodeGen.generatedOperand(listExpr.get(0));
        String expected = "variable";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void check_generated_operand3() {
        List<AST> listExpr = create_tree_by_line("123");
        String actual = CodeGen.generatedOperand(listExpr.get(0));
        String expected = "$123";
        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void function_declaration_processing1() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());

        List<AST> list = create_tree_by_line("public static void Main ( )");
        CodeGen.function_declaration_processing(list.get(0).getChildren());

        actual = CodeGen.getBlockText();

        expected.add("\n.globl  main");
        expected.add(".type  main, @function");
        expected.add("\nmain:");
        expected.add("\t\tpushq %rbp");
        expected.add("\t\tmovq %rsp, %rbp\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void function_declaration_processing2() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());

        List<AST> list = create_tree_by_line("public static void Function ( )");
        CodeGen.function_declaration_processing(list.get(0).getChildren());

        actual = CodeGen.getBlockText();

        expected.add("Function:");
        expected.add("\t\tpushq %rbp");
        expected.add("\t\tmovq %rsp, %rbp\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void function_call_processing1() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());

        List<AST> list = create_tree_by_line("Console.Write ( str ) ;");
//        for (AST s : list) { AST.print_tree(s, 4, 100); }
        CodeGen.function_call_processing(list.get(0).getChildren().get(0).getChildren());

        actual = CodeGen.getBlockText();

//        for(String s : actual)
//            System.out.println(s);
        expected.add("\t\tmov \t$str0, %rdi\t# Console.Write str");
        expected.add("\t\tmov \tstr, %rsi");
        expected.add("\t\tcall\tprintf\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void var_create1() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());
        CodeGen gen = new CodeGen();

        List<AST> list = create_tree_by_line("int a = 1 + 2 ;");
        CodeGen.analysis(list);

        actual = CodeGen.getBlockText();

        expected.add(".text");
        expected.add("\t\tmovl\t$1, %eax\t# 1 + 2");
        expected.add("\t\taddl\t$2, %eax");
        expected.add("\t\tmovl\t%eax, a\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void var_create2() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());
        CodeGen gen = new CodeGen();

        List<AST> list = create_tree_by_line("int a = 2 * 3 ;");
        CodeGen.analysis(list);

        actual = CodeGen.getBlockText();

        expected.add(".text");
        expected.add("\t\tmovl\t$2, %eax\t# 2 * 3");
        expected.add("\t\tmovl\t$3, %ebx");
        expected.add("\t\tmull\t%ebx");
        expected.add("\t\tmovl\t%eax, a\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void var_create3() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());
        CodeGen gen = new CodeGen();

        List<AST> list = create_tree_by_line("int a = Console.ReadLine ( ) ;");
        CodeGen.analysis(list);

        actual = CodeGen.getBlockText();

        expected.add(".text");
        expected.add("\t\tmov $str0, %rdi\t# scanf");
        expected.add("\t\tleaq a, %rsi");
        expected.add("\t\tcall scanf\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void block_if_processing1() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());
        CodeGen gen = new CodeGen();

        List<AST> list = create_tree_by_line("if ( a == b ) { }");
        CodeGen.analysis(list);

        actual = CodeGen.getBlockText();
        actual.remove(actual.size() - 1);
        actual.remove(actual.size() - 1);

        expected.add(".text");
        expected.add("\t\t# if \t# a == b");
        expected.add("\t\tmovl\ta, %eax");
        expected.add("\t\tmovl\tb, %ebx");
        expected.add("\t\tcmpl\t%ebx, %eax");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void block_for_processing1() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());
        CodeGen gen = new CodeGen();

        List<AST> list = create_tree_by_line("for ( int i = 0 ; i < size ; i ++ ) { }");
        CodeGen.analysis(list);

        actual = CodeGen.getBlockText();

        expected.add(".text");
        expected.add("\t\tjmp \tcondition_jump_1");
        expected.add("condition_jump_2:");
        expected.add("\t\tincl\ti");
        expected.add("condition_jump_1:");
        expected.add("\t\tmovl\ti, %eax");
        expected.add("\t\tmovl\tsize, %ebx");
        expected.add("\t\tcmpl\t%ebx, %eax");
        expected.add("\t\tjl \tcondition_jump_2\n");

        Assert.assertEquals(expected, actual);
    }

    @org.junit.jupiter.api.Test
    void crement_processing1() {
        List<String> actual, expected = new ArrayList<>();
        CodeGen.setBlockText(new ArrayList<>());

        List<AST> list = create_tree_by_line("i -- ;");
        CodeGen.analysis(list);

        actual = CodeGen.getBlockText();

        expected.add("\t\tdecl\ti");

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