package fr.pantheonsorbonne.miage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;

public class CardTest {
    @Test
    void getValueTest() {
        assertEquals(CardValue.ACE, Card.valueOf("1S").getValue());
    }

    @Test
    void getColorTest() {
        assertEquals(CardColor.DIAMOND, Card.valueOf("1D").getColor());
    }

    @Test
    void valueOfTest() {
        assertEquals(CardValue.SIX, Card.valueOf("6D").getValue());
        assertEquals(CardColor.DIAMOND, Card.valueOf("6D").getColor());

        assertEquals(CardValue.QUEEN, Card.valueOf("QC").getValue());
        assertEquals(CardColor.CLUB, Card.valueOf("QC").getColor());
    }

    @Test
    void cardsToStringTest() {
        {
            Card[] cardsTest = new Card[1];
            cardsTest[0] = new Card(CardColor.CLUB, CardValue.ACE);
            assertEquals("1C", Card.cardsToString(cardsTest));
        }
        {
            Card[] cardsTest = new Card[1];
            cardsTest[0] = new Card(CardColor.HEART, CardValue.SIX);
            assertEquals("6H", Card.cardsToString(cardsTest));
        }
    }
    
}
