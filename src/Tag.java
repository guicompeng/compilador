package src;
public enum Tag {
    INVALID_TOKEN,
    UNEXPECTED_EOF,
    END_OF_FILE,
    AND,
    OR,
    IF,
    ELSE,

    // Palavras reservadas
    CLASS,
    INT,
    STRING,
    FLOAT,
    DO,
    WHILE,
    READ,
    WRITE,

    // Operadores e pontuação
    SEMICOLON, // ;
    COMMA, // ,
    OPEN_ROUND_BRACKET, // (
    CLOSE_ROUND_BRACKET, // )
    OPEN_CURLY_BRACKET, // {
    CLOSE_CURLY_BRACKET, // }
    ASSIGN,
    NOT, // !
    LESS,
    GREATER,
    EQUAL,
    NOT_EQUAL,
    LESS_EQUAL,
    GREATER_EQUAL,
    OP_SUM,
    OP_SUB,
    OP_MUL,
    OP_DIV,

    // Outros tokens
    ID,
    LITERAL,
}