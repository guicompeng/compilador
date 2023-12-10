package src;

public class RowSymbolTable {


    private String lexeme;
    private int level;
    private IDTypes type;

    public RowSymbolTable(String lexeme, int level, IDTypes type) {
        this.lexeme = lexeme;
        this.level = level;
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public IDTypes getType() {
        return type;
    }

    public void setType(IDTypes type) {
        this.type = type;
    }
}