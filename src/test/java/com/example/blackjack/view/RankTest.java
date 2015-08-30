package com.example.blackjack.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RankTest {
   @Test
   public void testNumberOfRanks() {
      // 2 to 10 (inclusive) plus Jack, Queen, King and Ace = 13
      assertEquals(13, Rank.values().length);
   }

   @Test
   public void testValues() {
      assertValue(2, Rank.TWO);
      assertValue(3, Rank.THREE);
      assertValue(4, Rank.FOUR);
      assertValue(5, Rank.FIVE);
      assertValue(6, Rank.SIX);
      assertValue(7, Rank.SEVEN);
      assertValue(8, Rank.EIGHT);
      assertValue(9, Rank.NINE);
      assertValue(10, Rank.TEN);
      assertValue(10, Rank.JACK);
      assertValue(10, Rank.QUEEN);
      assertValue(10, Rank.KING);
      assertValue(11, Rank.ACE);
   }

   private void assertValue(int expected, Rank rank) {
      assertEquals(expected, rank.getValue());
   }
}
