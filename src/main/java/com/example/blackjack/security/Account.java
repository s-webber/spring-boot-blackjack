package com.example.blackjack.security;

/** Represents the account details of a user. */
final class Account {
   private final String username;
   private final String password;

   Account(String username, String password) {
      this.username = username;
      this.password = password;
   }

   String getUsername() {
      return username;
   }

   String getPassword() {
      return password;
   }
}
