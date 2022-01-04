package com.example.blackjack.security;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/** Retrieves account details of users. */
@Component
class AccountRepository {
   private static final String PASSWORD = new BCryptPasswordEncoder().encode("password");

   /**
    * Returns the {@code Account} for the given username.
    * <p>
    * <b>NOTE:</b> The current implementation will <i>always</i> return an account, regardless of the value of the given
    * username, that has a password with the hardcoded value of {@code password}. Obviously this approach is <i>not</i>
    * suitable for use in a production system. TODO Use a store (e.g. LDAP server or SQL database) to lookup and
    * retrieve account details for the given username.
    *
    * @param username used to identify the account to be retrieved
    * @return an {@code Optional} containing the {@code Account}, or an empty {@code Optional} if no account was found
    * for the given username
    */
   Optional<Account> findByUsername(String username) {
      return Optional.of(new Account(username, PASSWORD));
   }
}
