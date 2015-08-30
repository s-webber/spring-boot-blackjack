package com.example.blackjack.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Signals that a request has been received to update a game that is not in the appropriate state. */
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public final class GameAlreadyCompleteException extends RuntimeException {
   public GameAlreadyCompleteException(String gameId) {
      super("cannot update already completed game with id " + gameId);
   }
}
