public enum Tag {
    INVALID_TOKEN,
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
    SEMICOLON,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    ASSIGN,
    NOT,
    LESS,
    GREATER,
    EQUAL,
    NOT_EQUAL,
    LESS_EQUAL,
    GREATER_EQUAL,
    OP_DIVISION,

    // Outros tokens
    ID,
}