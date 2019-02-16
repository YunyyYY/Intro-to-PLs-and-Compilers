import java.util.*;
import java.io.*;
import java_cup.runtime.*;  // defines Symbol

/**
 * This program is to be used to test the Carrot scanner.
 * This version is set up to test all tokens, but more code is needed to test 
 * other aspects of the scanner (e.g., input that causes errors, character 
 * numbers, values associated with tokens).
 */
public class P2 {

    private static int char_count;

    public static void main(String[] args) throws IOException {
                                           // exception may be thrown by yylex

        // ADD CALLS TO OTHER TEST METHODS HERE

        // test illegal tokens
        System.out.println("----- test illegal tokens -----");
        testWrongTokens();
        CharNum.num = 1;

        // test comments
        System.out.println("----- test comments -----");
        testComments();
        CharNum.num = 1;

        // test all tokens
        System.out.println("----- test all legal tokens -----");
        LineNum.num = 1;   // System.out.println(LineNum.num);
        char_count = CharNum.num;
        testAllTokens();
        if (char_count-1 != 975) {
            System.err.println("[CharCount] Count wrongTokens.in chars wrong!");
            // System.err.println(char_count);
        }
        if (LineNum.num != 83) {
            System.err.println("[LineCount] Count lines wrong!");
        }  // System.out.println(LineNum.num);

    }

    /**
     * testAllTokens
     *
     * Open and read from file allTokens.in
     * For each token read, write the corresponding string to allTokens.out
     * If the input file contains all tokens, one per line, we can verify
     * correctness of the scanner by comparing the input and output files
     * (e.g., using a 'diff' command).
     */
    private static void testAllTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        PrintWriter outFile = null;
        try {
            inFile = new FileReader("allTokens.in");
            outFile = new PrintWriter(new FileWriter("allTokens.out"));
        } catch (FileNotFoundException ex) {
            System.err.println("File allTokens.in not found.");
            System.exit(-1);
        } catch (IOException ex) {
            System.err.println("allTokens.out cannot be opened.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();
        while (my_token.sym != sym.EOF) {
            switch (my_token.sym) {
            case sym.BOOL:
                outFile.println("bool"); 
                break;
			case sym.INT:
                outFile.println("int");
                break;
            case sym.VOID:
                outFile.println("void");
                break;
            case sym.TRUE:
                outFile.println("true"); 
                break;
            case sym.FALSE:
                outFile.println("false"); 
                break;
            case sym.STRUCT:
                outFile.println("struct"); 
                break;
            case sym.CIN:
                outFile.println("cin"); 
                break;
            case sym.COUT:
                outFile.println("cout");
                break;				
            case sym.IF:
                outFile.println("if");
                break;
            case sym.ELSE:
                outFile.println("else");
                break;
            case sym.WHILE:
                outFile.println("while");
                break;
            case sym.RETURN:
                outFile.println("return");
                break;
            case sym.ID:
                outFile.println(((IdTokenVal)my_token.value).idVal);
                break;
            case sym.INTLITERAL:  
                outFile.println(((IntLitTokenVal)my_token.value).intVal);
                break;
            case sym.STRINGLITERAL: 
                outFile.println(((StrLitTokenVal)my_token.value).strVal);
                break;    
            case sym.LCURLY:
                outFile.println("{");
                break;
            case sym.RCURLY:
                outFile.println("}");
                break;
            case sym.LPAREN:
                outFile.println("(");
                break;
            case sym.RPAREN:
                outFile.println(")");
                break;
            case sym.SEMICOLON:
                outFile.println(";");
                break;
            case sym.COMMA:
                outFile.println(",");
                break;
            case sym.DOT:
                outFile.println(".");
                break;
            case sym.WRITE:
                outFile.println("<<");
                break;
            case sym.READ:
                outFile.println(">>");
                break;				
            case sym.PLUSPLUS:
                outFile.println("++");
                break;
            case sym.MINUSMINUS:
                outFile.println("--");
                break;	
            case sym.PLUS:
                outFile.println("+");
                break;
            case sym.MINUS:
                outFile.println("-");
                break;
            case sym.TIMES:
                outFile.println("*");
                break;
            case sym.DIVIDE:
                outFile.println("/");
                break;
            case sym.NOT:
                outFile.println("!");
                break;
            case sym.AND:
                outFile.println("&&");
                break;
            case sym.OR:
                outFile.println("||");
                break;
            case sym.EQUALS:
                outFile.println("==");
                break;
            case sym.NOTEQUALS:
                outFile.println("!=");
                break;
            case sym.LESS:
                outFile.println("<");
                break;
            case sym.GREATER:
                outFile.println(">");
                break;
            case sym.LESSEQ:
                outFile.println("<=");
                break;
            case sym.GREATEREQ:
                outFile.println(">=");
                break;
			case sym.ASSIGN:
                outFile.println("=");
                break;
			default:
				outFile.println("UNKNOWN TOKEN");
            } // end switch

            char_count = char_count + CharNum.num;
            my_token = my_scanner.next_token();
        } // end while
        outFile.close();
    }

    /**
     * testComments
     *
     * Open and read from file comments.in, which is completely composed
     * of comments. No output file will be generated. This function should
     * have no output, otherwise an error occurs.
     */
    private static void testComments() throws IOException {
        // open input and output files
        FileReader inFile = null;
        try {
            inFile = new FileReader("comments.in");
        } catch (FileNotFoundException ex) {
            System.err.println("File comments.in not found.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();

        while (my_token.sym != sym.EOF) {
            switch (my_token.sym) {
                case sym.BOOL:
                case sym.INT:
                case sym.VOID:
                case sym.TRUE:
                case sym.FALSE:
                case sym.STRUCT:
                case sym.CIN:
                case sym.COUT:
                case sym.IF:
                case sym.ELSE:
                case sym.WHILE:
                case sym.RETURN:
                case sym.ID:
                case sym.INTLITERAL:
                case sym.STRINGLITERAL:
                case sym.LCURLY:
                case sym.RCURLY:
                case sym.LPAREN:
                case sym.RPAREN:
                case sym.SEMICOLON:
                case sym.COMMA:
                case sym.DOT:
                case sym.WRITE:
                case sym.READ:
                case sym.PLUSPLUS:
                case sym.MINUSMINUS:
                case sym.PLUS:
                case sym.MINUS:
                case sym.TIMES:
                case sym.DIVIDE:
                case sym.NOT:
                case sym.AND:
                case sym.OR:
                case sym.EQUALS:
                case sym.NOTEQUALS:
                case sym.LESS:
                case sym.GREATER:
                case sym.LESSEQ:
                case sym.GREATEREQ:
                case sym.ASSIGN:
                    System.out.println("[TEST ERROR] fail to ignore comments");
                    break;
                default:
                    System.err.println("UNKNOWN TOKEN");
            }
            my_token = my_scanner.next_token();
        }
    }

    /**
     * testWrongTokens
     *
     * Open and read from file wrongTokens.in, which is completely composed
     * of wrong tokens, one per line. No output file will be generated, but all errors
     * will be output to standard error as built-in error messages.
     *
     */
    private static void testWrongTokens() throws IOException {
        // open input and output files
        FileReader inFile = null;
        try {
            inFile = new FileReader("wrongTokens.in");
        } catch (FileNotFoundException ex) {  // nothing is this file should be recognized as legal tokens
            System.err.println("File wrongTokens.in not found.");
            System.exit(-1);
        }

        // create and call the scanner
        Yylex my_scanner = new Yylex(inFile);
        Symbol my_token = my_scanner.next_token();

        while (my_token.sym != sym.EOF) {  // System.out.println(LineNum.num);
            switch (my_token.sym) {
                case sym.BOOL:
                case sym.INT:
                case sym.VOID:
                case sym.TRUE:
                case sym.FALSE:
                case sym.STRUCT:
                case sym.CIN:
                case sym.COUT:
                case sym.IF:
                case sym.ELSE:
                case sym.WHILE:
                case sym.RETURN:
                case sym.ID:
                case sym.STRINGLITERAL:
                case sym.LCURLY:
                case sym.RCURLY:
                case sym.LPAREN:
                case sym.RPAREN:
                case sym.SEMICOLON:
                case sym.COMMA:
                case sym.DOT:
                case sym.WRITE:
                case sym.READ:
                case sym.PLUSPLUS:
                case sym.MINUSMINUS:
                case sym.PLUS:
                case sym.MINUS:
                case sym.TIMES:
                case sym.DIVIDE:
                case sym.NOT:
                case sym.AND:
                case sym.OR:
                case sym.EQUALS:
                case sym.NOTEQUALS:
                case sym.LESS:
                case sym.GREATER:
                case sym.LESSEQ:
                case sym.GREATEREQ:
                case sym.ASSIGN:
                    if (LineNum.num == 1)
                        System.err.println("[TEST ERROR] detect overflow integer as wrong token " +
                                "on line " + LineNum.num);
                    else if (LineNum.num > 1 && LineNum.num < 11)
                        System.err.println("[TEST ERROR] fail to detect illegal charater on line "
                                + LineNum.num );
                    else {
                        switch (LineNum.num) {
                            case 11:
                                System.err.println("[TEST ERROR] fail to detect unterminated string " +
                                        "on line " + LineNum.num);
                                break;
                            case 12:
                                System.err.println("[TEST ERROR] fail to detect unterminated with" +
                                        " illegal escape char on line "+ LineNum.num);
                                break;
                            case 13:
                                System.err.println("[TEST ERROR] terminated with illegal escape " +
                                        "char on line "+ LineNum.num);
                                break;
                            case 14:
                                System.err.println("[TEST ERROR] fail to detect unterminated with " +
                                        "new line on line "+ LineNum.num);
                                break;
                            case 15:
                                System.err.println("[TEST ERROR] fail to detect unterminated without " +
                                        "new line and illegal escape char on line "+ LineNum.num);
                                break;
                            default:
                                System.err.println("[TEST ERROR] Fail to detect end of file!");
                        }
                    }
                    break;
                case sym.INTLITERAL:  // only overflow integer are included in wrongTokens.in
                    if (sym.INTLITERAL > Integer.MAX_VALUE)
                        System.err.println("[TEST ERROR] Integer overflow not detected on line 1!");
                    break;
                default:
                    System.err.println("UNKNOWN TOKEN");
            } // end switch

            my_token = my_scanner.next_token();
        }   // System.out.println(LineNum.num);
    }
}
