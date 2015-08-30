package com.example.blackjack.view;

import static java.util.Objects.requireNonNull;
import io.swagger.annotations.ApiModelProperty;

/**
 * An immutable snapshot of a specific stage of a blackjack game.
 *
 * @see com.example.blackjack.model.Game#snapshotCurrentState()
 */
public final class GameState {
   @ApiModelProperty(value = "The identifier for this game.", required = true)
   private final String id;
   @ApiModelProperty(value = "The current state of this game.", required = true)
   private final Status status;
   @ApiModelProperty(value = "The cards currently held by the dealer.", required = true)
   private final Hand dealer;
   @ApiModelProperty(value = "The cards currently held by the player.", required = true)
   private final Hand player;

   /**
    * @param id
    *           the identifier for the game
    * @param status
    *           the current state of the game
    * @param dealer
    *           the cards currently held by the dealer
    * @param player
    *           the cards currently held by the player
    * @throws NullPointerException
    *            if any of {@code id}, {@code status}, {@code dealer} or {@code player} is {@code null}
    */
   public GameState(String id, Status status, Hand dealer, Hand player) {
      this.id = requireNonNull(id);
      this.status = requireNonNull(status);
      this.dealer = requireNonNull(dealer);
      this.player = requireNonNull(player);
   }

   public String getId() {
      return id;
   }

   public Status getStatus() {
      return status;
   }

   public Hand getDealer() {
      return dealer;
   }

   public Hand getPlayer() {
      return player;
   }
}
