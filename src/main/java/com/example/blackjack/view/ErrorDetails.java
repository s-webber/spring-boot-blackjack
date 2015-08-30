package com.example.blackjack.view;

/** Represents details of an error that has occurred while trying to process a request. */
public final class ErrorDetails {
   private final int status;
   private final String error;
   private final String message;

   /**
    * @param status
    *           the HTTP status code
    * @param error
    *           the name of the error
    * @param message
    *           a description of the cause of the error
    */
   public ErrorDetails(int status, String error, String message) {
      this.status = status;
      this.error = error;
      this.message = message;
   }

   public int getStatus() {
      return status;
   }

   public String getError() {
      return error;
   }

   public String getMessage() {
      return message;
   }
}
