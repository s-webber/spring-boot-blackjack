package com.example.blackjack.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.GameState;
import com.example.blackjack.view.Hand;
import com.example.blackjack.view.Status;

/**
 * A mutable representation of a blackjack game.
 * <p>
 * Only two actions are currently supported during the playing of a game - "hit" and "stand". TODO Add support for "insurance", "double down", "split" and
 * "surrender".
 * </p>
 * <p>
 * TODO Consider adding a version number member variable that is incremented after every update and returned in the {@code GameState}. The calling code could
 * provide this value as an argument in subsequent calls to update the {@code Game}. If the value provided by the caller did not match the current value stored
 * by the {@code Game} then an exception could be thrown to indicate that the caller is not properly aligned with the current state. This could be handled at
 * the web service level using {@code ETag}s to specify the value and the {@code 412} (Precondition Failed) error status to indicate an inconsistency.
 * </p>
 */
public class Game {
   private static final Logger LOG = LoggerFactory.getLogger(Game.class);

   /** Used to coordinate the updating and retrieval of mutable state. */
   private final Object lock = new Object();

   private final String id;
   private final Deck deck;
   /** The cards currently held by the dealer, in the order they were dealt. */
   private final List<Card> dealer = new ArrayList<>();
   /** The cards currently held by the player, in the order they were dealt. */
   private final List<Card> player = new ArrayList<>();
   private Status status = Status.PLAYERS_TURN;

   /**
    * @param id
    *           the identifier for this game
    * @param deck
    *           the {@code Deck} to use to obtain {@code Card}s for this game.
    * @throws NullPointerException
    *            if either {@code id} or {@code deck} are {@code null}
    */
   public Game(String id, Deck deck) {
      this.id = requireNonNull(id);
      this.deck = requireNonNull(deck);
      dealInitialCards();
   }

   /** At the start of a game the dealer is dealt one card and the player is dealt two cards. */
   private void dealInitialCards() {
      deal(dealer);

      deal(player);
      deal(player);

      // call postUpdate so that if the player has been dealt blackjack then the game will be immediately updated to a completed state
      postUpdate();
   }

   /** Returns the identifier for this game. */
   public String getId() {
      return id;
   }

   /** Returns an immutable snapshot of the current state of this game. */
   public GameState snapshotCurrentState() {
      synchronized (lock) {
         return new GameState(id, status, new Hand(dealer), new Hand(player));
      }
   }

   /**
    * Deals another card to the player.
    *
    * @return the updated state of the game as a result of this action
    * @throws GameAlreadyCompleteException
    *            if this game is not in an appropriate state to be updated
    */
   public GameState hit() {
      return update(() -> deal(player));
   }

   /**
    * Updates the status of this game to indicate that the player has completed their turn.
    *
    * @return the updated state of the game as a result of this action
    * @throws GameAlreadyCompleteException
    *            if this game is not in an appropriate state to be updated
    */
   public GameState stand() {
      return update(this::setPlayerComplete);
   }

   /**
    * Attempts to update the state of this game using the given logic.
    * <p>
    * This method ensures updates are performed in a consistent way by wrapping the given logic in the following:
    * <ol>
    * <li>Ensures updates are processed sequentially rather than in parallel.</li>
    * <li>Disallows updates to games that are not in an appropriate state.</li>
    * <li>Performs any actions required due to the result of the update.</li>
    * <li>Returns the new state of the game resulting from the update.</li>
    * </ol>
    *
    * @param updateLogic
    *           contains the logic to apply to this game
    * @return the updated state of the game as a result of applying {@code updateLogic} to it
    */
   private GameState update(Runnable updateLogic) {
      synchronized (lock) {
         assertUpdatable();
         updateLogic.run();
         postUpdate();
         return snapshotCurrentState();
      }
   }

   /**
    * @throws GameAlreadyCompleteException
    *            if this game is not in an appropriate state to be updated.
    */
   private void assertUpdatable() {
      if (status != Status.PLAYERS_TURN) {
         throw new GameAlreadyCompleteException(id);
      }
   }

   /** Performs any actions required as a result of an update. */
   private void postUpdate() {
      if (HandValuer.isBlackjack(player)) {
         handlePlayerBlackjack();
      } else if (HandValuer.isBust(player)) {
         // if the player is bust then there is no need to deal any further cards for the dealer - the player has lost
         setStatus(Status.DEALER_WON);
      } else if (isDealersTurn()) {
         playDealer();
         setGameOutcome();
      }
   }

   /**
    * Called to determine the outcome of the game when it is has already been determined that the player has blackjack.
    * <p>
    * If the player has blackjack (i.e. a hand of two cards with a combined total of 21) then the player will win unless the dealer also has blackjack (in which
    * case the game outcome will be a draw). Therefore, it is only worth the dealer attempting to add extra cards to their hand if it is possible for them to
    * get blackjack (based on the value of the single card they were already dealt at the start of the game).
    */
   private void handlePlayerBlackjack() {
      if (HandValuer.isPossibleBlackjack(dealer.get(0))) {
         deal(dealer);
         setGameOutcome();
      } else {
         setStatus(Status.PLAYER_WON);
      }
   }

   /** Returns {@code true} if the players turn is over. */
   private boolean isDealersTurn() {
      return status == Status.DEALERS_TURN || HandValuer.isTarget(player);
   }

   /**
    * Deal cards for the dealer.
    * <p>
    * The dealer continues to hit until the value of their hand is equal to or greater than the dealer's minimum.
    */
   private void playDealer() {
      while (HandValuer.isBelowDealersMinimum(dealer)) {
         deal(dealer);
      }
   }

   private void setGameOutcome() {
      Status gameOutcome = determineOutcome(dealer, player);
      setStatus(gameOutcome);
   }

   /** Adds a new card to the given hand. */
   private void deal(List<Card> hand) {
      Card next = requireNonNull(deck.deal());
      LOG.info(id + " dealt " + next);
      hand.add(next);
   }

   /** Updates the status of this game to indicate that the players turn is complete. */
   private void setPlayerComplete() {
      setStatus(Status.DEALERS_TURN);
   }

   private void setStatus(Status newStatus) {
      LOG.info(id + " changed from " + status + " to " + newStatus);
      status = newStatus;
   }

   /**
    * Returns the outcome of this completed game.
    * <p>
    * Players win by not busting and having a total higher than the dealer's. The dealer loses by busting or having a lesser hand than the player who has not
    * busted. If the player and dealer have the same total then the game is a draw - unless exactly one of the hands is a blackjack, in which case the owner of
    * that hand wins.
    */
   private static Status determineOutcome(Collection<Card> dealer, Collection<Card> player) {
      int playerValue = HandValuer.value(player);
      if (HandValuer.isBust(playerValue)) {
         // sanity check - should never get here as if player is bust then postUpdate should of immediately set the status to DEALER_WON,
         // rather than call this method
         throw new IllegalStateException();
      }

      int dealerValue = HandValuer.value(dealer);
      if (HandValuer.isBust(dealerValue)) {
         return Status.PLAYER_WON;
      }

      if (playerValue > dealerValue) {
         return Status.PLAYER_WON;
      } else if (dealerValue > playerValue) {
         return Status.DEALER_WON;
      } else {
         return determineOutcomeWhenHandsHaveSameValue(dealer, player);
      }
   }

   /**
    * Returns the outcome of this completed game where it has already been determined that both the dealer and player have hands of the same value.
    * <p>
    * A blackjack beats any hand that is not a blackjack, even one with a value of 21.
    */
   private static Status determineOutcomeWhenHandsHaveSameValue(Collection<Card> dealer, Collection<Card> player) {
      boolean dealerHasBlackjack = HandValuer.isBlackjack(dealer);
      boolean playerHasBlackjack = HandValuer.isBlackjack(player);
      if (dealerHasBlackjack == playerHasBlackjack) {
         return Status.DRAW;
      } else if (dealerHasBlackjack) {
         return Status.DEALER_WON;
      } else {
         // sanity check - if player has blackjack then it should not be possible for the dealer to reach 21 with more than 2 cards,
         // as dealer will stop dealing themselves card once they know they cannot win, so the branch of the code should never be executed
         throw new IllegalStateException();
      }
   }
}
