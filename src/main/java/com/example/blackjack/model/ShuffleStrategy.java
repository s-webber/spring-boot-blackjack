package com.example.blackjack.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Provides a mechanism for randomly shuffling elements of a list.
 * <p>
 * <b>NOTE:</b> In a production system, involving the gambling of real money, the "randomness" provided by {@code Collections.shuffle(List)} may be deemed
 * insufficient and a hardware random number generator preferred instead. TODO Make this an interface so new strategies can be added easily.
 */
@Component
class ShuffleStrategy<T> {
   /**
    * Returns a randomly shuffled copy of the given list.
    *
    * @param input
    *           a list containing the elements to be shuffled
    * @return a randomly shuffled copy of {@code input}
    */
   List<T> shuffle(List<T> input) {
      List<T> copy = new ArrayList<>(input);
      Collections.shuffle(copy);
      return copy;
   }
}
