package fr.pantheonsorbonne.miage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
//import java.util.stream.Collectors;

import fr.pantheonsorbonne.miage.enums.CardColor;
import fr.pantheonsorbonne.miage.enums.CardValue;
import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.exception.NoSuchPlayerException;
import fr.pantheonsorbonne.miage.exception.TotalCollectedCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;

class EngineTest extends LocalScopa {

    Queue<Card> initialRoundDeckTest = new LinkedList<>();
    Queue<Card> roundDeckTest = new LinkedList<>();
    Queue<Card> playerCardsTest = new LinkedList<>();
    ArrayList<Card> pairCardsTest = new ArrayList<>();
    Map<String, Queue<Card>> playerCollectedCardsTest = new HashMap<>();
    Queue<Card> J1collectedCards = new LinkedList<>();
    Queue<Card> J2collectedCards = new LinkedList<>();
    Queue<Card> J3collectedCards = new LinkedList<>();
    Map<String, Integer> playerCollectedScopaTest = new HashMap<>();
    Queue<String> playersTest = new LinkedList<>();
    boolean activateSpecialDeck = false;

    public EngineTest() {
        super(Set.of("Joueur1", "Joueur2", "Joueur3"));
    }

    protected int getDeckSize() {
        if (!activateSpecialDeck)
            return Deck.deckSize;
        else
            return getSpecialDeckSize();
    }

    protected Card[] getDeckRandomCards(int n) {
        if (!activateSpecialDeck)
            return Deck.getRandomCards(n);
        else
            return getSpecialDeckRandomCards(n);
    }

    @Test
    void getInitialRoundDeckTest() {
        assertEquals(4, getInitialRoundDeck().size());
    }

    /*
     * tests on the initial round deck
     */
    @Test
    void checkGameResetIfThreeSameCardValue() {

        initialRoundDeckTest.add(new Card(CardColor.CLUB, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.SPADE, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));

        ScopaEngine test = new LocalScopa(Set.of());
        assertEquals(true, test.checkOverThreeSameCardValue(initialRoundDeckTest));
    }

    @Test
    void checkGameResetIfFourSameCardValue() {

        initialRoundDeckTest.add(new Card(CardColor.CLUB, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.SPADE, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));

        ScopaEngine test = new LocalScopa(Set.of());
        assertEquals(true, test.checkOverThreeSameCardValue(initialRoundDeckTest));
    }

    @Test
    void checkGameContinuesIfTwoSameCardValue() {

        initialRoundDeckTest.add(new Card(CardColor.CLUB, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.SPADE, CardValue.THREE));
        initialRoundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.HEART, CardValue.TWO));

        ScopaEngine test = new LocalScopa(Set.of());
        assertEquals(false, test.checkOverThreeSameCardValue(initialRoundDeckTest));
    }

    @Test
    void checkGameContinuesIfNoSameCardValue() {

        initialRoundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        initialRoundDeckTest.add(new Card(CardColor.SPADE, CardValue.THREE));
        initialRoundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        initialRoundDeckTest.add(new Card(CardColor.HEART, CardValue.TWO));

        ScopaEngine test = new LocalScopa(Set.of());
        assertEquals(false, test.checkOverThreeSameCardValue(initialRoundDeckTest));
    }

    @Test
    void giveCardsToPlayer() {
        var test = new LocalScopa(Set.of("Joueur1"));
        playerCardsTest.add(new Card(CardColor.CLUB, CardValue.THREE));
        test.giveCardsToPlayer("Joueur1", "3C");
        boolean result = !playerCardsTest.isEmpty();
        assertTrue(result);
    }

    /*
     * @Test
     * void getCardFromPlayerTest throws NoMoreCardException(){
     * var test = new LocalScopa(Set.of("Joueur1"));
     * playerCardsTest.add(new Card(CardColor.CLUB, CardValue.THREE));
     * 
     * assertEquals("3C",test.getCardFromPlayer("Joueur1"));
     * }
     */

    /*
     * 
     */
    void noCardsWithPlayersTest() {

    }

    /*
     * tests on making a pair
     */
    @Test
    void makePairTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.FOUR));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.CLUB, CardValue.THREE));
        playerCardsTest.add(new Card(CardColor.HEART, CardValue.FOUR));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        String carteAttendue = "4H";
        Card carteObtenue = (test.makePair(playerCardsTest, roundDeckTest)).get(0);

        assertEquals(carteAttendue, carteObtenue.toString());
    }

    @Test
    void makePairWithSettebelloInDeckTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("7H", (test.makePair(playerCardsTest, roundDeckTest)).get(0).toString());
    }

    @Test
    void makePairWithSettebelloInHandTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("7D", test.makePair(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void makePairWithDenierInDeckTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.FIVE));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("5S", test.makePair(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void makePairWithDenierInHandTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.FIVE));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("1D", test.makePair(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void cantMakePairTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.FOUR));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.CLUB, CardValue.THREE));
        playerCardsTest.add(new Card(CardColor.HEART, CardValue.QUEEN));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals(true, test.makePair(playerCardsTest, roundDeckTest).isEmpty());
    }

    @Test
    void makePairWithoutSettebelloAndDenierTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.CLUB, CardValue.THREE));
        playerCardsTest.add(new Card(CardColor.HEART, CardValue.FOUR));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        String carteAttendue = "KS";
        Card carteObtenue = test.makePair(playerCardsTest, roundDeckTest).get(0);

        assertEquals(carteAttendue, carteObtenue.toString());
    }

    /*
     * tests on settebello strategy when the settebello is in the round deck
     */
    @Test
    void makingPairWithSettebelloInDeckStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("7H", test.settebelloInDeckStrategy(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void cantMakePairWithSettebelloInDeckStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.THREE));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals(true, test.settebelloInDeckStrategy(playerCardsTest, roundDeckTest).isEmpty());
    }

    /*
     * tests on settebello strategy when the settebello is in the player's hand
     */
    @Test
    void makingPairWithSettebelloInHandStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("7D", test.settebelloInHandStrategy(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void cantMakePairWithSettebelloInHandStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.THREE));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals(true, test.settebelloInDeckStrategy(playerCardsTest, roundDeckTest).isEmpty());
    }

    /*
     * tests on the denier strategy when there's a denier card in the deck
     */
    @Test
    void makingPairWithDenierInDeckStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.FIVE));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("5S", test.denierCardInDeckStrategy(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void cantMakePairWithDenierInDeckStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals(true, test.denierCardInDeckStrategy(playerCardsTest, roundDeckTest).isEmpty());
    }

    /*
     * tests on the denier strategy when the denier card is in the player's hand
     */
    @Test
    void makingPairWithDenierInHandStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.ACE));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.FIVE));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("1D", test.denierCardInHandStrategy(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void cantMakePairWithDenierInHandStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.TWO));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SEVEN));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals(true, test.denierCardInHandStrategy(playerCardsTest, roundDeckTest).isEmpty());
    }

    @Test
    void noStrategyTest() {

        roundDeckTest.add(new Card(CardColor.HEART, CardValue.TWO));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        playerCardsTest.add(new Card(CardColor.HEART, CardValue.SIX));
        playerCardsTest.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        playerCardsTest.add(new Card(CardColor.SPADE, CardValue.TWO));

        var test = new LocalScopa(Set.of("Joueur1"));

        assertEquals("6H", test.noStrategy(playerCardsTest, roundDeckTest).get(0).toString());
    }

    @Test
    void processPairCardsTest() throws NoSuchPlayerException {

        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.TWO));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        // the player's selected card
        pairCardsTest.add(new Card(CardColor.HEART, CardValue.TWO));
        // the pair of the deck
        pairCardsTest.add(new Card(CardColor.SPADE, CardValue.TWO));

        var test = new LocalScopa(Set.of("Joueur1"));
        test.initCollectedAndScopaCards();
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.TWO));
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.TWO));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);

        String expected = playerCollectedCardsTest.get("Joueur1").toString();
        String actual = test.processPairCards("Joueur1", pairCardsTest, roundDeckTest).get("Joueur1").toString();

        assertEquals(expected, actual);
    }

    /*
     * tests on whether a point for a scopa is given or not
     */
    @Test
    void processAScopaPointTest() {

        // the player's selected card
        pairCardsTest.add(new Card(CardColor.DIAMOND, CardValue.KING));
        // the pair of the deck
        pairCardsTest.add(new Card(CardColor.HEART, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));
        test.initCollectedAndScopaCards();
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.KING));
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.KING));

        playerCollectedScopaTest.put("Joueur1", 1);

        int expected = playerCollectedScopaTest.get("Joueur1");
        int actual = test.processScopaPoint("Joueur1", roundDeckTest).get("Joueur1");

        assertEquals(expected, actual);
    }

    @Test
    void processNoScopaPointTest() {

        // there are still cards in the round deck
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.TWO));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.ACE));

        // the player's selected card
        pairCardsTest.add(new Card(CardColor.DIAMOND, CardValue.KING));
        // the pair of the deck
        pairCardsTest.add(new Card(CardColor.HEART, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1"));
        test.initCollectedAndScopaCards();
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.KING));
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.KING));

        playerCollectedScopaTest.put("Joueur1", 0);

        int expected = playerCollectedScopaTest.get("Joueur1");
        int actual = test.processScopaPoint("Joueur1", roundDeckTest).get("Joueur1");

        assertEquals(expected, actual);
    }

    /*
     * test
     */
    @Test
    void addRemainingCardsToTheLastPlayerCollectedCards() {

        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.TWO));
        roundDeckTest.add(new Card(CardColor.CLUB, CardValue.KING));
        roundDeckTest.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        roundDeckTest.add(new Card(CardColor.SPADE, CardValue.SIX));

        var test = new LocalScopa(Set.of("Joueur1"));
        test.initCollectedAndScopaCards();

        playersTest.offer("Joueur1");

        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.TWO));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.KING));
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.FIVE));
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.SIX));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);

        String expected = playerCollectedCardsTest.get("Joueur1").toString();
        String actual = test.addRemainingCardsToCollected(roundDeckTest, playersTest).get("Joueur1").toString();

        assertEquals(expected, actual);
    }

    /*
     * tests on bestCount method
     */
    @Test
    void onePlayerHavingBestCountTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.FIVE));
        J2collectedCards.add(new Card(CardColor.HEART, CardValue.FIVE));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));
        J2collectedCards.add(new Card(CardColor.CLUB, CardValue.QUEEN));

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2"));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);

        ArrayList<String> resultatAttendu = new ArrayList<>();
        resultatAttendu.add("Joueur2");
        ArrayList<String> resultatObtenu = test.bestCount(playerCollectedCardsTest);

        assertEquals(resultatAttendu, resultatObtenu);
    }

    @Test
    void twoPlayersHavingBestCountTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.KING));
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.KING));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.FIVE));
        J2collectedCards.add(new Card(CardColor.HEART, CardValue.FIVE));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));
        J2collectedCards.add(new Card(CardColor.CLUB, CardValue.QUEEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.ACE));
        J3collectedCards.add(new Card(CardColor.HEART, CardValue.ACE));

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        ArrayList<String> resultatAttendu = new ArrayList<>();
        resultatAttendu.add("Joueur2");
        resultatAttendu.add("Joueur1");
        ArrayList<String> resultatObtenu = test.bestCount(playerCollectedCardsTest);

        assertEquals(resultatAttendu, resultatObtenu);
    }

    /*
     * tests on mostDenierCount method
     */
    @Test
    void onePlayerHavingMostDenierCountTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.QUEEN));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.FOUR));
        J3collectedCards.add(new Card(CardColor.HEART, CardValue.FOUR));
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.KING));
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.KING));

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("Joueur2");

        assertEquals(expectedResult, test.mostDenierCount(playerCollectedCardsTest));
    }

    @Test
    void twoPlayersHavingMostDenierCountTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.ACE));
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.TWO));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.TWO));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.FOUR));
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.FOUR));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.QUEEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SIX));
        J3collectedCards.add(new Card(CardColor.HEART, CardValue.SIX));
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.KING));
        J3collectedCards.add(new Card(CardColor.DIAMOND, CardValue.KING));
        J3collectedCards.add(new Card(CardColor.DIAMOND, CardValue.JACK));
        J3collectedCards.add(new Card(CardColor.HEART, CardValue.JACK));

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        ArrayList<String> expectedResult = new ArrayList<>();
        expectedResult.add("Joueur3");
        expectedResult.add("Joueur2");

        assertEquals(expectedResult, test.mostDenierCount(playerCollectedCardsTest));
    }

    /*
     * tests on the settebello
     */
    @Test
    void havingSettebelloTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.HEART, CardValue.KING));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.ACE));

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        assertEquals("Joueur2", test.havingSettebello(playerCollectedCardsTest));
    }

    @Test
    void notHavingSettebelloTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.HEART, CardValue.KING));
        J2collectedCards.add(new Card(CardColor.HEART, CardValue.SEVEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.ACE));

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        assertEquals(null, test.havingSettebello(playerCollectedCardsTest));
    }

    @Test
    public void countPlayersScoreTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.ACE));
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.SEVEN));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.TWO));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.ACE));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        playerCollectedScopaTest.put("Joueur1", 1);
        playerCollectedScopaTest.put("Joueur2", 0);
        playerCollectedScopaTest.put("Joueur3", 0);

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));

        Map<String, Integer> playerScoresTest = new HashMap<>();
        playerScoresTest.put("Joueur1", 2);
        playerScoresTest.put("Joueur2", 2);
        playerScoresTest.put("Joueur3", 0);

        assertEquals(playerScoresTest, test.countPlayersScores(playerCollectedCardsTest, playerCollectedScopaTest));
    }

    @Test
    public void countPlayersScoreTestWith2BestCount() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.ACE));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.TWO));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.ACE));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        playerCollectedScopaTest.put("Joueur1", 1);
        playerCollectedScopaTest.put("Joueur2", 0);
        playerCollectedScopaTest.put("Joueur3", 2);

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));

        Map<String, Integer> playerScoresTest = new HashMap<>();
        playerScoresTest.put("Joueur1", 2);
        playerScoresTest.put("Joueur2", 3);
        playerScoresTest.put("Joueur3", 2);

        assertEquals(playerScoresTest, test.countPlayersScores(playerCollectedCardsTest, playerCollectedScopaTest));
    }

    @Test
    public void countPlayersScoreTestWith3mostDenierCount() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.ACE));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        playerCollectedScopaTest.put("Joueur1", 1);
        playerCollectedScopaTest.put("Joueur2", 0);
        playerCollectedScopaTest.put("Joueur3", 1);

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));

        Map<String, Integer> playerScoresTest = new HashMap<>();
        playerScoresTest.put("Joueur1", 3);
        playerScoresTest.put("Joueur2", 2);
        playerScoresTest.put("Joueur3", 2);

        assertEquals(playerScoresTest, test.countPlayersScores(playerCollectedCardsTest, playerCollectedScopaTest));
    }

    @Test
    public void countPlayersScoreTestWithHavingSettebello() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.HEART, CardValue.ACE));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.DIAMOND, CardValue.ACE));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        playerCollectedScopaTest.put("Joueur1", 0);
        playerCollectedScopaTest.put("Joueur2", 1);
        playerCollectedScopaTest.put("Joueur3", 0);

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));

        Map<String, Integer> playerScoresTest = new HashMap<>();
        playerScoresTest.put("Joueur1", 1);
        playerScoresTest.put("Joueur2", 3);
        playerScoresTest.put("Joueur3", 1);

        assertEquals(playerScoresTest, test.countPlayersScores(playerCollectedCardsTest, playerCollectedScopaTest));
    }

    @Test
    public void getOneWinnerTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.DIAMOND, CardValue.SEVEN));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.QUEEN));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.FOUR));
        J3collectedCards.add(new Card(CardColor.HEART, CardValue.FOUR));
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.KING));
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.KING));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));

        assertEquals("Joueur1", test.getWinner(playerCollectedCardsTest));
    }

    @Test
    public void getNoWinnerTest() {

        // Joueur1's collected cards
        J1collectedCards.add(new Card(CardColor.SPADE, CardValue.SEVEN));
        J1collectedCards.add(new Card(CardColor.CLUB, CardValue.SEVEN));

        // Joueur2's collected cards
        J2collectedCards.add(new Card(CardColor.SPADE, CardValue.QUEEN));
        J2collectedCards.add(new Card(CardColor.DIAMOND, CardValue.QUEEN));

        // Joueur3's collected cards
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.FOUR));
        J3collectedCards.add(new Card(CardColor.HEART, CardValue.FOUR));
        J3collectedCards.add(new Card(CardColor.CLUB, CardValue.KING));
        J3collectedCards.add(new Card(CardColor.SPADE, CardValue.KING));

        playerCollectedCardsTest.put("Joueur1", J1collectedCards);
        playerCollectedCardsTest.put("Joueur2", J2collectedCards);
        playerCollectedCardsTest.put("Joueur3", J3collectedCards);

        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));

        assertEquals("nobody", test.getWinner(playerCollectedCardsTest));
    }

    @Test
    void declareWinner() {
        var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
        test.declareWinner("Joueur1");
    }

    /*
     * @Test
     * void playTest() throws TotalCollectedCardException, NoSuchPlayerException{
     * var test = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3"));
     * test.play();
     * }
     */
/* 
    @Override
    protected Queue<Card> getPlayerCards(String playerName) throws NoSuchPlayerException {
        return null;
    }

    @Override
    protected void declareWinner(String winner) {
    }

    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
    }

    @Override
    protected Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer,
            String cardProviderPlayerOpponent) {
        return null;
    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> cards, String playerName) {
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {
        return null;
    }
*/
    protected int getSpecialDeckSize() {
        return specialCards.size();
    }

    static ArrayList<Card> specialCards;
    static {
        specialCards = new ArrayList<>();
        String strcard[] = { "1S", "2S", "3S", "4D", "5D", "6D", "7H", "KH", "JH", "4H", "5H", "6H", "QH" };
        for (int i = 0; i < strcard.length; i++)
            specialCards.add(Card.valueOf(strcard[i]));
    }

    protected Card[] getSpecialDeckRandomCards(int n) {
        Card card[] = new Card[n];
        for (int i = 0; i < n; i++) {
            card[i] = specialCards.remove(0);
        }
        return card;
    }

    @Test
    public void testPlay() {
        EngineTest playTest = new EngineTest();
        playTest.activateSpecialDeck = true;
        playTest.enableTotalCollException=false;
        try {
            String winner = playTest.play();
            assertEquals(winner, "Joueur2");
        } catch (Exception e) {
            assertTrue(false,e.toString());
        }

        // playTest.getW
    }

}