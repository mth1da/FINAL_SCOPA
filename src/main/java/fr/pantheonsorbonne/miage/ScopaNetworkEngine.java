package fr.pantheonsorbonne.miage;

import fr.pantheonsorbonne.miage.exception.InvalidStateException;
import fr.pantheonsorbonne.miage.exception.NoMoreCardException;
import fr.pantheonsorbonne.miage.exception.NoSuchPlayerException;
import fr.pantheonsorbonne.miage.exception.TotalCollectedCardException;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.model.Game;
import fr.pantheonsorbonne.miage.model.GameCommand;

import java.util.*;

/**
 * This class implements the scopa with the network engine
 */
public class ScopaNetworkEngine extends ScopaEngine {
    private static final int PLAYER_COUNT = 4;
    private static final String CARDSFORYOU = "cardsForYou";

    private final HostFacade hostFacade;
    //private final Set<String> players;
    private final Game scopa;

    public ScopaNetworkEngine(HostFacade hostFacade, fr.pantheonsorbonne.miage.model.Game scopa) {
        this.hostFacade = hostFacade;
        //this.players = players;
        this.scopa = scopa;
    }

    public static void main(String[] args) throws TotalCollectedCardException, NoSuchPlayerException, InvalidStateException {
        //create the host facade
        HostFacade hostFacade = Facade.getFacade();
        hostFacade.waitReady();

        //set the name of the player
        hostFacade.createNewPlayer("Host");

        //create a new game of scopa
        fr.pantheonsorbonne.miage.model.Game scopa = hostFacade.createNewGame("SCOPA");

        //wait for enough players to join
        hostFacade.waitForExtraPlayerCount(PLAYER_COUNT);

        ScopaEngine host = new ScopaNetworkEngine(hostFacade, scopa);
        host.play();


    }

    /**
     * get the set of players initially in the game
     *
     * @return
     */
    @Override
    protected Set<String> getInitialPlayers() {
        return this.scopa.getPlayers();
    }

    /**
     * give this hand (as string) to the provided player
     *
     * @param playerName name of the player to receive the cards
     * @param hand       the cards as Strings
     */
    @Override
    protected void giveCardsToPlayer(String playerName, String hand) {
        hostFacade.sendGameCommandToPlayer(scopa, playerName, new GameCommand(CARDSFORYOU, hand));
    }


    @Override
    protected void declareWinner(String winner) {
        hostFacade.sendGameCommandToPlayer(scopa, winner, new GameCommand("gameOver", "win"));
    }


    /**
     * give this stack of card to the winner player
     *
     * @param roundStack a stack of card at stake
     * @param winner     the winner
     */
    @Override
    protected void giveCardsToPlayer(Collection<Card> roundStack, String winner) {
        List<Card> cards = new ArrayList<>();
        cards.addAll(roundStack);
        //shuffle the round deck so we are not stuck
        Collections.shuffle(cards);
        hostFacade.sendGameCommandToPlayer(scopa, winner, new GameCommand(CARDSFORYOU, Card.cardsToString(cards.toArray(new Card[cards.size()]))));
    }

    /**
     * we get a card from a player, if possible.
     * 
     * If the player has no more card, throw an exception
     *
     * @param player the name of the player
     * @return a card from a player
     * @throws NoMoreCardException if player has no more card.
     * @throws InvalidStateException
     */
    @Override
    protected Card getCardFromPlayer(String player) throws NoMoreCardException, InvalidStateException {
        hostFacade.sendGameCommandToPlayer(scopa, player, new GameCommand("playACard"));
        GameCommand expectedCard = hostFacade.receiveGameCommand(scopa);
        if (expectedCard.name().equals("card")) {
            return Card.valueOf(expectedCard.body());
        }
        if (expectedCard.name().equals("outOfCard")) {
            throw new NoMoreCardException();
        }
        //should not happen!
        throw new InvalidStateException();

    }

    @Override
    protected Queue<Card> getPlayerCards(String playerName) {
        return new LinkedList<>();
    }

}