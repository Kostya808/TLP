import AST.AST;
import lexer.Lexer;
import parser.Parser;

import java.util.ArrayList;
import java.util.List;

public class Compiler {
    public static void main(String[] args) {
        List<AST> listNodes = new ArrayList<AST>();
        Lexer lexer = new Lexer();
        lexer.start("../nod.cs", listNodes);
        Parser.check_error(listNodes);
        Parser.print_error();
        for (AST s : listNodes) {
            AST.print_tree(s, 4, 100);
        }
    }
}
