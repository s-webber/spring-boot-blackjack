package com.example.blackjack;

import static com.example.blackjack.view.Rank.ACE;
import static com.example.blackjack.view.Rank.EIGHT;
import static com.example.blackjack.view.Rank.FIVE;
import static com.example.blackjack.view.Rank.FOUR;
import static com.example.blackjack.view.Rank.JACK;
import static com.example.blackjack.view.Rank.KING;
import static com.example.blackjack.view.Rank.NINE;
import static com.example.blackjack.view.Rank.QUEEN;
import static com.example.blackjack.view.Rank.SEVEN;
import static com.example.blackjack.view.Rank.SIX;
import static com.example.blackjack.view.Rank.TEN;
import static com.example.blackjack.view.Rank.THREE;
import static com.example.blackjack.view.Rank.TWO;
import static com.example.blackjack.view.Suit.CLUBS;
import static com.example.blackjack.view.Suit.DIAMONDS;
import static com.example.blackjack.view.Suit.HEARTS;
import static com.example.blackjack.view.Suit.SPADES;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;
import com.example.blackjack.view.Suit;

public class CardReaderTest {
   @Test
   public void testSingleCard() {
      assertCard("2S", TWO, SPADES);
      assertCard("3H", THREE, HEARTS);
      assertCard("4D", FOUR, DIAMONDS);
      assertCard("5C", FIVE, CLUBS);
      assertCard("6S", SIX, SPADES);
      assertCard("7H", SEVEN, HEARTS);
      assertCard("8D", EIGHT, DIAMONDS);
      assertCard("9C", NINE, CLUBS);
      assertCard("TS", TEN, SPADES);
      assertCard("JH", JACK, HEARTS);
      assertCard("QD", QUEEN, DIAMONDS);
      assertCard("KC", KING, CLUBS);
      assertCard("AS", ACE, SPADES);
   }

   @Test
   public void testSequenceOfCards() {
      Iterator<Card> itr = CardReader.toCards("KH JC 2D 7D QH TS TH").iterator();
      assertCard(itr.next(), KING, HEARTS);
      assertCard(itr.next(), JACK, CLUBS);
      assertCard(itr.next(), TWO, DIAMONDS);
      assertCard(itr.next(), SEVEN, DIAMONDS);
      assertCard(itr.next(), QUEEN, HEARTS);
      assertCard(itr.next(), TEN, SPADES);
      assertCard(itr.next(), TEN, HEARTS);
      assertFalse(itr.hasNext());
   }

   @Test
   public void testInvalidEmpty() {
      assertInvalid("");
   }

   @Test
   public void testInvalidRank() {
      // '1' is an invalid rank
      assertInvalid("1S");
   }

   @Test
   public void testInvalidSuit() {
      // 'P' is an invalid suit
      assertInvalid("7P");
   }

   @Test
   public void testInvalidLength() {
      // each representation of a card has to be two characters (the first indicating the rank and the second indicating the suit)
      assertInvalid("2");
      assertInvalid("S");
      assertInvalid("22S");
      assertInvalid("AHH");
   }

   @Test
   public void testInvalidCardInSequence() {
      assertInvalid("7G 8D 9D"); // 1st card invalid
      assertInvalid("7D 8G 9D"); // 2nd card invalid
      assertInvalid("7D 8D 9G"); // 3rd card invalid
   }

   private void assertInvalid(String input) {
      assertThrows(IllegalArgumentException.class, () -> CardReader.toCards(input));
   }

   private void assertCard(String string, Rank rank, Suit suit) {
      Iterator<Card> itr = CardReader.toCards(string).iterator();
      assertCard(itr.next(), rank, suit);
      assertFalse(itr.hasNext());
   }

   private void assertCard(Card card, Rank rank, Suit suit) {
      assertSame(rank, card.getRank());
      assertSame(suit, card.getSuit());
   }
}
