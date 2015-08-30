package com.example.blackjack.model;

import static com.example.blackjack.view.Rank.ACE;
import static com.example.blackjack.view.Rank.EIGHT;
import static com.example.blackjack.view.Rank.NINE;
import static com.example.blackjack.view.Rank.SEVEN;
import static com.example.blackjack.view.Rank.SIX;
import static com.example.blackjack.view.Rank.TEN;
import static com.example.blackjack.view.Rank.THREE;
import static com.example.blackjack.view.Rank.TWO;
import static com.example.blackjack.view.Suit.SPADES;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.Test;

import com.example.blackjack.CardReader;
import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;

public class HandValuerTest {
   /**
    * Example hands and their expected values.
    * <p>
    * key = hand value, value = list of example hands that should have the same value as the key
    */
   private static final Map<Integer, List<String>> HANDS_BY_VALUE = new HashMap<>();
   static {
      HANDS_BY_VALUE.put(2, asList("2D"));
      HANDS_BY_VALUE.put(3, asList("3H"));
      HANDS_BY_VALUE.put(4, asList("2C 2S"));
      HANDS_BY_VALUE.put(5, asList("2C 3S"));
      HANDS_BY_VALUE.put(6, asList("3D 3H"));
      HANDS_BY_VALUE.put(7, asList("2S 3D 2H"));
      HANDS_BY_VALUE.put(8, asList("2S 3D 3H"));
      HANDS_BY_VALUE.put(9, asList("4D 5H"));
      HANDS_BY_VALUE.put(10, asList("3S 2D 5H"));
      HANDS_BY_VALUE.put(11, asList("5D 6H"));
      HANDS_BY_VALUE.put(12, asList("KS 2C", "AS AC"));
      HANDS_BY_VALUE.put(13, asList("9S 4C"));
      HANDS_BY_VALUE.put(14, asList("2S 3C 4D 5H"));
      HANDS_BY_VALUE.put(15, asList("2S 3C 3D 5H 2H"));
      HANDS_BY_VALUE.put(16, asList("AC 5C"));
      HANDS_BY_VALUE.put(17, asList("7C JD", "TH 6S AC"));
      HANDS_BY_VALUE.put(18, asList("8S QH", "TH 6S AC AS"));
      HANDS_BY_VALUE.put(19, asList("7H 5D 7C", "AS AC AD AH 2C TH 3S"));
      HANDS_BY_VALUE.put(20, asList("5S 5H 5D 5C", "2S 6S 4D 5C 3H", "9H AC"));
      HANDS_BY_VALUE.put(21, asList("7H 7D 7C", "AC KH", "KC KH AC", "AS AC AD AH 2C 2H 2S 2D 3H 3S 3D"));
      HANDS_BY_VALUE.put(22, asList("KH KD 2C", "AC 9S 5C 7C"));
      HANDS_BY_VALUE.put(23, asList("7H 6H TH"));
      HANDS_BY_VALUE.put(24, asList("6S 6H 6D 6C"));
      HANDS_BY_VALUE.put(25, asList("TH TD 5C"));
      HANDS_BY_VALUE.put(26, asList("JC 9C 7H"));
      HANDS_BY_VALUE.put(27, asList("TH 7C JD"));
      HANDS_BY_VALUE.put(28, asList("8D KH QD"));
      HANDS_BY_VALUE.put(29, asList("JH TD 9C"));
      HANDS_BY_VALUE.put(30, asList("KS QH JC"));
   }

   @Test
   public void testHandsByValue() {
      assertHandsByValue((expectedValue, cards) -> assertValue(expectedValue, cards));
   }

   @Test
   public void testEmpty() {
      assertValue(0);
   }

   /** Test the values of hands consisting of one card, for each possible rank. */
   @Test
   public void testEachRankIndividually() {
      for (Rank rank : Rank.values()) {
         assertValue(rank.getValue(), rank);
      }
   }

   /** Test the value of a single hand containing one card for each possible rank. */
   @Test
   public void testEachRankCollectively() {
      // 2+3+4+5+6+7+8+9+10+10+10+10+1 (as ace low) = 85
      assertValue(85, Rank.values());
   }

   @Test
   public void testBlackjack() {
      assertValue(21, ACE, TEN);
   }

   @Test
   public void testBust() {
      assertValue(22, SEVEN, NINE, SIX);
   }

   @Test
   public void testAceHigh() {
      assertValue(21, EIGHT, TWO, ACE);
   }

   @Test
   public void testAceLow() {
      assertValue(12, EIGHT, THREE, ACE);
   }

   @Test
   public void testMultipleAcesOneHigh() {
      assertValue(14, ACE, ACE, ACE, ACE);
   }

   @Test
   public void testMultipleAcesAllLow() {
      assertValue(12, ACE, ACE, ACE, ACE, EIGHT);
   }

   @Test
   public void testIsBelowDealersMinimum() {
      assertHandsByValue((expectedValue, cards) -> assertEquals(expectedValue < 17, HandValuer.isBelowDealersMinimum(cards)));
   }

   @Test
   public void testIsTarget() {
      assertHandsByValue((expectedValue, cards) -> assertEquals(expectedValue == 21, HandValuer.isTarget(cards)));
   }

   @Test
   public void testIsBust() {
      assertHandsByValue((expectedValue, cards) -> assertEquals(expectedValue > 21, HandValuer.isBust(cards)));
   }

   @Test
   public void testIsBustInteger() {
      for (int i = 0; i <= 21; i++) {
         assertFalse(HandValuer.isBust(i));
      }
      for (int i = 22; i <= 30; i++) {
         assertTrue(HandValuer.isBust(i));
      }
   }

   @Test
   public void testIsPossibleBlackjack() {
      for (Rank rank : Rank.values()) {
         Card card = new Card(rank, SPADES);
         assertEquals(rank.getValue() > 9, HandValuer.isPossibleBlackjack(card));
      }
   }

   @Test
   public void testIsBlackjackTrue() {
      assertBlackjack("AH TH");
      assertBlackjack("AS KD");
      assertBlackjack("AD JC");
      assertBlackjack("AC QS");
      assertBlackjack("TH AH");
      assertBlackjack("JS AD");
      assertBlackjack("KD AC");
      assertBlackjack("QC AS");
   }

   @Test
   public void testIsBlackjackFalse() {
      assertNotBlackjack("AC AS"); // right number of cards, too low value
      assertNotBlackjack("AC 9S"); // right number of cards, too low value
      assertNotBlackjack("QC KS"); // right number of cards, too low value
      assertNotBlackjack("JD JC"); // right number of cards, too low value
      assertNotBlackjack("QC 9S"); // right number of cards, too low value
      assertNotBlackjack("TH 5C 6D"); // right value, too many cards
      assertNotBlackjack("TH 3C 6D 2S"); // right value, too many cards
      assertNotBlackjack("7S 3C 8S"); // too many cards, too low value
      assertNotBlackjack("7S 7C 8S"); // too many cards, too high value
   }

   private void assertBlackjack(String cards) {
      assertIsBlackjack(true, cards);
   }

   private void assertNotBlackjack(String cards) {
      assertIsBlackjack(false, cards);
   }

   private void assertIsBlackjack(boolean expected, String cards) {
      assertEquals(expected, HandValuer.isBlackjack(CardReader.toCards(cards)));
   }

   private void assertHandsByValue(BiConsumer<Integer, Collection<Card>> test) {
      for (Map.Entry<Integer, List<String>> handsByValue : HANDS_BY_VALUE.entrySet()) {
         for (String hand : handsByValue.getValue()) {
            test.accept(handsByValue.getKey(), CardReader.toCards(hand));
         }
      }
   }

   private void assertValue(int expectedHandValue, Rank... ranks) {
      Collection<Card> cards = Arrays.stream(ranks).map(r -> new Card(r, SPADES)).collect(toList());
      assertValue(expectedHandValue, cards);
   }

   private void assertValue(int expectedHandValue, Collection<Card> cards) {
      assertEquals(cards.toString(), expectedHandValue, HandValuer.value(cards));
   }
}
