package com.example.blackjack.model;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

public class ShuffleStrategyTest {
   @Test
   public void test() {
      ShuffleStrategy<Integer> s = new ShuffleStrategy<Integer>();

      List<Integer> input = unmodifiableList(IntStream.range(0, 100).mapToObj(Integer::valueOf).collect(toList()));
      List<Integer> output1 = s.shuffle(input);
      List<Integer> output2 = s.shuffle(input);

      assertNotEquals(input, output1);
      assertNotEquals(input, output2);
      assertNotEquals(output1, output2);
      assertContainSameElements(input, output1);
      assertContainSameElements(input, output2);
   }

   private void assertContainSameElements(List<?> list1, List<?> list2) {
      assertEquals(list1.size(), list2.size());
      list1.stream().forEach(i -> assertTrue(list2.contains(i)));
   }
}
