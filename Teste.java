import java.io.IOException;

public class Teste {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer("teste3.txt");
        Token token;
        do {
            token = lexer.scan();
            if(token != null) System.out.println("Token: " + token.getToken().toString() + " - Lexema: " + token.getLexeme());
        } while(token.getToken() != Tag.END_OF_FILE);
    }
}
