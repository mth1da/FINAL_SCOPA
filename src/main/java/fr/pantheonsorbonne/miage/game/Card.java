package fr.pantheonsorbonne.miage.game;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;

import java.util.Arrays;
import java.util.stream.Collectors;

public class Card {

    private final CardColor color;
    private final CardValue value;


    public Card(CardColor color, CardValue value) {
        this.color = color;
        this.value = value;
    }

    /**
     * For a String representation of a card, return the card
     *
     * @param str
     * @return the card
     * @throws RuntimeException if the String representation is Invaliid
     */
    public static Card valueOf(String str) {
        CardValue value;
        CardColor color;
        if (str.length() == 3) {//it's a 10
            value = CardValue.valueOfStr(str.substring(0, 2));
            color = CardColor.valueOfStr(str.substring(2, 3));
        } else {
            value = CardValue.valueOfStr(str.substring(0, 1));
            color = CardColor.valueOfStr(str.substring(1, 2));
        }
        return new Card(color, value);

    }

    public static String cardsToString(Card[] cards) {
        return Arrays.stream(cards).map(Card::toString).collect(Collectors.joining(";"));
    }

    public static Card[] stringToCards(String cards) {
        if (cards.isEmpty()) {
            return new Card[0];
        }
        return (Card[]) Arrays.stream(cards.split(";")).map(Card::valueOf).toArray(Card[]::new);
    }


    /**
     * Get the color of the card
     *
     * @return
     */
    public CardColor getColor() {
        return color;
    }

    @Override
    public String toString() {
        return this.getValue().getStringRepresentation() + this.getColor().getStringRepresentation();
    }

    public String toFancyString() {

        int rank = this.getValue().ordinal();
        if (rank > 6) {
            rank=rank+4;
        }
        return new String(Character.toChars(this.color.getCode() + rank));
    }

    /**
     * get the value of the card
     *
     * @return
     */
    public CardValue getValue() {
        return value;
    }
}
