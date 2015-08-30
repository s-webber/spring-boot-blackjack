package com.example.blackjack.model;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/** Signals that a request has been received for a combination of username and game ID that the system has no record of. */
@ResponseStatus(HttpStatus.NOT_FOUND)
public final class GameNotFoundException extends RuntimeException {
   public GameNotFoundException(String username, String gameId) {
      super("could not find game id " + gameId + " for the user: " + username);
   }
}
