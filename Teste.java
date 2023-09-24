import java.io.IOException;

public class Teste {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Por favor, forneça o nome do arquivo como parâmetro.");
        } else {
            String fileName = args[0];
            Lexer lexer = new Lexer(fileName);
            Token token;
            System.out.printf("%-30s %-30s%n", "Token", "Lexema");
            System.out.println("----------------------------------------------------------------");
            do {
                token = lexer.scan();
                if (token != null)
                    System.out.printf("%-30s %-30s%n", token.getToken().toString(), token.getLexeme());
            } while (token.getToken() != Tag.END_OF_FILE);

            // imprimir tabela de simbolos
            System.out.println("\n\nTabela de simbolos:");
            lexer.printTabelaSimbolos();
        }

    }
}
