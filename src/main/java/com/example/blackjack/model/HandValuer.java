package com.example.blackjack.model;

import static com.example.blackjack.view.Rank.ACE;

import java.util.Collection;

import com.example.blackjack.view.Card;
import com.example.blackjack.view.Rank;

/** Provides methods for determining the value of a collection of cards. */
public final class HandValuer {
   /** The ideal value of a hand. Any hand with a value greater than this is considered bust. */
   private static final int TARGET = 21;
   /** The minimum value of the dealers completed hand. While the dealer's hand has a lower value than this then it must continue to add cards to it. */
   private static final int DEALERS_MINIMUM = 17;
   /**
    * The minimum value of the first card of a hand in order for that hand to have blackjack.
    * <p>
    * Blackjack requires an Ace (valued at 11) and a Ten, Jack, Queen or King (all valued at 10). Therefore the first card dealt for a hand must have a value of
    * at least 10 in order for it to to be possible for the hand to achieve blackjack when its second card is dealt.
    */
   private static final int FIRST_CARD_BLACKJACK_MINIMUM = 10;
   /** The high value that can be assigned to an Ace as an alternative to {@code #ACE_LOW_VALUE}. */
   private static final int ACE_HIGH_VALUE = ACE.getValue();
   /** The low value that can be assigned to an Ace as an alternative to {@code #ACE_HIGH_VALUE}. */
   private static final int ACE_LOW_VALUE = 1;

   /** Private constructor as all methods are static. */
   private HandValuer() {
      // do nothing
   }

   /** Returns the combined value of the given cards. */
   public static int value(Collection<Card> cards) {
      int value = 0;
      int numAces = 0;
      for (Card c : cards) {
         Rank r = c.getRank();
         if (isAce(r)) {
            numAces++;
         }
         value += r.getValue();
      }
      return treatAceAsLowIfNecessary(value, numAces);
   }

   /** Returns {@code true} if the given value is greater than 21, else {@code false}. */
   public static boolean isBust(int value) {
      return value > TARGET;
   }

   /** Returns {@code true} if the combined value of the given cards is greater than 21, else {@code false}. */
   public static boolean isBust(Collection<Card> cards) {
      return isBust(value(cards));
   }

   /** Returns {@code true} if the value of the combined value of the given cards equals 21, else {@code false}. */
   public static boolean isTarget(Collection<Card> cards) {
      return value(cards) == TARGET;
   }

   /** Returns {@code true} if the combined value of the given cards is below 17, else {@code false}. */
   public static boolean isBelowDealersMinimum(Collection<Card> dealersCards) {
      return value(dealersCards) < DEALERS_MINIMUM;
   }

   /**
    * Returns {@code true} if the given cards represents a blackjack, else {@code false}.
    * <p>
    * A hand is a blackjack if it contains two cards with a combined total of 21.
    */
   public static boolean isBlackjack(Collection<Card> cards) {
      return cards.size() == 2 && isTarget(cards);
   }

   /**
    * Returns {@code true} if it is possible to have a blackjack hand that contains the given card, else {@code false}.
    * <p>
    * For a hand to be a blackjack then every card it contains must have a value of at least 10.
    */
   public static boolean isPossibleBlackjack(Card card) {
      return card.getRank().getValue() >= FIRST_CARD_BLACKJACK_MINIMUM;
   }

   /**
    * Returns the optimal value for a hand - using the most appropriate values for any Aces the hand contains.
    * <p>
    * When valuing a hand most ranks are associated with a single value (e.g. a "7" is always worth 7, a King is always worth 10). The one exception to this
    * rule is the Ace. Each Ace can be valued as either a 1 (referred to as the "hard" value) or a 11 (referred to as the "soft" value). This method returns the
    * optimal value for the given {@code value} (which has been calculated by valuing all Aces as 11), which may involve re-valuing some of the Aces in the hand
    * as having a value of 1 instead.
    * <p>
    * Example: When valuing Aces as 11 a hand containing "6 Hearts", "Ace Diamonds" and "7 Spades" will have a value of 24 (6+11+7) - this is not a good outcome
    * as it is greater than the target value of 21 (i.e. it is bust). If the Ace is instead valued as 1 then the value of the hand will be 14, meaning the hand
    * is not bust.
    *
    * @param value
    *           the value of a hand where any Aces have been valued at 11
    * @param numAces
    *           the number of Aces contained in the hand that {@code value} was determined for
    * @return the optimal value for a hand - using the most appropriate values for any Aces the hand contains
    */
   private static int treatAceAsLowIfNecessary(int value, int numAces) {
      while (numAces > 0 && isBust(value)) {
         value -= ACE_HIGH_VALUE - ACE_LOW_VALUE;
         numAces--;
      }
      return value;
   }

   private static boolean isAce(Rank r) {
      return r == ACE;
   }
}
