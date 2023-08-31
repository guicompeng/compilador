public abstract class Token {
    private final Tag tag;

    public Token(Tag t) {
        tag = t;
    }

    public String toString() {
        return "" + tag;
    }

    public Tag getToken() {
        return tag;
    }

    public abstract String getLexeme();
}