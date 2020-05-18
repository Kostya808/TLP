package lexer;

import AST.AST;
import parser.Parser;
import supporting_structures.Token;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private static boolean ignore_flag;
    private static List<String> rawSequences = new ArrayList<>();

    public static void start(File file, List<AST> listNodes) {
        String readLine;
        int lineCounter = 0;
        ignore_flag = false;

        try {
            FileReader fr = new FileReader(file); //создаем BufferedReader с сущ. FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr); // считаем сначала первую строку
            readLine = reader.readLine();
            while (readLine != null) {
                lineCounter++;
                tokens(readLine, lineCounter, listNodes);
                readLine = reader.readLine(); // считываем остальные строки в цикле
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!rawSequences.isEmpty()) {
            System.out.println("\nНеопределённые последовательности:");
            for (String s : rawSequences)
                System.out.println(s);
        }
    }

    public static void tokens(String sourceString, int lineCounter, List<AST> listNodes) {
        String processedStr = ignoring_comments(sourceString);
        if(processedStr.contains("\"")) {
            String[] subStr = quotation_mark(processedStr); //разбиение по кавычкам
            for(String str: subStr) {
                if (str.contains("\"")) {
                    parser_connection(new String[]{str}, lineCounter, sourceString, listNodes);
                }
                else {
                    String partitionResult = space_splitting(str); //вставка пробелов рядом со знаками
                    String[] subStrSpace = partitionResult.split(" ");
                    parser_connection(subStrSpace, lineCounter, sourceString, listNodes);
                }

            }
        }
        else {
            String partitionResult = space_splitting(processedStr); //вставка пробелов рядом со знаками
            String[] subStr = partitionResult.split(" "); // Разделения строки с помощью метода split()
            parser_connection(subStr, lineCounter, sourceString, listNodes);
        }
    }

    private static void parser_connection(String[] arrString, int lineCounter, String sourceString, List<AST> listNodes) {
        String resultOfChecking;
        for(String s: arrString) {
            if(s.trim().length() != 0) {
                resultOfChecking = ownership_check(s);
                String output = "Loc=<" + lineCounter + ":" + sourceString.indexOf(s) + ">  " + resultOfChecking + "  " + s;
//                System.out.println(output);
                if(resultOfChecking.equals("unknown"))
                    rawSequences.add(output);
                Token token = new Token(s, resultOfChecking, lineCounter, sourceString.indexOf(s) + 1);
                sourceString = replacement_of_spent_tokens(sourceString, s);

                Parser.add_to_the_list_of_nodes(listNodes, token);
                Parser.node_list_analysis(listNodes);
            }
        }
    }

    public static String ignoring_comments(String line) {
        String[] ArrayPattern = new String[] {"//.*", "/\\*.*?\\*/" };
        Pattern pattern;
        Matcher matcher;
        for (String value : ArrayPattern) {
            pattern = Pattern.compile(value);
            matcher = pattern.matcher(line);
            line = matcher.replaceAll(" ");
        }
        pattern = Pattern.compile("/\\*.+");
        matcher = pattern.matcher(line);
        if(matcher.find()) {
            line = matcher.replaceFirst(" ");
            ignore_flag = true;
            return line;
        }
        pattern = Pattern.compile(".+\\*/");
        matcher = pattern.matcher(line);
        if(matcher.find()) {
            line = matcher.replaceFirst(" ");
            ignore_flag = false;
            return line;
        }
        if(ignore_flag)
            return " ";
        return line;
    }

    private static String[] quotation_mark(String line) {
        char[] bufArCh = line.toCharArray();
        int count = 0;
        for (char arCh : bufArCh) {
            if (arCh == '\"')
                count++;
        }
        int[] arrIndex = new int[count + 2];
        arrIndex[0] = 0;
        arrIndex[count + 1] = line.length();
        for (int i = 0, j = 1; i < bufArCh.length; i++) {
            if (bufArCh[i] == '\"') {
                arrIndex[j] = i;
                if(j % 2 == 0 && j != count + 1)
                    arrIndex[j]++;
                j++;
            }
        }

        String[] subStr = new String[count + 1];
        for(int i = 0; i < subStr.length; i++) {
            subStr[i] = line.substring(arrIndex[i], arrIndex[i + 1]);
        }
        return subStr;
    }

    public static String space_splitting(String line) {
        String newStr = line;
        String[] ArrayPattern = new String[] {"\\(", "\\)", "\\[", "]", "\\{", "}", "\\*", "/", ";", ":",
        ",", "%", "-", "\\+", "=", "-=",  "\\+=", "-\\s\\s-", "\\+\\s\\s\\+", "=\\s\\s=", "<", ">", "<\\s\\s=", ">\\s\\s=",
        "!\\s=", " \\*\\s\\s=", "/\\s\\s=", "\t"};
        String[] ArrayReplaceable = new String[] {" ( ", " ) ", " [ ", " ] ", " { ", " } ", " * ", " / ",  " ; ", " : ",
        " , ", " % ", " - ", " + ", " = ", " -= ", " += ", " -- ", " ++ ", " == ", " < ", " > ", " <= ", " >= ",
        " != ", " *= ", " /= ", " "};
        for(int i = 0; i < ArrayPattern.length; i++) {
            Pattern pattern = Pattern.compile(ArrayPattern[i]);
            Matcher matcher = pattern.matcher(newStr);
            newStr = matcher.replaceAll(ArrayReplaceable[i]);
        }
        return newStr;
    }

    public static String ownership_check(String line) {
        switch (line) {
            case ("{"):
                return "LBrace";
            case ("}"):
                return "RBrace";
            case ("("):
                return "LParen";
            case (")"):
                return "RParen";
            case ("["):
                return "LSquareBracket";
            case ("]"):
                return "RSquareBracket";
            case (","):
                return "Comma";
            case ("&&"):
                return "LogicalAnd";
            case ("||"):
                return "LogicalOr";
            case ("+"):
                return "OperatorAddition";
            case ("-"):
                return "OperatorSubtraction";
            case ("*"):
                return "OperatorMultiplication";
            case ("/"):
                return "OperatorDivision";
            case ("%"):
                return "OperatorMod";
            case ("="):
                return "OperatorAssignment";
            case (">"):
                return "OperatorMore";
            case ("<"):
                return "OperatorSmaller";
            case ("<="):
                return "OperatorLessOrEqual";
            case (">="):
                return "OperatorMoreOrEqual";
            case ("=="):
                return "OperatorEq";
            case ("+="):
                return "OperatorAddAssign";
            case ("-="):
                return "OperatorSubAssign";
            case ("*="):
                return "OperatorMultAssign";
            case ("/="):
                return "OperatorDivAssign";
            case ("!="):
                return "OperatorNotEq";
            case (";"):
                return "Semicolon";
            case ("++"):
                return "OperatorIncrement";
            case ("--"):
                return "OperatorDecrement";

            case ("new"):
                return "KeyWordNew";
            case ("null"):
                return "Null";
            case ("true"):
                return "True";
            case ("false"):
                return "False";
            case ("void"):
                return "Void";
            case ("int"):
                return "Int";
            case ("double"):
                return "Double";
            case ("string"):
                return "String";
            case ("public"):
                return "KeyWordPublic";
            case ("private"):
                return "KeyWordPrivate";
            case ("static"):
                return "ModifierStatic";
            case ("class"):
                return "KeywordClass";
            case ("using"):
                return "KeywordUsing";
            case ("namespace"):
                return "KeywordNamespace";
            case ("if"):
                return "KeywordIf";
            case ("else"):
                return "KeywordElse";
            case ("for"):
                return "KeywordFor";
            case ("break"):
                return "OperatorBreak";
            default:
                if(Pattern.matches("0[bB][_01]*[01]", line))
                    return "Binary";
                else if(Pattern.matches("0[0-7]+", line))
                    return "Octal";
                else if(Pattern.matches("0[xX][\\d[a-f][A-F]]+", line))
                    return "Hexadecimal";
                else if(Pattern.matches("[1-9]\\d*", line))
                    return "DecimalInteger";
                else if(Pattern.matches("[0-9]", line))
                    return "DecimalInteger";
                else if(Pattern.matches("[1-9][\\d.]*", line))
                    return "NotAnInteger";
                else if(Pattern.matches("0\\.\\d*", line))
                    return "NotAnInteger";
                else if(Pattern.matches("[\\w&&[^\\d]]\\w*", line))
                    return "Id";
                else if(Pattern.matches("[\\w&&[^\\d]][[.\\w]\\w]*", line))
                    return "IdClassCall";
                else if(Pattern.matches("[\"].*[\"]", line))
                    return "StringLiteral";
                else if(Pattern.matches("['].*[']", line))
                    return "CharacterLiteral";
                return "unknown";
        }
    }
    private static String replacement_of_spent_tokens (String line, String s) {
        int firstOccurrence = line.indexOf(s);
        char[] bufferArrayChar = line.toCharArray();
        for(int i = firstOccurrence; i < firstOccurrence + s.length(); i++) {
            bufferArrayChar[i] = ' ';
        }
        return new String(bufferArrayChar);
    }
}
