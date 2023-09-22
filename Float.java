public class Float extends Token {
    public final double value;

    public Float(double value) {
        super(Tag.FLOAT);
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