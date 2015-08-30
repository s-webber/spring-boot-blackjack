package com.example.blackjack.view;

/** Represents a rank associated with a playing card. */
public enum Rank {
   TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10), ACE(11);

   private final int value;

   private Rank(int value) {
      this.value = value;
   }

   /**
    * Returns the value associated with this rank.
    * <p>
    * In the case of an {@code ACE} it will be the "soft" value of 11 that will be returned. The {@link com.example.blackjack.model.HandValuer} is
    * responsible for determining when the "hard" value of 1 should be used instead.
    */
   public int getValue() {
      return value;
   }
}
