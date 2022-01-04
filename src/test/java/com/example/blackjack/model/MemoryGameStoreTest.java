package com.example.blackjack.model;

import static com.example.blackjack.CardReader.toCards;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemoryGameStoreTest {
   private static final String DUMMY_USER_NAME = "dummy user name";
   private static final String DUMMY_GAME_ID = "d8558043-3cbd-4edb-9f86-75ec5a32047f";

   private GameStore testObject;
   private GameIdGenerator mockIdGenerator;
   private DeckFactory mockDeckFactory;

   @BeforeEach
   public void setUp() {
      mockIdGenerator = mock(GameIdGenerator.class);
      mockDeckFactory = mock(DeckFactory.class);

      // Deck will need to contain cards so newly created games can deal 3 cards - 1 to the dealer and 2 to the player.
      // Provide 12 cards (the ranks and suits are not important) as the most games created by a single test is 4 (and 3x4=12).
      Deck dummyDeck = new Deck(toCards("2S 3S 4S 2H 3H 4H 2D 3D 4D 2C 3C 4C"));
      when(mockDeckFactory.createDeck()).thenReturn(dummyDeck);

      testObject = new MemoryGameStore(mockIdGenerator, mockDeckFactory);
   }

   @Test
   public void testCreateGame() {
      when(mockIdGenerator.generate()).thenReturn(DUMMY_GAME_ID);
      Game game = testObject.createGame(DUMMY_USER_NAME);
      assertEquals(DUMMY_GAME_ID, game.getId());
   }

   @Test
   public void testCreateGame_DuplicateGameId() {
      when(mockIdGenerator.generate()).thenReturn(DUMMY_GAME_ID);

      // create first game
      assertEquals(DUMMY_GAME_ID, testObject.createGame(DUMMY_USER_NAME).getId());

      // assert exception thrown if try to create new game with already used game id
      try {
         testObject.createGame(DUMMY_USER_NAME);
         fail();
      } catch (IllegalArgumentException e) {
         assertEquals("game already exists with game id " + DUMMY_GAME_ID, e.getMessage());
      }
   }

   @Test
   public void testGetExistingGame() {
      when(mockIdGenerator.generate()).thenReturn(DUMMY_GAME_ID);
      Game game = testObject.createGame(DUMMY_USER_NAME);
      assertSame(game, testObject.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID));
   }

   @Test
   public void testGetExistingGame_DoesNotExist() {
      assertGameNotFound(testObject, DUMMY_USER_NAME, DUMMY_GAME_ID);
   }

   @Test
   public void testGetExistingGame_WrongUser() {
      when(mockIdGenerator.generate()).thenReturn(DUMMY_GAME_ID);
      Game game = testObject.createGame(DUMMY_USER_NAME);
      assertGameNotFound(testObject, "wrong user name", game.getId());
   }

   @Test
   public void testGetExistingGame_MultipleGames() {
      final String gameId1 = "11111";
      final String gameId2 = "22222";
      when(mockIdGenerator.generate()).thenReturn(gameId1, gameId2);

      Game game1 = testObject.createGame(DUMMY_USER_NAME);
      Game game2 = testObject.createGame(DUMMY_USER_NAME);

      assertSame(game1, testObject.findByUsernameAndGameId(DUMMY_USER_NAME, gameId1));
      assertSame(game2, testObject.findByUsernameAndGameId(DUMMY_USER_NAME, gameId2));
   }

   @Test
   public void testGetExistingGame_MultipleUsers() {
      final String username1 = "user1";
      final String username2 = "user2";
      final String gameId1 = "11111";
      final String gameId2 = "22222";
      when(mockIdGenerator.generate()).thenReturn(gameId1, gameId2);

      Game game1 = testObject.createGame(username1);
      Game game2 = testObject.createGame(username2);

      assertSame(game1, testObject.findByUsernameAndGameId(username1, gameId1));
      assertSame(game2, testObject.findByUsernameAndGameId(username2, gameId2));
      assertGameNotFound(testObject, username1, gameId2);
      assertGameNotFound(testObject, username2, gameId1);
   }

   @Test
   public void testFindGameIdsByUsername() {
      final String username1 = "user1";
      final String username2 = "user2";
      final String gameId1 = "11111";
      final String gameId2 = "22222";
      final String gameId3 = "33333";
      final String gameId4 = "44444";

      when(mockIdGenerator.generate()).thenReturn(gameId1, gameId2, gameId3, gameId4);

      assertTrue(testObject.findGameIdsByUsername(username1).isEmpty());

      testObject.createGame(username1);

      assertSet(testObject.findGameIdsByUsername(username1), gameId1);
      assertTrue(testObject.findGameIdsByUsername(username2).isEmpty());

      testObject.createGame(username2);
      testObject.createGame(username1);
      testObject.createGame(username1);

      assertSet(testObject.findGameIdsByUsername(username1), gameId1, gameId3, gameId4);
      assertSet(testObject.findGameIdsByUsername(username2), gameId2);
   }

   private void assertSet(Set<String> set, String... contents) {
      assertEquals(contents.length, set.size());
      assertTrue(set.containsAll(asList(contents)));
   }

   private void assertGameNotFound(GameStore gameStore, String username, String gameId) {
      try {
         gameStore.findByUsernameAndGameId(username, gameId);
         fail();
      } catch (GameNotFoundException e) {
         assertEquals("could not find game id " + gameId + " for the user: " + username, e.getMessage());
      }
   }
}
