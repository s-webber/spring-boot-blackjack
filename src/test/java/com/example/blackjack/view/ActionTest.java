package com.example.blackjack.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.example.blackjack.CardReader;
import com.example.blackjack.model.Game;

public class ActionTest {
   @Test
   public void testNumberOfActions() {
      assertEquals(2, Action.values().length);
   }

   @Test
   public void testHit() {
      Game game = mock(Game.class);
      GameState expected = createDummyGameState();
      when(game.hit()).thenReturn(expected);
      GameState actual = Action.HIT.update(game);
      assertSame(expected, actual);
   }

   @Test
   public void testStand() {
      Game game = mock(Game.class);
      GameState expected = createDummyGameState();
      when(game.stand()).thenReturn(expected);
      GameState actual = Action.STAND.update(game);
      assertSame(expected, actual);
   }

   private GameState createDummyGameState() {
      return new GameState("dummy game id", Status.PLAYERS_TURN, new Hand(CardReader.toCards("7D")), new Hand(CardReader.toCards("TC 6D")));
   }
}
