package com.example.blackjack.view;

import static java.util.Objects.requireNonNull;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.example.blackjack.model.HandValuer;

/** Represents the cards that belong to a participant in a game. */
public final class Hand {
   @ApiModelProperty(value = "The cards that belong to this hand, in the order they were dealt.", required = true)
   private final List<Card> cards;
   @ApiModelProperty(value = "The value of this hand. Any value higher than 21 indicates that this hand is bust.", required = true)
   private final int value;

   /**
    * Creates a {@code Hand} which contains the given cards.
    *
    * @param cards
    *           the cards that belong to this hand, in the order they were dealt
    * @throws NullPointerException
    *            if {@code cards} is {@code null}
    */
   public Hand(Collection<Card> cards) {
      this.cards = copyOf(cards);
      this.value = HandValuer.value(this.cards);
   }

   private static List<Card> copyOf(Collection<Card> original) {
      ArrayList<Card> copy = new ArrayList<Card>(requireNonNull(original));
      return Collections.unmodifiableList(copy);
   }

   public List<Card> getCards() {
      return cards;
   }

   public int getValue() {
      return value;
   }
}
