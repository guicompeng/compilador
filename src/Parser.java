package src;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Parser {
    final int IF = 1, THEN = 2, ELSE = 3, BEGIN = 4, END = 5, PRINT = 6, SEMI = 7, NUM = 8, EQ = 9;

    private static Lexer lexer;
    private static Token tok;
    private static SymbolTable symbolTable;
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Por favor, forneça o nome do arquivo como parâmetro.");
        } else {
            String fileName = args[0];
            lexer = new Lexer(fileName);
            symbolTable = new SymbolTable();
            advance();
            program();
            System.out.println("Sintático: ok");
        }

    }

    private static void error() throws IOException {
        System.out.println("Erro sintático na linha " + lexer.line);
        System.out.println("Causado por: lexema não esperado: " + tok.getLexeme());
        System.exit(0);
    }

    private static void advance() throws IOException {
        tok = lexer.scan(); // lê próximo token
    }

    private static void eat(Tag t) throws IOException {
        if (tok.getToken() == t)
            advance();
        else
            error();
    }

    private static void program() throws IOException {
        programAux();
        eat(Tag.END_OF_FILE);
    }

    // class identifier [decl-list] body
    private static void programAux() throws IOException {
        eat(Tag.CLASS);
        eat(Tag.ID);
        if (tok.getToken() != Tag.OPEN_CURLY_BRACKET) {
            declList();
        }
        body();
    }

    // decl ";" { decl ";"}
    private static void declList() throws IOException {
        decl();
        eat(Tag.SEMICOLON);
        if (tok.getToken() != Tag.OPEN_CURLY_BRACKET) { // follow (inicio do body): {
            declList();
        }
    }

    // type ident-list
    private static void decl() throws IOException {
        type();
        identList();
    }

    // identifier {"," identifier}
    private static void identList() throws IOException {
        eat(Tag.ID);
        if (tok.getToken() == Tag.COMMA) {
            eat(Tag.COMMA);
            identList();
        }
    }

    // int | string | float
    private static void type() throws IOException {
        switch (tok.getToken()) {
            case INT:
                eat(Tag.INT);
                break;
            case STRING:
                eat(Tag.STRING);
                break;
            case FLOAT:
                eat(Tag.FLOAT);
                break;
            default:
                error();
        }
    }

    // "{" stmt-list "}"
    private static void body() throws IOException {
        eat(Tag.OPEN_CURLY_BRACKET);
        stmtList();
        eat(Tag.CLOSE_CURLY_BRACKET);
    }

    // stmt ";" { stmt ";" }
    private static void stmtList() throws IOException {
        stmt();
        eat(Tag.SEMICOLON);
        if (tok.getToken() != Tag.CLOSE_CURLY_BRACKET) { // follow: }
            stmtList();
        }
    }

    // = assign-stmt | if-stmt | do-stmt | read-stmt | write-stmt
    private static void stmt() throws IOException {
        switch (tok.getToken()) {
            case ID: // assign-stmt
                assignStmt();
                break;
            case IF: // if-stmt
                ifStmt();
                break;
            case DO: // do-stmt
                doStmt();
                break;
            case READ: // read-stmt
                readStmt();
                break;
            case WRITE: // write-stmt
                writeStmt();
                break;
            default:
                error();
        }
    }

    // identifier "=" simple-expr
    private static void assignStmt() throws IOException {
        eat(Tag.ID);
        eat(Tag.ASSIGN);
        simpleExpr();
    }

    // if-stmt ::= if "(" condition ")" "{" stmt-list "}" else-stmt
    private static void ifStmt() throws IOException {
        eat(Tag.IF);
        eat(Tag.OPEN_ROUND_BRACKET);
        symbolTable.blockInput();  // entrada do bloco: atualizar nível da tabela de simbolos
        condition();
        eat(Tag.CLOSE_ROUND_BRACKET);
        symbolTable.blockOutput();  // saida do bloco: atualizar nível da tabela de simbolos
        eat(Tag.OPEN_CURLY_BRACKET);
        stmtList();
        eat(Tag.CLOSE_CURLY_BRACKET);
        elseStmt();
    }

    // else-stmt ::= else "{" stmt-list "}" | λ
    private static void elseStmt() throws IOException {
        if (tok.getToken() == Tag.ELSE) {
            eat(Tag.ELSE);
            eat(Tag.OPEN_CURLY_BRACKET);
            symbolTable.blockInput();  // entrada do bloco: atualizar nível da tabela de simbolos
            stmtList();
            eat(Tag.CLOSE_CURLY_BRACKET);
            symbolTable.blockOutput();  // saida do bloco: atualizar nível da tabela de simbolos
        }
    }

    // condition ::= expression
    private static void condition() throws IOException {
        expression();
    }

    // do-stmt ::= do "{" stmt-list "}" do-suffix
    private static void doStmt() throws IOException {
        eat(Tag.DO);
        eat(Tag.OPEN_CURLY_BRACKET);
        symbolTable.blockInput();  // entrada do bloco: atualizar nível da tabela de simbolos
        stmtList();
        eat(Tag.CLOSE_CURLY_BRACKET);
        symbolTable.blockOutput();  // saida do bloco: atualizar nível da tabela de simbolos
        doSuffix();
    }

    // do-suffix ::= while "(" condition ")"
    private static void doSuffix() throws IOException {
        eat(Tag.WHILE);
        eat(Tag.OPEN_ROUND_BRACKET);
        condition();
        eat(Tag.CLOSE_ROUND_BRACKET);
    }

    // read-stmt ::= read "(" identifier ")"
    private static void readStmt() throws IOException {
        eat(Tag.READ);
        eat(Tag.OPEN_ROUND_BRACKET);
        eat(Tag.ID);
        eat(Tag.CLOSE_ROUND_BRACKET);
    }

    // write-stmt ::= write "(" writable ")"
    private static void writeStmt() throws IOException {
        eat(Tag.WRITE);
        eat(Tag.OPEN_ROUND_BRACKET);
        writable();
        eat(Tag.CLOSE_ROUND_BRACKET);
    }

    // writable ::= simple-expr
    private static void writable() throws IOException {
        simpleExpr();
    }

    // expression ::= simple-expr | simple-expr relop simple-expr
    private static void expression() throws IOException {
        simpleExpr();
        Tag t = tok.getToken();
        // first do relop: ">" | ">=" | "<" | "<=" | "!=" | "=="
        if (t == Tag.GREATER || t == Tag.GREATER_EQUAL || t == Tag.LESS || t == Tag.LESS_EQUAL || t == Tag.NOT_EQUAL
                || t == Tag.EQUAL) {
            relop();
            System.out.println("a?" + tok.getToken());
            simpleExpr();
            System.out.println("b?" + tok.getToken());
        }
    }

    // simple-expr ::= term simple-expr-aux
    private static void simpleExpr() throws IOException {
        term();
        simpleExprAux();
    }

    // simple-expr-aux ::= addop term simple-expr-aux | λ
    private static void simpleExprAux() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.OP_SUM || t == Tag.OP_SUB || t == Tag.OR) { // first do addop é: "+" | "-" | "||"
            addop();
            term();
            simpleExprAux();
        }
    }

    // term ::= factor-a term-aux
    private static void term() throws IOException {
        factorA();
        termAux();
    }

    // term-aux ::= mulop factor-a term-aux | λ
    private static void termAux() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.OP_MUL || t == Tag.OP_DIV || t == Tag.AND) { // first do mulop é: "*" | "/" | "&&"
            mulop();
            factorA();
            termAux();
        }
    }

    // factor-a ::= factor | "!" factor | "-" factor
    private static void factorA() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.NOT || t == Tag.OP_SUB) {
            advance();
        }
        factor();
    }

    // factor ::= identifier | constant | "(" expression ")"
    private static void factor() throws IOException {
        switch (tok.getToken()) {
            case ID:
                eat(Tag.ID);
                break;
            // const pode ser int, float ou literal
            case INT:
            case FLOAT:
            case LITERAL:
                advance();
                break;
            case OPEN_ROUND_BRACKET:
                eat(Tag.OPEN_ROUND_BRACKET);
                expression();
                eat(Tag.CLOSE_ROUND_BRACKET);
                break;
            default:
                error();
        }
    }

    // relop ::= ">" | ">=" | "<" | "<=" | "!=" | "=="
    private static void relop() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.GREATER || t == Tag.GREATER_EQUAL || t == Tag.LESS || t == Tag.LESS_EQUAL || t == Tag.NOT_EQUAL
                || t == Tag.EQUAL) {
            advance();
        } else {
            error();
        }
    }

    // addop ::= "+" | "-" | "||"
    private static void addop() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.OP_SUM || t == Tag.OP_SUB || t == Tag.OR) {
            advance();
        } else {
            error();
        }
    }

    // mulop ::= "*" | "/" | "&&"
    private static void mulop() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.OP_MUL || t == Tag.OP_DIV || t == Tag.AND) {
            advance();
        } else {
            error();
        }
    }
}
