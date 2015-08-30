package com.example.blackjack.view;

import java.util.function.Function;

import com.example.blackjack.model.Game;

/** Represents the actions a player can request to update a game. */
public enum Action {
   /**
    * Deals another card to the player.
    *
    * @see com.example.blackjack.model.Game#hit()
    */
   HIT(Game::hit),
   /**
    * Updates the status of a game to indicate that the player has completed their turn.
    *
    * @see com.example.blackjack.model.Game#stand()
    */
   STAND(Game::stand);

   private final Function<Game, GameState> logic;

   private Action(Function<Game, GameState> logic) {
      this.logic = logic;
   }

   /**
    * Updates the given game with the logic associated with this action.
    *
    * @param game
    *           the game to update
    * @return the updated state of the game as a result of this action
    * @throws com.example.blackjack.model.GameAlreadyCompleteException
    *            if the game is not in an appropriate state to be updated
    */
   public GameState update(Game game) {
      return logic.apply(game);
   }
}
