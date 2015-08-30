package com.example.blackjack.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class AccountRepositoryTest {
   private AccountRepository testObject = new AccountRepository();

   @Test
   public void test() {
      assertFindByUsername("qwerty");
      assertFindByUsername("asdf");
      assertFindByUsername("zxcvbnm");
   }

   private void assertFindByUsername(String username) {
      Account account = testObject.findByUsername(username).get();
      assertNotNull(account);
      assertEquals(username, account.getUsername());
      assertEquals("password", account.getPassword());
   }
}
