import AST.AST;
import code_generation.CodeGen;
import lexer.Lexer;
import parser.Parser;
import semantics.SemanticAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Compiler {
    private static List<String> errorsList = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        File file;
        String options = "non";
        List<AST> listNodes = new ArrayList<AST>();
        Lexer lexer = new Lexer();
        CodeGen codeGeneration = new CodeGen();

        if(args.length == 0) {
//            file = new File(Objects.requireNonNull(Lexer.class.getClassLoader().getResource("nod.cs")).getFile());
            return;
        } else if(args.length == 2){
            options = args[0];
            file = new File(args[1]);
        } else {
            file = new File(args[0]);
        }

        switch (options) {
            case ("non"):
                Lexer.start(file, listNodes, options);
                SemanticAnalysis.analysis(listNodes, "Level", 0);
                check_error(listNodes);
                if (errorsList.isEmpty()) {
                    CodeGen.analysis(listNodes);
                    write_to_file(CodeGen.get_assembler_code());
                }
                break;
            case ("--dump-tokens"):
                Lexer.start(file, listNodes, options);
                Lexer.print_lexer();
                check_error(listNodes);
                break;
            case ("--dump-ast"):
                Lexer.start(file, listNodes, options);
                Parser.print_parse(listNodes);
                check_error(listNodes);
                break;
            case ("--dump-asm"):
                Lexer.start(file, listNodes, options);
                SemanticAnalysis.analysis(listNodes, "Level", 0);
                check_error(listNodes);
                if (errorsList.isEmpty()) {
                    CodeGen.analysis(listNodes);
                    CodeGen.print_asm();
                }
                break;
        }
        print_error();
    }

    private static void print_error() {
        if(!errorsList.isEmpty()) {
            System.out.println("\nErrors:");
            for (String s : errorsList)
                System.out.println(s);
        }
    }

    private static void check_error (List<AST> listNodes) {
        Lexer.print_unknown_tokens(errorsList);
        Parser.print_error(listNodes, errorsList);
        SemanticAnalysis.print_error(errorsList);
    }

    private static void write_to_file(List<String> asmCode) throws IOException {
        FileWriter writer = new FileWriter("output.s");
        for(String lineCode : asmCode) {
            writer.write(lineCode + System.getProperty("line.separator"));
        }
        writer.close();
    }
}
