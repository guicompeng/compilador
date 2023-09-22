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

        while(EOF == false && ch != '\n' && ch != ' ' && ch != '\t') {
            lexemaInvalido += ch;
            readch();
        }
        System.out.println("Erro léxico na linha " + line + ". Lexema inválido: " + lexemaInvalido);
        if(EOF) return Word.EOF;
        else return new Word(lexemaInvalido, Tag.INVALID_TOKEN);
    }

    public Token scan() throws IOException {
        // Se for fim de arquivo
        if(EOF) return Word.EOF;
        
        // Desconsidera delimitadores na entrada
        for (;; readch()) {
            if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\b')
                continue;
            else if (ch == '\n')
                line++; // conta linhas
            else
                break;
        }

        // Se for fim de arquivo
        if(EOF) return Word.EOF;

        switch (ch) {
            // Operadores
            case '&':
                if (readch('&')) {
                    return Word.and;
                } else {
                    return this.erroTokenNaoEncontrado();
                }
            case '|':
                if (readch('|'))
                    return Word.or;
                else {
                    return this.erroTokenNaoEncontrado();
                }
            case '=':
                if (readch('='))
                    return Word.eq;
                else {
                    return this.erroTokenNaoEncontrado();
                }

            case '<':
                if (readch('='))
                    return Word.le;
                else {
                    return this.erroTokenNaoEncontrado();
                }
            case '>':
                if (readch('='))
                    return Word.ge;
                else {
                    return this.erroTokenNaoEncontrado();
                }
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