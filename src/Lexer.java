package src;
import java.io.*;
import java.util.*;

public class Lexer {
    public int line = 1; // contador de linhas
    private char ch = ' '; // caractere lido do arquivo
    private FileReader file;
    private Hashtable<String, Word> words = new Hashtable<String, Word>();

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
        reserve(new Word("else", Tag.ELSE));
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

    private void erroTokenNaoEncontrado() throws IOException {
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
        System.exit(0);
    }

    private void erroStringNaoFoiFechada(String stringNaoFechada) throws IOException {
        System.out.println("Erro léxico na linha " + line + ". String não foi fechada: " + stringNaoFechada);
        System.exit(0);
    }

    private void erroFloatMalFormado(String floatMalFormado) throws IOException {
        System.out.println("Erro léxico na linha " + line + ". Float mal formado: " + floatMalFormado);
        System.exit(0);
    }

    private void erroIntComeca0(String intComeca0) throws IOException {
        System.out.println("Erro léxico na linha " + line + ". Int começa com 0: " + intComeca0);
        System.exit(0);
    }

    private void erroEOFNaoEsperado() throws IOException {
        System.out.println("Erro léxico na linha " + line + ". Fim de arquivo não era esperado");
        System.exit(0);
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
                    this.erroTokenNaoEncontrado();
                }
            case '|':
                if (readch('|'))
                    return Word.OR;
                else {
                    this.erroTokenNaoEncontrado();
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
                    return Word.LESS;
                }
            case '>':
                if (readch('='))
                    return Word.GREATER_EQUAL;
                else {
                    return Word.GREATER;
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
                        if(EOF) this.erroEOFNaoEsperado();
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
                    return Word.OP_DIV;
                }
            case '"':
                String str = "";
                readch();
                while(ch != '"') {
                    if(EOF) return Word.EOF;
                    if(ch == '\n') this.erroStringNaoFoiFechada(str);
                    str += ch;
                    readch();
                }
                readch();
                return new Word(str, Tag.LITERAL);
            case '(':
                readch();
                return Word.OPEN_ROUND_BRACKET;
            case ')':
                readch();
                return Word.CLOSE_ROUND_BRACKET;
            case '{':
                readch();
                return Word.OPEN_CURLY_BRACKET;
            case '}':
                readch();
                return Word.CLOSE_CURLY_BRACKET;
            case ';':
                readch();
                return Word.SEMICOLON;
            case ',':
                readch();
                return Word.COMMA;
            case '+':
                readch();
                return Word.OP_SUM;
            case '-':
                readch();
                return Word.OP_SUB;
            case '*':
                readch();
                return Word.OP_MUL;
                
        }
        // Números
        if (Character.isDigit(ch)) {
            String value = "";
            // ler a parte inteira do digito
            while (Character.isDigit(ch)) {
                value += ch;
                readch();
            }
            // logo apos ler os digitos, se houver ponto, entao e float
            if(ch == '.') {
                value += ch; // inserir o . na string value
                // apos o ponto, precisa haver pelo menos mais um digito
                readch();
                if(Character.isDigit(ch))  {
                    // continua lendo enquanto for digito (na parte direita do .)
                     while (Character.isDigit(ch)) {
                        value += ch;
                        readch();
                    }
                        return new Float(Double.parseDouble(value));
                } else {
                    this.erroFloatMalFormado(value);
                }
            } 
            // se nao houver ponto, entao e int
            else {
                // se for int, nao pode comecar com 0
                if(value.length() > 1 && value.charAt(0) == '0') {
                    this.erroIntComeca0(value);
                } else {
                    return new Int(Integer.parseInt(value));
                }
            }
            
        }
        // Identificadores
        if (Character.isLetter(ch)) {
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(ch);
                readch();
            } while (Character.isLetterOrDigit(ch) || ch == '_');
            String s = sb.toString();
            Word w = (Word) words.get(s);
            if (w != null)
                return w; // palavra já existe na HashTable
            w = new Word(s, Tag.ID);
            words.put(s, w);
            return w;
        }
        
        this.erroTokenNaoEncontrado();
        return null;
    }
}