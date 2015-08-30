package com.example.blackjack.view;

/** Represents a specific state in the life cycle of a game. */
public enum Status {
   /**
    * Indicates that the player still has choices to make (i.e. to hit or stand).
    * <p>
    * This is the initial state of a game.
    */
   PLAYERS_TURN,
   /**
    * Indicates that the dealer is in the process of completing their hand.
    * <p>
    * This state is reached when a player has chosen to stand.
    */
   DEALERS_TURN,
   /**
    * Indicates that the game is complete and the outcome was a draw.
    */
   DRAW,
   /**
    * Indicates that the game is complete and the outcome was a win for the player (i.e. the dealer lost).
    */
   PLAYER_WON,
   /**
    * Indicates that the game is complete and the outcome was a win for the dealer (i.e. the player lost).
    */
   DEALER_WON
}
