package src;

import java.io.IOException;

public class Parser {
    final int IF = 1, THEN = 2, ELSE = 3, BEGIN = 4, END = 5, PRINT = 6, SEMI = 7, NUM = 8, EQ = 9;

    private static Lexer lexer;
    private static Token tok;
    private static SymbolTable symbolTable;
    private static IDTypes curType = null;
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

    private static void semanticError(String causedBy) throws IOException {
        System.out.println("Erro semântico na linha " + lexer.line);
        System.out.println("Causado por: " + causedBy);
        System.exit(0);
    }

    private static void checkIdWasDeclared(String lexemeId) throws IOException {
        RowSymbolTable rst = symbolTable.findRow(lexemeId);
        if(rst == null) {
            semanticError("\"" + lexemeId + "\"" + " não foi declarado");
        }
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
        // inserir identificador na tabela de simbolos
        RowSymbolTable rst = new RowSymbolTable(tok.getLexeme(), symbolTable.getCountLevel(), curType);
        symbolTable.insertRowSymbolTable(rst);

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
                curType = IDTypes.INT;
                break;
            case STRING:
                eat(Tag.STRING);
                curType = IDTypes.STRING;
                break;
            case FLOAT:
                eat(Tag.FLOAT);
                curType = IDTypes.FLOAT;
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
        checkIdWasDeclared(tok.getLexeme());
        RowSymbolTable rst = symbolTable.findRow(tok.getLexeme());

        eat(Tag.ID);
        eat(Tag.ASSIGN);
        IDTypes typeSimpleExprt = simpleExpr();
        // check se a variavel na esquerda (antes do = ) é igual ao tipo resultante na direita (simpleExpr)
        if(rst.getType() != typeSimpleExprt) {
            semanticError("Tipos incompatíveis. A variável " + rst.getLexeme() + " foi declarada com o tipo " + rst.getType() + " mas o tipo a direita do '=' é " + typeSimpleExprt);
        }
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
        checkIdWasDeclared(tok.getLexeme());
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
    private static IDTypes expression() throws IOException {
        IDTypes localType = simpleExpr();
        Tag t = tok.getToken();
        // first do relop: ">" | ">=" | "<" | "<=" | "!=" | "=="
        if (t == Tag.GREATER || t == Tag.GREATER_EQUAL || t == Tag.LESS || t == Tag.LESS_EQUAL || t == Tag.NOT_EQUAL
                || t == Tag.EQUAL) {
            relop();
            simpleExpr();
            // As operações de comparação resultam em valor lógico (verdadeiro ou falso)
            localType = IDTypes.BOOLEAN;
        }
        return localType;
    }

    // simple-expr ::= term simple-expr-aux
    private static IDTypes simpleExpr() throws IOException {     
        IDTypes localType = term();
        IDTypes localType2 = simpleExprAux();
        if(localType2 != null && localType != localType2) {
            semanticError("Há tipos incompatíveis nessa expressão (" + localType + " com " + localType2 + ")");
        }
        return localType;
    }

    // simple-expr-aux ::= addop term simple-expr-aux | λ
    private static IDTypes simpleExprAux() throws IOException {
        IDTypes resultType = null;
        Tag t = tok.getToken();
        if (t == Tag.OP_SUM || t == Tag.OP_SUB || t == Tag.OR) { // first do addop é: "+" | "-" | "||"
            addop();
            IDTypes localType = term();
            IDTypes localType2 = simpleExprAux();
            if(localType2 != null && localType != localType2) {
                semanticError("Há tipos incompatíveis nessa expressão (" + localType + " com " + localType2 + ")");
            }
            resultType = localType;
        }
        return resultType;
    }

    private static class ReducaoTermAux {
        public IDTypes type;
        public Tag op;
        public ReducaoTermAux(IDTypes type, Tag op) {
            this.type = type;
            this.op = op;
        }
    }

    // term ::= factor-a term-aux
    private static IDTypes term() throws IOException {
        IDTypes localType = factorA();
        ReducaoTermAux localReducao = termAux();
        if(localReducao.type != null && localType != localReducao.type) {
            semanticError("Há tipos incompatíveis nessa expressão (" + localType + " com " + localReducao.type + ")");
        }
        // se for divisao, resultado é float, se "&&" é booleano
        IDTypes resultType = null;
        if(localReducao.op == Tag.OP_DIV) {
            resultType = IDTypes.FLOAT;
        } else if(localReducao.op == Tag.AND) {
            resultType = IDTypes.BOOLEAN;
        } else {
            resultType = localType;
        }
        return resultType;
    }

    // term-aux ::= mulop factor-a term-aux | λ
    private static ReducaoTermAux termAux() throws IOException {
        IDTypes resultType = null;
        Tag t = tok.getToken();
        if (t == Tag.OP_MUL || t == Tag.OP_DIV || t == Tag.AND) { // first do mulop é: "*" | "/" | "&&"
            mulop();
            IDTypes localType = factorA();
            ReducaoTermAux localReducao = termAux();
            if(localReducao.type != null && localType != localReducao.type) {
                semanticError("Há tipos incompatíveis nessa expressão (" + localType + " com " + localReducao.type + ")");
            }
            // se for divisao, resultado é float, se "&&" é booleano
            if(localReducao.op == Tag.OP_DIV) {
                resultType = IDTypes.FLOAT;
            } else if(localReducao.op == Tag.AND) {
                resultType = IDTypes.BOOLEAN;
            } else {
                resultType = localType;
            }
        }
        return new ReducaoTermAux(resultType, t);
    }

    // factor-a ::= factor | "!" factor | "-" factor
    private static IDTypes factorA() throws IOException {
        Tag t = tok.getToken();
        if (t == Tag.NOT || t == Tag.OP_SUB) {
            advance();
        }
        return factor();
    }

    // factor ::= identifier | constant | "(" expression ")"
    private static IDTypes factor() throws IOException {
        switch (tok.getToken()) {
            case ID:
                checkIdWasDeclared(tok.getLexeme());
                IDTypes localType = symbolTable.findRow(tok.getLexeme()).getType();
                eat(Tag.ID);
                return localType;
            // const pode ser int, float ou literal
            case INT:
                advance();
                return IDTypes.INT;
            case FLOAT:
                advance();
                return IDTypes.FLOAT;
            case LITERAL:
                advance();
                return IDTypes.STRING;
            case OPEN_ROUND_BRACKET:
                eat(Tag.OPEN_ROUND_BRACKET);
                IDTypes localAuxType = expression();
                eat(Tag.CLOSE_ROUND_BRACKET);
                return localAuxType;
            default:
                error();
                return IDTypes.INT; // nao faz diferenca esse return, pois o error() ja vai encerrar o programa
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
