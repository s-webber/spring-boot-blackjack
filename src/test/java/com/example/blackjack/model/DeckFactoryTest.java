package com.example.blackjack.model;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;
import com.example.blackjack.view.Suit;

public class DeckFactoryTest {
   private static final int EXPECTED_NUMBER_OF_SUITS = 4;
   private static final int EXPECTED_NUMBER_OF_RANKS = 13;
   private static final int EXPECTED_NUMBER_OF_CARDS = EXPECTED_NUMBER_OF_SUITS * EXPECTED_NUMBER_OF_RANKS;

   @Test
   @SuppressWarnings("unchecked")
   public void testShuffleStrategy() {
      // create mocks
      ShuffleStrategy<Card> mockShuffleStrategy = mock(ShuffleStrategy.class);
      Card dummyCard1 = new Card(Rank.THREE, Suit.SPADES);
      Card dummyCard2 = new Card(Rank.KING, Suit.CLUBS);
      Card dummyCard3 = new Card(Rank.SEVEN, Suit.DIAMONDS);
      when(mockShuffleStrategy.shuffle(any())).thenReturn(asList(dummyCard1), asList(dummyCard2), asList(dummyCard3));

      // create object to test
      DeckFactory deckFactory = new DeckFactory(mockShuffleStrategy);

      assertSame(dummyCard1, deckFactory.createDeck().deal());
      assertSame(dummyCard2, deckFactory.createDeck().deal());
      assertSame(dummyCard3, deckFactory.createDeck().deal());
   }

   @Test
   public void testDeckContents() {
      DeckFactory deckFactory = new DeckFactory(new ShuffleStrategy<Card>());
      Deck d = deckFactory.createDeck();

      // deal cards from deck and store in a list
      List<Card> cards = new ArrayList<>();
      for (int i = 0; i < EXPECTED_NUMBER_OF_CARDS; i++) {
         Card c = d.deal();
         assertNotNull(c);
         cards.add(c);
      }

      // check there are not more than the expected number of cards in the deck
      try {
         d.deal();
         fail();
      } catch (IllegalStateException e) {
         assertEquals("deck is empty", e.getMessage());
      }

      // check dealt cards contain every combination of rank and suit
      Map<Suit, List<Card>> cardsBySuit = cards.stream().collect(Collectors.groupingBy(Card::getSuit));
      assertEquals(EXPECTED_NUMBER_OF_SUITS, cardsBySuit.size());
      cardsBySuit.values().stream().forEach(c -> assertEquals(EXPECTED_NUMBER_OF_RANKS, c.size()));
   }
}
