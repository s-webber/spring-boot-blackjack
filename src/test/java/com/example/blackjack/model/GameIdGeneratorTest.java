package com.example.blackjack.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class GameIdGeneratorTest {
   @Test
   public void testFormat() {
      GameIdGenerator g = new GameIdGenerator();
      String id = g.generate();
      Pattern p = Pattern.compile("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}");
      Matcher m = p.matcher(id);
      assertTrue(m.matches(), () -> id);
   }

   @Test
   public void testUnique() {
      GameIdGenerator g = new GameIdGenerator();
      Set<String> ids = new HashSet<>();
      for (int i = 0; i < 10000; i++) {
         String id = g.generate();
         assertTrue(ids.add(id));
      }
   }
}
