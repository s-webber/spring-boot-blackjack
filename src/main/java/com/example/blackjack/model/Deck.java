package com.example.blackjack.model;

import static java.util.Objects.nonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.example.blackjack.view.Card;

/**
 * Represents a deck of cards for use by a particular game.
 *
 * @see DeckFactory#createDeck()
 */
final class Deck {
   private final Queue<Card> cards;

   /**
    * @param cards
    *           the contents of the deck, in the order they are to be dealt
    */
   Deck(List<Card> cards) {
      this.cards = new LinkedList<>(cards);
   }

   /**
    * Removes and returns the next {@code Card}.
    *
    * @throws IllegalStateException
    *            if the deck is empty
    */
   Card deal() {
      Card next = poll();
      if (nonNull(next)) {
         return next;
      } else {
         throw new IllegalStateException("deck is empty");
      }
   }

   private Card poll() {
      synchronized (cards) {
         // TODO If used to gamble for real money then it may be preferable to shuffle the remaining cards
         // immediately before every deal rather than the current situation where the cards are just shuffled once before the deck is created.
         // The advantage of shuffling every time a new card is requested is that even if someone managed to get access to the initial
         // ordering they still would not have the advantage of knowing what order the cards will really be dealt.
         return cards.poll();
      }
   }
}
