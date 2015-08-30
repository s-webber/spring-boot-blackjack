package com.example.blackjack.model;

import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;
import com.example.blackjack.view.Suit;

/** Creates new {@code Deck} instances. */
@Component
class DeckFactory {
   /** Contains a {@code Card} for every possible combination of {@code Rank} and {@code Suit}. */
   private static final List<Card> CARDS = unmodifiableList(stream(Rank.values()).map(r -> stream(Suit.values()).map(s -> new Card(r, s))).flatMap(identity())
         .collect(toList()));

   private final ShuffleStrategy<Card> shuffleStrategy;

   /**
    * @param shuffleStrategy
    *           the strategy to use to randomly shuffle the cards contained in {@code Deck} instances created by {@link #createDeck()}
    */
   @Autowired
   DeckFactory(ShuffleStrategy<Card> shuffleStrategy) {
      this.shuffleStrategy = shuffleStrategy;
   }

   /** Returns a new {@code Deck} which uses a newly copied and randomly shuffled collection of {@code Card}s. */
   Deck createDeck() {
      return new Deck(shuffleStrategy.shuffle(CARDS));
   }
}
