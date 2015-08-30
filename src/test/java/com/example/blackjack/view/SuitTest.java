package com.example.blackjack.view;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SuitTest {
   @Test
   public void testNumberOfActions() {
      assertEquals(4, Suit.values().length);
   }
}
