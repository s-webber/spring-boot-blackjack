package com.example.blackjack.view;

import static com.example.blackjack.CardReader.toCards;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class GameStateTest {
   @Test
   public void test() {
      Hand dealers = new Hand(toCards("AS 7C"));
      Hand players = new Hand(toCards("QH JD"));
      String gameId = "dummy game id";
      Status status = Status.DEALERS_TURN;
      GameState gameState = new GameState(gameId, status, dealers, players);
      assertSame(gameId, gameState.getId());
      assertSame(status, gameState.getStatus());
      assertSame(dealers, gameState.getDealer());
      assertSame(players, gameState.getPlayer());
   }
}
