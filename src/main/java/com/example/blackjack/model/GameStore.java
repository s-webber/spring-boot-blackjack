package com.example.blackjack.model;

import java.util.Set;

/** Provides a mechanism for creating new games and finding existing games. */
public interface GameStore {
   /** Returns a newly created {@code Game} for the given username. */
   Game createGame(String username);

   /**
    * Returns an existing game with the given username and game ID.
    *
    * @throws GameNotFoundException
    *            if no game is found with a game ID of {@code gameId} which belongs to the user identified by {@code username}
    */
   Game findByUsernameAndGameId(String username, String gameId);

   /** Returns the IDs of all games belonging to the given username. */
   Set<String> findGameIdsByUsername(String username);
}
