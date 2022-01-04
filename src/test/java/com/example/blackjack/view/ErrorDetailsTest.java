package com.example.blackjack.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class ErrorDetailsTest {
   @Test
   public void test() {
      int statusCode = 1;
      String error = "error";
      String message = "message";
      ErrorDetails e = new ErrorDetails(statusCode, error, message);
      assertEquals(statusCode, e.getStatus());
      assertSame(error, e.getError());
      assertSame(message, e.getMessage());
   }
}
