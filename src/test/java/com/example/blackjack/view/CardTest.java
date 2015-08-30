package com.example.blackjack.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class CardTest {
   @Test(expected = NullPointerException.class)
   public void testRankNull() {
      new Card(null, Suit.SPADES);
   }

   @Test(expected = NullPointerException.class)
   public void testSuitNull() {
      new Card(Rank.ACE, null);
   }

   @Test(expected = NullPointerException.class)
   public void testNulls() {
      new Card(null, null);
   }

   public void testGetters() {
      assertGetters(Rank.KING, Suit.CLUBS);
      assertGetters(Rank.NINE, Suit.HEARTS);
   }

   private void assertGetters(Rank rank, Suit suit) {
      Card card = new Card(rank, suit);
      assertSame(rank, card.getRank());
      assertSame(suit, card.getSuit());
   }

   @Test
   public void testToString() {
      assertEquals("[ACE SPADES]", new Card(Rank.ACE, Suit.SPADES).toString());
      assertEquals("[SEVEN DIAMONDS]", new Card(Rank.SEVEN, Suit.DIAMONDS).toString());
   }
}
