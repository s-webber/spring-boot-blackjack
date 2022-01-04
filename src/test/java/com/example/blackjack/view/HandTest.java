package com.example.blackjack.view;

import static com.example.blackjack.CardReader.toCards;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

public class HandTest {
   private static final Card CARD = new Card(Rank.FIVE, Suit.DIAMONDS);

   @Test
   public void testGetValue() {
      Hand hand = new Hand(toCards("AS 7C"));
      assertEquals(18, hand.getValue());
   }

   @Test
   public void testToString() {
      Hand hand = new Hand(toCards("AS 7C"));
      assertEquals("[[ACE SPADES], [SEVEN CLUBS]]", hand.getCards().toString());
   }

   @Test
   public void testImmutable() {
      Collection<Card> cards = toCards("AS 7C");
      Hand hand = new Hand(cards);
      assertImmutableCopy(cards, hand.getCards());
   }

   private void assertImmutableCopy(Collection<Card> original, List<Card> copy) {
      assertContainSameElements(original, copy);
      assertImmutable(copy);

      // confirm changes in original do not affect copy
      final int expectedCopySize = copy.size();
      original.add(CARD);
      assertEquals(expectedCopySize, copy.size());
      original.clear();
      assertEquals(expectedCopySize, copy.size());
   }

   private void assertContainSameElements(Collection<Card> original, List<Card> copy) {
      assertTrue(original.containsAll(copy));
      assertTrue(copy.containsAll(original));
   }

   private void assertImmutable(List<Card> list) {
      // Asserting the class of the given list is the same class as instances returned from
      // Collections.unmodifiableList in order to confirm that that the given list is immutable.
      assertSame(Collections.unmodifiableList(asList(CARD)).getClass(), list.getClass());
   }
}
