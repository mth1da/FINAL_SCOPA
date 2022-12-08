package fr.pantheonsorbonne.miage.enums;

public enum CardValue {
    ACE("1", 1),
    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    JACK("J", 8),
    QUEEN("Q", 9),
    KING("K", 10);

    final private String stringRepresentation;
    final private int rank;

    CardValue(String stringRepresentation, int value) {
        this.stringRepresentation = stringRepresentation;
        this.rank = value;
    }

    public static CardValue valueOfStr(String str) {
        for (CardValue value : CardValue.values()) {
            if (str.equals(value.getStringRepresentation())) {
                return value;
            }
        }

        throw new RuntimeException("failed to parse value");

    }

    public String getStringRepresentation() {
        return stringRepresentation;
    }

    public int getRank() {
        return rank;
    }
}
