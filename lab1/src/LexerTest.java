import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {

    @org.junit.jupiter.api.Test
    void ownership_check() {
        List<String> expected = new ArrayList<>();
        List<String> actual = new ArrayList<>();

        expected.add(Lexer.ownership_check("Int"));
        actual.add("Id");
        expected.add(Lexer.ownership_check("int"));
        actual.add("Int");
        expected.add(Lexer.ownership_check("123variable"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check(""));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("_Qwer"));
        actual.add("Id");
        expected.add(Lexer.ownership_check("Абвгд"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("_qwee123"));
        actual.add("Id");
        expected.add(Lexer.ownership_check("i"));
        actual.add("Id");
        expected.add(Lexer.ownership_check("_i"));
        actual.add("Id");
        expected.add(Lexer.ownership_check("Qwe13_"));
        actual.add("Id");
        expected.add(Lexer.ownership_check("Qw._e13_"));
        actual.add("IdClassCall");
        expected.add(Lexer.ownership_check("Q.w_e13_"));
        actual.add("IdClassCall");
        expected.add(Lexer.ownership_check("1Qw_e13"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check(".Qw._e13_"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("\".Qw._e13"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("\".Qw._e13\""));
        actual.add("StringLiteral");

        Assert.assertEquals(expected, actual);

        //числа
        expected = new ArrayList<>();
        actual = new ArrayList<>();
        expected.add(Lexer.ownership_check("0b0_1"));
        actual.add("Binary");
        expected.add(Lexer.ownership_check("012345"));
        actual.add("Octal");
        expected.add(Lexer.ownership_check("012349"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("1012349"));
        actual.add("DecimalInteger");
        expected.add(Lexer.ownership_check("0"));
        actual.add("DecimalInteger");
        expected.add(Lexer.ownership_check("-0"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("12-42"));
        actual.add("unknown");
        expected.add(Lexer.ownership_check("0x12349"));
        actual.add("Hexadecimal");
        expected.add(Lexer.ownership_check("1012.349"));
        actual.add("NotAnInteger");
        expected.add(Lexer.ownership_check("0.0"));
        actual.add("NotAnInteger");

        Assert.assertEquals(expected, actual);
    }

    @Test
    void space_splitting() {
        List<String> expected = new ArrayList<>();
        List<String> actual = new ArrayList<>();

        expected.add(Lexer.space_splitting("static void Main(string[] args)"));
        actual.add("static void Main ( string [  ]  args ) ");
        expected.add(Lexer.space_splitting("class Program"));
        actual.add("class Program");
        expected.add(Lexer.space_splitting("+a+"));
        actual.add(" + a + ");
        expected.add(Lexer.space_splitting("for(int i=0; i<=2+1;i++)"));
        actual.add("for ( int i = 0 ;  i  <=  2 + 1 ; i  ++   ) ");
        expected.add(Lexer.space_splitting("int a,b;"));
        actual.add("int a , b ; ");
        Assert.assertEquals(expected, actual);
    }

    @Test
    void ignoring_comments() {
        List<String> expected = new ArrayList<>();
        List<String> actual = new ArrayList<>();

        expected.add(Lexer.ignoring_comments("static void //Main(string[] args)"));
        actual.add("static void  ");

        expected.add(Lexer.ignoring_comments("//static void Main(string[] args)"));
        actual.add(" ");
        expected.add(Lexer.ignoring_comments("static void /*Коммент*/Main(string[] args)"));
        actual.add("static void  Main(string[] args)");
        expected.add(Lexer.ignoring_comments("static void /* Main(string[] args)"));
        actual.add("static void  ");
        expected.add(Lexer.ignoring_comments("static void */ Main(string[] args)"));
        actual.add("  Main(string[] args)");
        expected.add(Lexer.ignoring_comments("static void /**/ Main(string[] args)"));
        actual.add("static void   Main(string[] args)");

        Assert.assertEquals(expected, actual);
    }
}