package fr.pantheonsorbonne.miage.game;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;

import java.util.Random;

/**
 * Represents a Deck of cards
 */
public class Deck {

    //private final static Random random = new Random();
    public static int deckSize = CardValue.values().length * CardColor.values().length;
    private static final Card[] deck = new Card[deckSize];
    private static Random random=new Random();

    static {
        int cardCount = deckSize;
        //generate all cards
        for (CardColor color : CardColor.values()) {
            for (CardValue value : CardValue.values()) {
                deck[cardCount-- - 1] = new Card(color, value);
            }
        }
        //shuffle them
        
        for (int i = 0; i < deckSize; i++) {
            int randomIndexToSwap = random.nextInt(deckSize);
            Card temp = deck[randomIndexToSwap];
            deck[randomIndexToSwap] = deck[i];
            deck[i] = temp;

        }
    }

    /**
     * disallow instantiation
     */
    private Deck() {
    }



    /**
     * return an array of random cards
     *
     * @param length the size of the array
     * @return
     */
    public static Card[] getRandomCards(int length) {
        Card[] result = new Card[length];
        for (int i = 0; i < length; i++) {
            result[i] = newRandomCard();
        }
        return result;
    }

    private static Card newRandomCard() {
        return deck[deckSize-- - 1];
    }
}
