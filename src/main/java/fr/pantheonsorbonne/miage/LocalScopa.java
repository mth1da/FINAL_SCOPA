package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.exception.NoSuchPlayerException;
import fr.pantheonsorbonne.miage.game.Card;

import java.util.*;

/**
 * this class implements the scopa locally
 */
public class LocalScopa extends ScopaEngine {

    private final Set<String> initialPlayers;
    private final Map<String, Queue<Card>> playerCards = new HashMap<>();

    protected Queue<Card> getPlayerCards(String playerName) throws NoSuchPlayerException{
        Queue<Card> playerCard;
        playerCard=playerCards.get(playerName);
        if (playerCard==null)
            throw new NoSuchPlayerException(playerName);
        return playerCard;
    }
    
    

    public LocalScopa(Set<String> initialPlayers) {
        this.initialPlayers = initialPlayers;
        for (String player : initialPlayers) {
            playerCards.put(player, new LinkedList<>());
        }
    }

    @Override
    protected Map<String, Integer> countPlayersScores(Map<String, Queue<Card>> playerCollectedCards, Map<String, Integer> playerCollectedScopa){
    	return super.countPlayersScores(playerCollectedCards, playerCollectedScopa);
    }


    public static void main(String... args) {
        LocalScopa localScopa = new LocalScopa(Set.of("Joueur1", "Joueur2", "Joueur3", "Joueur4"));
        try{
            
            localScopa.play();
        }
        catch (Throwable e){
            System.out.println(e);
        }
        

    }

    @Override
    protected Set<String> getInitialPlayers() {
        return this.initialPlayers;
    }

    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
        List<Card> cards = Arrays.asList(Card.stringToCards(hand));
        this.giveCardsToPlayer(cards, playerName);
    }

    @Override
    protected void declareWinner(String winner) {
        System.out.println(winner + " has won!");
    }

    @Override
    protected Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer, String cardProviderPlayerOpponent) {

        if (!this.playerCards.containsKey(cardProviderPlayer) || this.playerCards.get(cardProviderPlayer).isEmpty()) {
            this.playerCards.get(cardProviderPlayerOpponent).addAll(leftOverCard);
            this.playerCards.remove(cardProviderPlayer);
            return null;
        } else {
            return this.playerCards.get(cardProviderPlayer).poll();
        }
    }

    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String winner) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        Collections.shuffle(cards);
        this.playerCards.get(winner).addAll(cards);
    }

    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException {
        if (this.playerCards.get(player).isEmpty()) {
            throw new NoMoreCardException();
        } else {
        	Card card= this.playerCards.get(player).poll();
        	if (card.toString().equals("7D") && !this.playerCards.get(player).isEmpty()) {
        			Card secondCard = this.playerCards.get(player).poll();
        			this.playerCards.get(player).offer(card);
        			card=secondCard;
        	}        	
            return card;
        }
    }


}
