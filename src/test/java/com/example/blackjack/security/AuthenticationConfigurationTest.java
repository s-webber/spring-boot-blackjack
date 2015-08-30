package com.example.blackjack.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class AuthenticationConfigurationTest {
   private AuthenticationConfiguration testObject;
   private AccountRepository mockAccountRepository;

   @Before
   public void setUp() {
      mockAccountRepository = mock(AccountRepository.class);
      testObject = new AuthenticationConfiguration();
      testObject.setAccountRepository(mockAccountRepository);
   }

   @Test
   public void testFound() throws Exception {
      String username = "dummy user name";
      String password = "password";
      when(mockAccountRepository.findByUsername(username)).thenReturn(Optional.of(new Account(username, password)));

      UserDetailsService userDetailsService = getUserDetailsService();

      UserDetails userDetails = userDetailsService.loadUserByUsername(username);
      assertEquals(username, userDetails.getUsername());
      assertEquals(password, userDetails.getPassword());
   }

   @Test
   public void testNotFound() throws Exception {
      String username = "dummy user name";
      when(mockAccountRepository.findByUsername(username)).thenReturn(Optional.empty());

      UserDetailsService userDetailsService = getUserDetailsService();
      try {
         userDetailsService.loadUserByUsername(username);
         fail();
      } catch (UsernameNotFoundException e) {
         assertEquals("could not find the user: dummy user name", e.getMessage());
      }
   }

   private UserDetailsService getUserDetailsService() throws Exception {
      AuthenticationManagerBuilder mockAuthenticationManagerBuilder = mock(AuthenticationManagerBuilder.class);

      testObject.init(mockAuthenticationManagerBuilder);

      ArgumentCaptor<UserDetailsService> argument = ArgumentCaptor.forClass(UserDetailsService.class);
      verify(mockAuthenticationManagerBuilder).userDetailsService(argument.capture());
      UserDetailsService userDetailsService = argument.getValue();
      return userDetailsService;
   }
}
