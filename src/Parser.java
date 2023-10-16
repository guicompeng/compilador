package src;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {
    final int IF = 1, THEN = 2, ELSE = 3, BEGIN = 4, END = 5, PRINT = 6, SEMI = 7, NUM = 8, EQ = 9;

    private static Lexer lexer;
    private static Token tok;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Por favor, forneça o nome do arquivo como parâmetro.");
        } else {
            String fileName = args[0];
            lexer = new Lexer(fileName);
            System.out.printf("%-30s %-30s%n", "Token", "Lexema");
            System.out.println("----------------------------------------------------------------");
            do {
                advance();
                if (tok != null)
                    System.out.printf("%-30s %-30s%n", tok.getToken().toString(), tok.getLexeme());
            } while (tok.getToken() != Tag.END_OF_FILE);
        }

    }

    private static void error() throws IOException {
        System.out.println("Erro sintático na linha " + lexer.line);
        System.exit(0);
    }
    
    private static void advance() throws IOException {
        tok = lexer.scan(); // lê próximo token
    }

    private static void eat(Token t) throws IOException {
        if (tok == t)
            advance();
        else
            error();
    }
}
