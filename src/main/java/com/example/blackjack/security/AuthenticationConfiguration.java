package com.example.blackjack.security;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/** Configures the use of an {@code AccountRepository} for authentication. */
@Configuration
class AuthenticationConfiguration extends GlobalAuthenticationConfigurerAdapter {
   private AccountRepository accountRepository;

   @Override
   public void init(AuthenticationManagerBuilder auth) throws Exception {
      auth.userDetailsService(userDetailsService());
   }

   private UserDetailsService userDetailsService() {
      return username -> {
         Optional<Account> optional = accountRepository.findByUsername(username);
         Account account = optional.orElseThrow(() -> new UsernameNotFoundException("could not find the user: " + username));
         return new User(account.getUsername(), account.getPassword(), Collections.emptyList());
      };
   }

   @Autowired
   void setAccountRepository(AccountRepository accountRepository) {
      this.accountRepository = accountRepository;
   }
}
