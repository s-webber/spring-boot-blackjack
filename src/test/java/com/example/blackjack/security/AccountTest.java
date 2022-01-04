package com.example.blackjack.security;

import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class AccountTest {
   @Test
   public void test() {
      String username = "dummy user name";
      String password = "dummy password";
      Account a = new Account(username, password);
      assertSame(username, a.getUsername());
      assertSame(password, a.getPassword());
   }
}
