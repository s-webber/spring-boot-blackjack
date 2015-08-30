package com.example.blackjack.view;

import static java.util.Objects.requireNonNull;
import io.swagger.annotations.ApiModelProperty;

/**
 * Represents a playing card.
 * <p>
 * The {@code Object#equals(Object)} and {@code Object#hashCode()} methods are purposely not overriden by this class. This is because if in the future this
 * service is extended to support multiple packs per game then it will be possible for a hand to contain multiple cards with both the same rank and suit -
 * therefore not treating each {@code Card} instance as unique could cause confusion.
 */
public final class Card {
   private final Rank rank;
   private final Suit suit;

   /**
    * Creates a {@code Card} with the given rank and suit.
    *
    * @throws NullPointerException
    *            if either of {@code rank} or {@code suit} are {@code null}
    */
   public Card(Rank rank, Suit suit) {
      this.rank = requireNonNull(rank);
      this.suit = requireNonNull(suit);
   }

   @ApiModelProperty(required = true)
   public Rank getRank() {
      return rank;
   }

   @ApiModelProperty(required = true)
   public Suit getSuit() {
      return suit;
   }

   @Override
   public String toString() {
      return "[" + rank + " " + suit + "]";
   }
}
