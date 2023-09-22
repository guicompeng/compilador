public class Int extends Token {
    public final int value;

    public Int(int value) {
        super(Tag.INT);
        this.value = value;
    }

    public String toString() {
        return "" + value;
    }

    @Override
    public String getLexeme() {
        return String.valueOf(value);
    }
}