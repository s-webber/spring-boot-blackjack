package com.example.blackjack.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.example.blackjack.CardReader;
import com.example.blackjack.view.Card;
import com.example.blackjack.view.GameState;
import com.example.blackjack.view.Hand;
import com.example.blackjack.view.Status;

public class GameTest {
   private static final String DUMMY_GAME_ID = "375423b0-e862-4145-bec4-511fbe723227";

   @Test
   public void testGetId() {
      Game game = new Game(DUMMY_GAME_ID, toDeck("AS 2H 3C"));
      assertSame(DUMMY_GAME_ID, game.getId());
   }

   @Test
   public void testBothHave21NotBlackjack() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("7S TH 3S 4S 4C 7H 7D"));
      g.hit();
      g.hit();
      assertOutcome(g, Status.DRAW, 21, 21);
   }

   @Test
   public void testBothHave17() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("7D TH 7H TD"));
      g.stand();
      assertOutcome(g, Status.DRAW, 17, 17);
   }

   @Test
   public void testPlayerHas18DealerHas17() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("7D TH 8H TD"));
      g.stand();
      assertOutcome(g, Status.PLAYER_WON, 17, 18);
   }

   @Test
   public void testPlayerHas18DealerHas19() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("9D TH 8H TD"));
      g.stand();
      assertOutcome(g, Status.DEALER_WON, 19, 18);
   }

   @Test
   public void testPlayerHas15DealerBust() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("7D TH 5H 9D 6D"));
      g.stand();
      assertOutcome(g, Status.PLAYER_WON, 22, 15);
   }

   @Test
   public void testPlayerHas16DealerBust() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("7D TH 6H 9D 6D"));
      g.stand();
      assertOutcome(g, Status.PLAYER_WON, 22, 16);
   }

   @Test
   public void testPlayerBust() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("2C TH TD 2S"));
      g.hit();
      assertOutcome(g, Status.DEALER_WON, 2, 22);
   }

   @Test
   public void testDealerContinuesUntil17() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("2S TD 4D 2H 2D 2C 3S 3H 3D"));
      g.stand();
      assertOutcome(g, Status.DEALER_WON, 17, 14);
   }

   @Test
   public void testDealerBust() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("TH TD 4D 5H 7H"));
      g.stand();
      assertOutcome(g, Status.PLAYER_WON, 22, 14);
   }

   @Test
   public void testPlayerHasBlackjackDealerHasTwo() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("2C AS KH"));
      assertOutcome(g, Status.PLAYER_WON, 2, 21);
   }

   @Test
   public void testPlayerHasBlackjackDealerHasNine() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("9C AS KH"));
      assertOutcome(g, Status.PLAYER_WON, 9, 21);
   }

   @Test
   public void testDealerHasBlackjack() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("KH 7D 3H 8C AS"));
      g.hit();
      g.stand();
      assertOutcome(g, Status.DEALER_WON, 21, 18);
   }

   @Test
   public void testPlayerHasBlackjackDealerHasAce() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("AS AC TC 4C"));
      assertOutcome(g, Status.PLAYER_WON, 15, 21);
   }

   @Test
   public void testPlayerHasBlackjackDealerHasTen() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("TS AC TC 4C"));
      assertOutcome(g, Status.PLAYER_WON, 14, 21);
   }

   @Test
   public void testPlayerHas21DealerHasBlackjack() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("AS 9S 4S 8S TS"));
      g.hit();
      assertOutcome(g, Status.DEALER_WON, 21, 21);
   }

   @Test
   public void testBothHaveBlackjack() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("KH AS TD AC"));
      assertOutcome(g, Status.DRAW, 21, 21);
   }

   @Test
   public void testCannotUpdateAfterStand() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("9D TH 7H TD"));
      g.stand();
      assertCannotUpdate(g);
      assertOutcome(g, Status.DEALER_WON, 19, 17);
   }

   @Test
   public void testCannotUpdateAfterBust() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("9D TH 5H 7H"));
      g.hit();
      assertCannotUpdate(g);
      assertOutcome(g, Status.DEALER_WON, 9, 22);
   }

   @Test
   public void testCannotUpdateReachedTarget() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("9D TH 5H 6H 8D"));
      g.hit();
      assertCannotUpdate(g);
      assertOutcome(g, Status.PLAYER_WON, 17, 21);
   }

   @Test
   public void testSnapshotCurrentState() {
      Game g = new Game(DUMMY_GAME_ID, toDeck("9D TH 5H 3H 2H 4D 5D"));

      // check initial state
      GameState gs = g.snapshotCurrentState();
      assertSame(DUMMY_GAME_ID, gs.getId());
      assertSame(Status.PLAYERS_TURN, gs.getStatus());
      assertHand("9D", gs.getDealer());
      assertHand("TH 5H", gs.getPlayer());

      // check state after first hit
      g.hit();
      gs = g.snapshotCurrentState();
      assertSame(DUMMY_GAME_ID, gs.getId());
      assertSame(Status.PLAYERS_TURN, gs.getStatus());
      assertHand("9D", gs.getDealer());
      assertHand("TH 5H 3H", gs.getPlayer());

      // check state after second hit
      g.hit();
      gs = g.snapshotCurrentState();
      assertSame(DUMMY_GAME_ID, gs.getId());
      assertSame(Status.PLAYERS_TURN, gs.getStatus());
      assertHand("9D", gs.getDealer());
      assertHand("TH 5H 3H 2H", gs.getPlayer());

      // check state after stand
      g.stand();
      gs = g.snapshotCurrentState();
      assertSame(DUMMY_GAME_ID, gs.getId());
      assertSame(Status.PLAYER_WON, gs.getStatus());
      assertHand("9D 4D 5D", gs.getDealer());
      assertHand("TH 5H 3H 2H", gs.getPlayer());
   }

   private void assertHand(String string, Hand hand) {
      Collection<Card> cards = CardReader.toCards(string);
      assertEquals(HandValuer.value(cards), hand.getValue());
      assertEquals(cards.toString(), hand.getCards().toString());
   }

   private void assertCannotUpdate(Game g) {
      assertGameAlreadyCompleteException(g::hit);
      assertGameAlreadyCompleteException(g::stand);
   }

   private void assertGameAlreadyCompleteException(Runnable operation) {
      try {
         operation.run();
         fail();
      } catch (GameAlreadyCompleteException e) {
         assertEquals("cannot update already completed game with id " + DUMMY_GAME_ID, e.getMessage());
      }
   }

   private void assertOutcome(Game g, Status expectedOutome, int expectedDealerValue, int expectedPlayerValue) {
      GameState s = g.snapshotCurrentState();
      assertSame(expectedOutome, s.getStatus());
      assertEquals(expectedDealerValue, s.getDealer().getValue());
      assertEquals(expectedPlayerValue, s.getPlayer().getValue());
   }

   /**
    * @param cards
    *           A space separated sequence of two character codes where the first character represents the rank and the second character represents the suit.
    *           e.g. {@code TH AC 3D KS}
    */
   private static Deck toDeck(String cards) {
      return new Deck(CardReader.toCards(cards));
   }
}
