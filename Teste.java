import java.io.IOException;

public class Teste {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Por favor, forneça o nome do arquivo como parâmetro.");
        } else {
            String fileName = args[0];
            Lexer lexer = new Lexer(fileName);
            Token token;
            do {
                token = lexer.scan();
                if (token != null)
                    System.out.println(token.getToken().toString() + "\t" + token.getLexeme());
            } while (token.getToken() != Tag.END_OF_FILE);
        }

    }
}
