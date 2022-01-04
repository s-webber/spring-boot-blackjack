package com.example.blackjack.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class AccountRepositoryTest {
   /** Copied from org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder */
   private Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$(\\d\\d)\\$[./0-9A-Za-z]{53}");

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
      assertTrue(BCRYPT_PATTERN.matcher(account.getPassword()).matches());
   }
}
