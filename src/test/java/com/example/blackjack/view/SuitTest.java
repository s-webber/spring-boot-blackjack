package com.example.blackjack.view;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class SuitTest {
   @Test
   public void testNumberOfActions() {
      assertEquals(4, Suit.values().length);
   }
}
