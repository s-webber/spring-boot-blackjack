package com.example.blackjack.view;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;
import static java.util.Objects.requireNonNull;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * An immutable snapshot of a specific stage of a blackjack game.
 *
 * @see com.example.blackjack.model.Game#snapshotCurrentState()
 */
public final class GameState {
   @Schema(description = "The identifier for this game.", requiredMode = REQUIRED)
   private final String id;

   @Schema(description = "The current state of this game.", requiredMode = REQUIRED)
   private final Status status;

   @Schema(description = "The cards currently held by the dealer.", requiredMode = REQUIRED)
   private final Hand dealer;

   @Schema(description = "The cards currently held by the player.", requiredMode = REQUIRED)
   private final Hand player;

   /**
    * @param id the identifier for the game
    * @param status the current state of the game
    * @param dealer the cards currently held by the dealer
    * @param player the cards currently held by the player
    * @throws NullPointerException if any of {@code id}, {@code status}, {@code dealer} or {@code player} is
    * {@code null}
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
