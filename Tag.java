public enum Tag {
    INVALID_TOKEN,
    END_OF_FILE,
    AND,
    OR,
    TRUE,
    FALSE,
    IF,
    ELSE,
    // Palavras reservadas
    PRG,
    BEG,
    END,
    TYPE,
    INT,
    CHAR,
    BOOL,

    // Operadores e pontuação
    EQ,
    GE,
    LE,
    NE,

    // Outros tokens
    NUM,
    ID,
}