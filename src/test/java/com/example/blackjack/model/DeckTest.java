package com.example.blackjack.model;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;
import com.example.blackjack.view.Suit;

public class DeckTest {
   @Test
   public void test() {
      // create input
      Card c1 = new Card(Rank.TWO, Suit.HEARTS);
      Card c2 = new Card(Rank.KING, Suit.DIAMONDS);
      Card c3 = new Card(Rank.EIGHT, Suit.SPADES);
      List<Card> cards = asList(c1, c2, c3);

      // create object to test
      Deck d = new Deck(cards);

      // mutate List used to create Deck -
      // this will test that Deck is not affected by subsequent changes to the List passed as an argument to its constructor
      cards.set(0, null);
      cards.set(1, null);
      cards.set(2, null);

      // test deal() returns the cards
      assertSame(c1, d.deal());
      assertSame(c2, d.deal());
      assertSame(c3, d.deal());

      // check there are not more than the expected number of cards in the deck
      try {
         d.deal();
         fail();
      } catch (IllegalStateException e) {
         assertEquals("deck is empty", e.getMessage());
      }
   }
}
