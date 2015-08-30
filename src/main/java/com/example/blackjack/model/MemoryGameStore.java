package com.example.blackjack.model;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides an in-memory store of games.
 * <p>
 * <b>NOTE:</b> This approach is <i>not</i> suitable for use in a production system. Weaknesses include:
 * <ul>
 * <li>Memory usage. The in-memory data structure is added to each time a new game is created but no games are ever removed, even once they have completed. This
 * means that eventually, as new games continue to get created, the memory requirements will rise until they become impractical.</li>
 * <li>Not scalable. If an attempt was made to scale the service by adding new servers then that would introduce the problem that each instance would have its
 * own independent store. Requests to a server to update a game that was created by another server would result in a {@link GameNotFoundException} (404)
 * exception.</li>
 * <li>Not persistent. Details of games are not persisted between server restarts - meaning the service would not recover well from machine failure.</li>
 * </ul>
 * TODO: Provide an implementation of {@code GameStore} that uses a persistent shared store.
 */
@Component
final class MemoryGameStore implements GameStore {
   /**
    * A collection of games grouped by username.
    * <p>
    * The key is the username, the value is a map of games belonging to that user where the key is the game ID and the value is the corresponding {@code Game}.
    * Referring to concrete {@code ConcurrentHashMap} implementation, rather than {@code Map} interface, as want to make it explicit that a ConcurrentHashMap is
    * being used - as it is required to avoid {@code ConcurrentModificationException}s.
    * </p>
    */
   private ConcurrentHashMap<String, ConcurrentHashMap<String, Game>> store = new ConcurrentHashMap<String, ConcurrentHashMap<String, Game>>();

   private final GameIdGenerator gameIdGenerator;
   private final DeckFactory deckFactory;

   @Autowired
   public MemoryGameStore(GameIdGenerator gameIdGenerator, DeckFactory deckFactory) {
      this.gameIdGenerator = requireNonNull(gameIdGenerator);
      this.deckFactory = requireNonNull(deckFactory);
   }

   @Override
   public Game createGame(String username) {
      Game newGame = createGame();
      addToStore(username, newGame);
      return newGame;
   }

   private Game createGame() {
      return new Game(gameIdGenerator.generate(), deckFactory.createDeck());
   }

   private void addToStore(String username, Game newGame) {
      Map<String, Game> gamesByUser = getExistingGamesForUser(username);
      Game existingGame = gamesByUser.putIfAbsent(newGame.getId(), newGame);

      if (nonNull(existingGame)) {
         // should never get here as the game id of the newly created game should always be unique - but sanity check anyway
         throw new IllegalArgumentException("game already exists with game id " + newGame.getId());
      }
   }

   private ConcurrentHashMap<String, Game> getExistingGamesForUser(String username) {
      return store.computeIfAbsent(username, s -> new ConcurrentHashMap<String, Game>());
   }

   @Override
   public Game findByUsernameAndGameId(String username, String gameId) {
      Map<String, Game> gamesByUser = store.get(username);
      requireNonNullGame(gamesByUser, username, gameId);

      Game game = gamesByUser.get(gameId);
      requireNonNullGame(game, username, gameId);

      return game;
   }

   private void requireNonNullGame(Object o, String username, String gameId) {
      if (isNull(o)) {
         throw new GameNotFoundException(username, gameId);
      }
   }

   @Override
   public Set<String> findGameIdsByUsername(String username) {
      // OK to call keySet as using ConcurrentHashMap so will not get ConcurrentModificationException
      return new HashSet<>(getExistingGamesForUser(username).keySet());
   }
}
