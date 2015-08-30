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
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;
import com.example.blackjack.view.Suit;

/**
 * Converts {@code String}s to collections of {@code Card}s.
 * <p>
 * Only added to make unit tests more concise (hence why it is in {@code src/test/java} rather than {@code src/main/java}).
 */
public class CardReader {
   /**
    * Returns a newly created collection of {@code Card}s based on the contents of the given {@code String}.
    * <p>
    * The given {@code String} needs to be a space separated sequence of two character codes. Each two character code represents a playing card with the rank
    * denoted by the first character (e.g. 2, 3, 4, 5, 6, 7, 8, 9, T, J, Q, K or A) and the suit denoted by the second character (e.g. S, H, D or C). For
    * example, the two character code "AH" represents the "Ace of Hearts" and "7D" represents the "Seven of Diamonds".
    *
    * @param cards
    *           a space separated sequence of two character codes e.g. {@code TH AC 3D KS}
    */
   public static List<Card> toCards(String cards) {
      return stream(cards.split(" ")).map(CardReader::toCard).collect(toList());
   }

   private static Card toCard(String card) {
      assertValidLength(card);
      char rankInitial = card.charAt(0);
      char suitInitial = card.charAt(1);
      return new Card(toRank(rankInitial), toSuit(suitInitial));
   }

   private static void assertValidLength(String card) {
      if (card.length() != 2) {
         throw new IllegalArgumentException("Invalid card: " + card);
      }
   }

   private static Rank toRank(char rankInitial) {
      switch (rankInitial) {
         case '2':
            return TWO;
         case '3':
            return THREE;
         case '4':
            return FOUR;
         case '5':
            return FIVE;
         case '6':
            return SIX;
         case '7':
            return SEVEN;
         case '8':
            return EIGHT;
         case '9':
            return NINE;
         case 'T':
            return TEN;
         case 'J':
            return JACK;
         case 'Q':
            return QUEEN;
         case 'K':
            return KING;
         case 'A':
            return ACE;
         default:
            throw new IllegalArgumentException("Cannot convert to rank: " + rankInitial);
      }
   }

   private static Suit toSuit(char suitInitial) {
      switch (suitInitial) {
         case 'S':
            return SPADES;
         case 'H':
            return HEARTS;
         case 'D':
            return DIAMONDS;
         case 'C':
            return CLUBS;
         default:
            throw new IllegalArgumentException("Cannot convert to suit: " + suitInitial);
      }
   }
}
