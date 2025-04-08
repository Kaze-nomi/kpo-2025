package hse.bank.builders;

public record Report(String title, String content) {

    @Override
    public String toString() {
        return String.format("%s\n\n%s\n", title, content);
    }
}