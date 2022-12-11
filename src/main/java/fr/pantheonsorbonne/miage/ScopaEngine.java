package fr.pantheonsorbonne.miage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import fr.pantheonsorbonne.miage.exception.*;
import fr.pantheonsorbonne.miage.game.Card;
import fr.pantheonsorbonne.miage.game.Deck;
import fr.pantheonsorbonne.miage.enums.CardValue;

/**
 * this class is a abstract version of the engine, to be used locally on through
 * the network
 */
public abstract class ScopaEngine {

	public static final int CARDS_IN_HAND = 3;
	private static final String DENIER = "DIAMOND";

	protected boolean enableTotalCollException=true;

	Map<String, Queue<Card>> playerCollectedCards = new HashMap<>();
	Map<String, Integer> playerCollectedScopa = new HashMap<>();
	Queue<Card> roundDeck = new LinkedList<>();

	protected int getDeckSize(){
		return Deck.deckSize;
	}

	protected Card[] getDeckRandomCards(int n) {
		return Deck.getRandomCards(n);
	}
	/**
	 * play a scopa with the provided players
	 * @throws InvalidStateException
	 */
	public String play() throws TotalCollectedCardException, NoSuchPlayerException, InvalidStateException {

		// send the initial hand to every players
		giveInitialHandToPLayers();

		// set the initial collected cards and scopa of each players
		initCollectedAndScopaCards();

		// set the initial round deck and make sure there aren't 3 or more cards of the
		// same value
		do {
			roundDeck.addAll(getInitialRoundDeck());
		} while (checkOverThreeSameCardValue(roundDeck));

		// make a queue with all the players
		final Queue<String> players = new LinkedList<>();
		players.addAll(this.getInitialPlayers());
		for (int i = 0; i < players.size(); i++) {
			players.offer(players.poll());
		}

		// repeat until there are no more cards in deck 
		// and until the players don't have any cards left to play
		while (getDeckSize() > 1 || !noCardsWithPlayers()) {

			// take the first player form the queue
			String currentPlayer = players.poll();

			// display each card of all players and of the deck
			displayPlayerRoundDeckCards(currentPlayer, roundDeck);

			// and put it immediately at the end
			players.offer(currentPlayer);

			if (!getPlayerCards(currentPlayer).isEmpty()) {
				ArrayList<Card> pairCards = makePair(getPlayerCards(currentPlayer), roundDeck);
				// if a pair is possible,
				if (!pairCards.isEmpty()) {
					// processing the pair
					processPairCards(currentPlayer, pairCards, roundDeck);
					// processing the scopa point if done
					processScopaPoint(currentPlayer, roundDeck);
				} else {
					try {
						// applying avoid to put 7D strategy
						Card selectedCard = getCardFromPlayer(currentPlayer);
						// put the card in the round deck
						roundDeck.offer(selectedCard);
					} catch (NoMoreCardException e) {
						System.out.println("no more cards");
					}
				}
			} else {
				// send CARDS_IN_HAND (3) cards to every player
				Card[] cards = getDeckRandomCards(CARDS_IN_HAND);
				String hand = Card.cardsToString(cards);
				giveCardsToPlayer(currentPlayer, hand);
			}

			// lil display
			System.out.println("\n");
		}

		// since we've left the loop, the game is over
		// we give the remaning cards to the last player having played
		addRemainingCardsToCollected(roundDeck, players);

		// displaying the collected cards of each player
		displayPlayerCollectedCards(players);

		// making sure all 40 cards have been collected
		countTotalCards();
		
		// get the winner & send him the gameover & leave
		declareWinner(getWinner(playerCollectedCards));
		return getWinner(playerCollectedCards);
	}


	protected void giveInitialHandToPLayers() {
		for (String playerName : getInitialPlayers()) {
			// get random cards
			Card[] cards = getDeckRandomCards(CARDS_IN_HAND);
			// transform them to String
			String hand = Card.cardsToString(cards); 
			// send them to this players
			giveCardsToPlayer(playerName, hand);

		}
	}

	protected void initCollectedAndScopaCards() {
		for (String playerName : getInitialPlayers()) {
			playerCollectedCards.put(playerName, new LinkedList<>());
			playerCollectedScopa.put(playerName, 0);
		}
	}

	protected List<Card> getInitialRoundDeck() {
		return Arrays.asList(getDeckRandomCards(4));
	}

	/**
	 * informs if there are three or more cards of the same value on the table
	 *
	 * @return true if so, false if not
	 */
	protected boolean checkOverThreeSameCardValue(Queue<Card> queue) {
		HashMap<CardValue, Integer> map = new HashMap<>();
		for (Card card : queue) {
			CardValue value = card.getValue();
			if (map.containsKey(value)) {
				map.put(value, map.get(value) + 1);
				if (map.get(value) >= 3) {
					return true;
				}
			} else {
				map.put(value, 1);
			}
		}
		return false;
	}

	private boolean noCardsWithPlayers() throws NoSuchPlayerException {
		int count = 0;
		for (String player : getInitialPlayers()) {
			count = count + getPlayerCards(player).size();
		}
		return count == 0;
	}

	/*
	 * displaying the player's cards and the round deck's cards in a fancy way
	 */
	protected void displayPlayerRoundDeckCards(String currentPlayer, Queue<Card> roundDeck)
			throws NoSuchPlayerException {
		System.out.print("player " + currentPlayer + ": ");
		getPlayerCards(currentPlayer).stream().forEach(c -> System.out.print(c.toFancyString()));
		System.out.println();
		System.out.print("RoundDeck: ");
		roundDeck.stream().forEach(c -> System.out.print(c.toFancyString()));
		System.out.println();
	}

	/**
	 * get all cards of a player
	 * 
	 * @param playerName
	 * @return all cards of a player
	 */
	protected abstract Queue<Card> getPlayerCards(String playerName) throws NoSuchPlayerException;

	/*
	 * making a pair
	 */
	protected ArrayList<Card> makePair(Queue<Card> playerCards, Queue<Card> roundDeck) {

		// applying 7D strategy
		if (!settebelloInDeckStrategy(playerCards, roundDeck).isEmpty()) {
			System.out.println("took the settebello !");
			return settebelloInDeckStrategy(playerCards, roundDeck);
		} else if (!settebelloInHandStrategy(playerCards, roundDeck).isEmpty()) {
			System.out.println("took the settebello !");
			return settebelloInHandStrategy(playerCards, roundDeck);
		}

		// applying denier strategy
		else if (!denierCardInDeckStrategy(playerCards, roundDeck).isEmpty()) {
			return denierCardInDeckStrategy(playerCards, roundDeck);
		} else if (!denierCardInHandStrategy(playerCards, roundDeck).isEmpty()) {
			return denierCardInHandStrategy(playerCards, roundDeck);
		}

		// applying no specific strategy
		else {
			return noStrategy(playerCards, roundDeck);
		}

	}

	/*
	 * applying 7D strategy : if there's a settebello in the round deck
	 * and if the player have a card value of 7
	 * the player will make a pair with its 7 to take the settebello
	 */
	protected ArrayList<Card> settebelloInDeckStrategy(Queue<Card> playerCards, Queue<Card> roundDeck) {
		ArrayList<Card> playerCardDeckCard = new ArrayList<>();
		for (Card card : roundDeck) {
			if (card.toString().equals("7D")) {
				for (Card playerCard : playerCards) {
					if (playerCard.getValue().getStringRepresentation().equals("7")) {
						playerCardDeckCard.add(playerCard);
						playerCardDeckCard.add(card);
						return playerCardDeckCard;
					}
				}
			}
		}
		return playerCardDeckCard;
	}

	/*
	 * applying 7D strategy : if the player has a settebello in his hand
	 * and if there is a card value of 7 in the deck
	 * the player will make a pair with its settebello to take the 7
	 * by doing such, the player secures the settebello to its collected cards
	 */
	protected ArrayList<Card> settebelloInHandStrategy(Queue<Card> playerCards, Queue<Card> roundDeck) {
		ArrayList<Card> playerCardDeckCard = new ArrayList<>();
		for (Card playerCard : playerCards) {
			if (playerCard.toString().equals("7D")) {
				for (Card card : roundDeck) {
					if (card.getValue().getStringRepresentation().equals("7")) {
						playerCardDeckCard.add(playerCard);
						playerCardDeckCard.add(card);
						return playerCardDeckCard;
					}
				}
			}
		}
		return playerCardDeckCard;
	}

	/*
	 * applying denier strategy : if there's a card of denier in the round deck
	 * and if the player have a card which matches the value of the card of denier
	 * the player will make a pair with this card to take the denier card
	 */
	protected ArrayList<Card> denierCardInDeckStrategy(Queue<Card> playerCards, Queue<Card> roundDeck) {
		ArrayList<Card> playerCardDeckCard = new ArrayList<>();
		for (Card card : roundDeck) {
			if (card.getColor().name().equals(DENIER)) {
				for (Card playerCard : playerCards) {
					if (playerCard.getValue().getStringRepresentation()
							.equals(card.getValue().getStringRepresentation())) {
						playerCardDeckCard.add(playerCard);
						playerCardDeckCard.add(card);
						return playerCardDeckCard;
					}
				}
			}
		}
		return playerCardDeckCard;
	}

	/*
	 * applying denier strategy : if the player has a card of denier in his hand
	 * and if there is a card which matches the value of the player's card of denier
	 * in the deck
	 * the player will make a pair with this card to secure its denier card
	 */
	protected ArrayList<Card> denierCardInHandStrategy(Queue<Card> playerCards, Queue<Card> roundDeck) {
		ArrayList<Card> playerCardDeckCard = new ArrayList<>();
		for (Card playerCard : playerCards) {
			if (playerCard.getColor().name().equals(DENIER)) {
				for (Card card : roundDeck) {
					if (card.getValue().getStringRepresentation()
							.equals(playerCard.getValue().getStringRepresentation())) {
						playerCardDeckCard.add(playerCard);
						playerCardDeckCard.add(card);
						return playerCardDeckCard;
					}
				}
			}
		}
		return playerCardDeckCard;
	}

	/*
	 * applying no strategy : if there's no settebello nor a denier card in
	 * the round deck
	 * the player will make a pair with the first card value in the round deck
	 */
	protected ArrayList<Card> noStrategy(Queue<Card> playerCards, Queue<Card> roundDeck) {
		ArrayList<Card> playerCardDeckCard = new ArrayList<>();
		for (Card playerCard : playerCards) {
			for (Card deckCard : roundDeck) {
				if (deckCard.getValue().getStringRepresentation()
						.equals(playerCard.getValue().getStringRepresentation())) {
					playerCardDeckCard.add(playerCard);
					playerCardDeckCard.add(deckCard);
					return playerCardDeckCard;
				}
			}
		}
		return playerCardDeckCard;
	}

	/**
	 * after making a pair,
	 * processing the cards won by the player by adding them to its collected cards
	 * 
	 * @param currentPlayer  the current player who made a pair
	 * @param pairCards the pair of cards made by the current player
	 * @param roundDeck the current round deck
	 * @return an updated map of the collected cards
	 * @throws NoSuchPlayerException
	 */
	protected Map<String, Queue<Card>> processPairCards(String currentPlayer, ArrayList<Card> pairCards, Queue<Card> roundDeck)
			throws NoSuchPlayerException {
		Card selectedCard = pairCards.get(0);
		playerCollectedCards.get(currentPlayer).offer(selectedCard);
		getPlayerCards(currentPlayer).remove(selectedCard);

		Card removedCard = pairCards.get(1);
		playerCollectedCards.get(currentPlayer).offer(removedCard);
		roundDeck.remove(removedCard);

		return playerCollectedCards;
	}

	/**
	 * after making a pair,
	 * determining if the player did a scopa (ie if the round deck is empty)
	 * and if so, adding a point to its collected scopa
	 * 
	 * @param currentPlayer the current player who made a pair
	 * @param roundDeck the current round deck
	 * @return an updated map of the scopa points
	 */
	protected Map<String, Integer> processScopaPoint(String currentPlayer, Queue<Card> roundDeck) {
		if (roundDeck.isEmpty()) {
			int counter = playerCollectedScopa.get(currentPlayer) + 1;
			playerCollectedScopa.put(currentPlayer, counter);
			System.out.println(" made a scopa !");
		}
		return playerCollectedScopa;
	}

	/**
	 * adding remaining cards of the round deck to the last player having played
	 * 
	 * @param roundDeck last cards in the round deck
	 * @param players queue of all players
	 * @return an updated map of the collected cards
	 */
	protected Map<String, Queue<Card>> addRemainingCardsToCollected(Queue<Card> roundDeck, Queue<String> players) {
		while (!roundDeck.isEmpty()) {
			String currentPlayer = players.poll();
			playerCollectedCards.get(currentPlayer).offer(roundDeck.poll());
			players.offer(currentPlayer);
		}
		return playerCollectedCards;
	}

	/**
	 * display the collected cards of each players
	 * 
	 * @param players
	 */
	protected void displayPlayerCollectedCards(Queue<String> players) {
		System.out.println("Collected Cards");
		for (String currentPlayer : players) {
			System.out.print("player " + currentPlayer + ": ");
			playerCollectedCards.get(currentPlayer).stream().forEach(c -> System.out.print(c.toFancyString()));
			System.out.println();
		}
		System.out.println("\n");
	}

	/**
	 * give the winner of the game
	 * 
	 * @param playerCollectedCards all cards collected by each player
	 * @return the name of the winner or nobody if it's a tie
	 */
	protected String getWinner(Map<String, Queue<Card>> playerCollectedCards) {
		int maxCount = 0;
		String winner = "";
		// count the
		Map<String, Integer> playersScores = countPlayersScores(playerCollectedCards, playerCollectedScopa);
		for (Map.Entry<String, Integer> player : playersScores.entrySet()) {
			System.out.println(player.getKey() + " has " + player.getValue() + " points.");
			if (player.getValue() > maxCount) {
				maxCount = player.getValue();
				winner = player.getKey();
			} else if (player.getValue() == maxCount) {
				winner = "nobody";
			}
		}
		return winner;
	}

	/**
	 * gives the score of each player
	 *
	 * @param playerCollectedCards all cards collected by each player
	 * @return playersScores a map of the players associated with their score
	 */
	protected Map<String, Integer> countPlayersScores(Map<String, Queue<Card>> playerCollectedCards,
			Map<String, Integer> playerCollectedScopa) {

		Map<String, Integer> playersScores = new HashMap<>();
		ArrayList<String> bestCountPlayers = bestCount(playerCollectedCards);
		ArrayList<String> mostDenierCountPlayers = mostDenierCount(playerCollectedCards);

		for (Map.Entry<String, Queue<Card>> player : playerCollectedCards.entrySet()) {
			int count = 0;
			
			for (String joueur : bestCountPlayers) {
				if (player.getKey().equals(joueur)) {
					count++;
				}
			}
			
			for (String joueur : mostDenierCountPlayers) {
				if (player.getKey().equals(joueur)) {
					count++;
				}
			}

			if (player.getKey().equals(havingSettebello(playerCollectedCards))) {
				count++;
			}

			//setting the score of each player
			playersScores.put(player.getKey(), count);

			//adding to the score the scopa points if any
			if (playerCollectedScopa.get(player.getKey()) != null) {
				playersScores.put(player.getKey(), count + playerCollectedScopa.get(player.getKey()));
			}
		}
		//bestCountPlayers.stream().forEach(p -> System.out.println(p + " got 1 point for having collected the most cards."));
		//mostDenierCountPlayers.stream().forEach(p -> System.out.println(p + " got 1 point for having collected the most cards of diamond."));


		return playersScores;
	}

	/**
	 * gives the name[s] of the player[s] having the most pairs at the end of the
	 * game;
	 *
	 * @param playerCollectedCards a map of all cards collected by each player
	 * @return bestPlayers a list of name[s] of the best player[s]
	 */
	ArrayList<String> bestCount(Map<String, Queue<Card>> playerCollectedCards) {
		// determining the highest number of cards collected by a player
		int maxcount = 0;
		for (String player : playerCollectedCards.keySet()) {
			if (playerCollectedCards.get(player).size() > maxcount) {
				maxcount = playerCollectedCards.get(player).size();
			}
		}
		// setting the best players[s] thanks to the maxcount
		ArrayList<String> bestPlayers = new ArrayList<>();
		for (String player : playerCollectedCards.keySet()) {
			if (playerCollectedCards.get(player).size() == maxcount) {
				bestPlayers.add(player);
			}
		}
		return bestPlayers;
	}

	/**
	 * gives the name of the player having the most cards of deniers at the end of
	 * the game; null if 2 players have the same count
	 *
	 * @param playerCollectedCards
	 * @return the player having the most cards of deniers
	 */
	ArrayList<String> mostDenierCount(Map<String, Queue<Card>> playerCollectedCards) {
		long maxcount = 0;
		ArrayList<String> bestPlayers = new ArrayList<>();
		for (String player : playerCollectedCards.keySet()) {
			long counter = playerCollectedCards.get(player).stream()
					.filter(card -> card.getColor().name().equals(DENIER)).count();
			if (counter > maxcount) {
				maxcount = counter;
			}
		}
		for (String player : playerCollectedCards.keySet()) {
			if (playerCollectedCards.get(player).stream()
					.filter(card -> card.getColor().name().equals(DENIER)).count() == maxcount) {
				bestPlayers.add(player);
			}
		}
		return bestPlayers;
	}

	/**
	 * gives the name of the player having settebello (7 of deniers) at the end of
	 * the game
	 *
	 * @param playerCollectedCards
	 * @return the player having the settebello
	 */
	String havingSettebello(Map<String, Queue<Card>> playerCollectedCards) {
		for (String player : playerCollectedCards.keySet()) {
			if (playerCollectedCards.get(player).stream().filter(card -> card.toString().equals("7D")).count() > 0) {
				return player;
			}
		}
		return null;
	}

	/**
	 * this method must be called when a winner is identified
	 * send the winner the gameover
	 *
	 * @param winner the winner
	 * 
	 */
	protected abstract void declareWinner(String winner);

	/**
	 * making sure that all 40 cards are collected at the end of the game
	 * 
	 * @throws TotalCollectedCardException
	 */
	protected void countTotalCards() throws TotalCollectedCardException{
		int count = 0;
		for (String currentPlayer : getInitialPlayers()) {
			count = count + playerCollectedCards.get(currentPlayer).size();
		}

		if (enableTotalCollException && count != 40){
			throw new TotalCollectedCardException(count);
		}
	}

	
	/**
	 * provide the list of the initial players to play the game
	 *
	 * @return
	 */
	protected abstract Set<String> getInitialPlayers();

	/**
	 * give some card to a player
	 *
	 * @param playerName the player that will receive the cards
	 * @param hand       the cards as a string (to be converted later)
	 */
	protected abstract void giveCardsToPlayer(String playerName, String hand);

	/**
	 * get a card from a player. If the player doesn't have a card, it will be
	 * declared loser and all the left over cards will be given to his opponent
	 *
	 * @param leftOverCard               card left over from another round
	 * @param cardProviderPlayer         the player that should give a card
	 * @param cardProviderPlayerOpponent the Opponent of this player
	 * @return a card of null if player cardProviderPlayer is gameover
	 */
	/*protected abstract Card getCardOrGameOver(Collection<Card> leftOverCard, String cardProviderPlayer,
			String cardProviderPlayerOpponent);
	*/
	
	/**
	 * give some card to a player
	 *
	 * @param playerName the player that will receive the cards
	 * @param cards      the cards as a collection of cards
	 */
	protected abstract void giveCardsToPlayer(Collection<Card> cards, String playerName);

	/**
	 * get a card from a player
	 *
	 * @param player the player to give card
	 * @return the card from the player
	 * @throws NoMoreCardException if the player does not have a remaining card
	 * @throws InvalidStateException
	 */
	protected abstract Card getCardFromPlayer(String player) throws NoMoreCardException, InvalidStateException;
}
