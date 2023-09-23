import java.io.*;
import java.util.*;

public class Lexer {
    public static int line = 1; // contador de linhas
    private char ch = ' '; // caractere lido do arquivo
    private FileReader file;
    private Hashtable words = new Hashtable();
    private boolean EOF = false;

    /* Método para inserir palavras reservadas na HashTable */
    private void reserve(Word w) {
        words.put(w.getLexeme(), w); // lexema é a chave para entrada na HashTable
    }

    /* Método construtor */
    public Lexer(String fileName) throws FileNotFoundException {
        try {
            file = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            System.out.println("Arquivo não encontrado");
            throw e;
        }
        // Insere palavras reservadas na HashTable
        reserve(new Word("class", Tag.CLASS));
        reserve(new Word("if", Tag.IF));
        reserve(new Word("int", Tag.INT));
        reserve(new Word("float", Tag.FLOAT));
        reserve(new Word("string", Tag.STRING));
        reserve(new Word("do", Tag.DO));
        reserve(new Word("while", Tag.WHILE));
        reserve(new Word("read", Tag.READ));
        reserve(new Word("write", Tag.WRITE));
    }

    /* Lê o próximo caractere do arquivo */
    private void readch() throws IOException {
        int chInt = file.read();

        // se for fim de arquivo
        if (chInt == -1) EOF = true;
        
        ch = (char) chInt;
    }


    /* Lê o próximo caractere do arquivo e verifica se é igual a c */
    private boolean readch(char c) throws IOException {
        readch();
        if (ch != c)
            return false;
        ch = ' ';
        return true;
    }

    private Word erroTokenNaoEncontrado() throws IOException {
        String lexemaInvalido = "";

        // Se o token nao for encontrado, todos caracteres para frente (ate achar um espaco em branco) sera considerado parte do token invalido
        while (true) {
            if (Character.isWhitespace(ch)) {
                break;
            } else {
                lexemaInvalido += ch;
                readch();
                if(EOF) break;
            }
        }
        System.out.println("Erro léxico na linha " + line + ". Lexema inválido: " + lexemaInvalido);
        if(EOF) return Word.EOF;
        else return new Word(lexemaInvalido, Tag.INVALID_TOKEN);
    }

    public Token scan() throws IOException {        
        // Desconsidera delimitadores na entrada
        while (true) {
            if (Character.isWhitespace(ch)) {
                if (ch == '\n') line++; // conta linhas
                readch();
            } else {
                break;
            }
        }

        // Se for fim de arquivo
        if(EOF) return Word.EOF;

        switch (ch) {
            // Operadores
            case '&':
                if (readch('&')) {
                    return Word.AND;
                } else {
                    return this.erroTokenNaoEncontrado();
                }
            case '|':
                if (readch('|'))
                    return Word.OR;
                else {
                    return this.erroTokenNaoEncontrado();
                }
            case '=':
                if (readch('='))
                    return Word.EQUAL;
                else {
                    return Word.ASSIGN;
                }
            case '<':
                if (readch('='))
                    return Word.LESS_EQUAL;
                else {
                    return this.erroTokenNaoEncontrado();
                }
            case '>':
                if (readch('='))
                    return Word.GREATER_EQUAL;
                else {
                    return this.erroTokenNaoEncontrado();
                }
            case '/':
                readch();
                // comentario com mais de uma linha
                if (ch == '*') {
                    ch = ' '; // limpar o '*', para nao interferir como o primeiro '*' quando verifica o encerramento do comentario
                    while(true) {
                        char anterior = ch;
                        readch();
                        if(anterior == '*' && ch == '/') {
                            readch();
                            break;
                        }
                        if(EOF) return Word.EOF;
                        if(ch == '\n') line++;
                    }
                    return this.scan(); // chama recursivamente o proximo token
                }
                // comentario em uma linha
                else if(ch == '/') {
                    // desconsiderar tudo enquanto nao encontrar a proxima linha ou EOF
                    while(!readch('\n')) {
                        if(EOF) return Word.EOF;
                    }
                    line++; // somar uma linha, pois acabou o comentario
                    return this.scan(); // chama recursivamente o proximo token
                }
                // operador de divisao
                else {
                    return Word.OP_DIVISION;
                }
            case '{':
                readch();
                return Word.OPEN_BRACKET;
            case '}':
                readch();
                return Word.CLOSE_BRACKET;
            case ';':
                readch();
                return Word.SEMICOLON;
                
        }
        // Números
        if (Character.isDigit(ch)) {
            int value = 0;
            do {
                value = 10 * value + Character.digit(ch, 10);
                readch();
            } while (Character.isDigit(ch));
            return new Float(value);
        }
        // Identificadores
        if (Character.isLetter(ch)) {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch));
            String s = sb.toString();
            Word w = (Word) words.get(s);
            if (w != null)
                return w; // palavra já existe na HashTable
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }
        
        return this.erroTokenNaoEncontrado();
    }
}