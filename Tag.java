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

    // Operadores e pontuação
    EQ,
    GE,
    LE,
    NE,

    // Outros tokens
    ID,
}