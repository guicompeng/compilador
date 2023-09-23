public class Word extends Token {
    private String lexeme = "";
    public static final Word EOF = new Word("", Tag.END_OF_FILE);
    public static final Word AND = new Word("&&", Tag.AND);
    public static final Word OR = new Word("||", Tag.OR);
    public static final Word SEMICOLON = new Word(";", Tag.SEMICOLON);
    public static final Word OPEN_BRACKET = new Word("{", Tag.OPEN_BRACKET);
    public static final Word CLOSE_BRACKET = new Word("}", Tag.CLOSE_BRACKET);
    public static final Word ASSIGN = new Word("=", Tag.ASSIGN);
    public static final Word NOT = new Word("!", Tag.NOT);
    public static final Word LESS = new Word("<", Tag.LESS);
    public static final Word GREATER = new Word(">", Tag.GREATER);
    public static final Word EQUAL = new Word("==", Tag.EQUAL);
    public static final Word NOT_EQUAL = new Word("!=", Tag.NOT_EQUAL);
    public static final Word LESS_EQUAL = new Word("<=", Tag.LESS_EQUAL);
    public static final Word GREATER_EQUAL = new Word(">=", Tag.GREATER_EQUAL);

    public static final Word OP_DIVISION = new Word("/", Tag.OP_DIVISION);

    public Word(String s, Tag tag) {
        super(tag);
        lexeme = s;
    }

    public String toString() {
        return "" + lexeme;
    }

    @Override
    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }
}