import AST.AST;
import lexer.Lexer;
import parser.Parser;
import semantics.SemanticAnalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Compiler {
    public static void main(String[] args) {
        List<AST> listNodes = new ArrayList<AST>();
        Lexer lexer = new Lexer();
        if(args.length == 0) {
            File file = new File(Objects.requireNonNull(Lexer.class.getClassLoader().getResource("nod.cs")).getFile());
            lexer.start(file, listNodes);
        }
        else {
            File file = new File(args[0]);
            lexer.start(file, listNodes);
        }

        Parser.check_error(listNodes);

        for (AST s : listNodes) { AST.print_tree(s, 4, 100); }
        SemanticAnalysis.analysis(listNodes, "Level", 0);

//        SemanticAnalysis.print_table();
        SemanticAnalysis.print_declared_func();

        Parser.print_error();
        SemanticAnalysis.print_error();

        System.out.print("\n");
    }
}
