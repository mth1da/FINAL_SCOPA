package fr.pantheonsorbonne.miage.enums;

/**
 * List the possible colors of a card
 */
public enum CardColor {
    SPADE(127137),
    HEART(127137 + 16), 
    DIAMOND(127137 + 16 * 2), 
    CLUB(127137 + 16 * 3); 

    private final int code;

    CardColor(int code) {
        this.code = code;
    }

    public static CardColor valueOfStr(String substring) {
        for (CardColor color : CardColor.values()) {
            if (color.name().substring(0, 1).equals(substring)) {
                return color;
            }
        }
        throw new RuntimeException("No Such Color");
    }

    public int getCode() {
        return code;
    }

    public String getStringRepresentation() {
        return "" + this.name().charAt(0);
    }
}