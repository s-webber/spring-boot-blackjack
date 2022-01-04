package com.example.blackjack.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CardTest {
   @Test
   public void testRankNull() {
      assertThrows(NullPointerException.class, () -> new Card(null, Suit.SPADES));
   }

   @Test
   public void testSuitNull() {
      assertThrows(NullPointerException.class, () -> new Card(Rank.ACE, null));
   }

   @Test
   public void testNulls() {
      assertThrows(NullPointerException.class, () -> new Card(null, null));
   }

   @Test
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
