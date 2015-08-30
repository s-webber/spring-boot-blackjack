package com.example.blackjack.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.blackjack.model.Game;
import com.example.blackjack.model.GameStore;
import com.example.blackjack.view.Action;
import com.example.blackjack.view.ErrorDetails;
import com.example.blackjack.view.GameState;

/** The {@code RestController} which provides the API to create and play blackjack games. */
@Api("An API for playing the blackjack card game.")
@RestController
@RequestMapping("/blackjack")
public final class BlackjackController {
   private static final Logger LOG = LoggerFactory.getLogger(BlackjackController.class);

   /** Used to create and retrieve games. */
   @Autowired
   private GameStore gameStore;

   @RequestMapping(method = RequestMethod.GET)
   @ApiOperation(value = "Returns IDs of games belonging to the given user.", notes = "All games for the given user, regardless of if they have already been completed, will be returned.")
   @ApiResponses(@ApiResponse(code = 200, message = "OK"))
   public Set<String> listGames(@AuthenticationPrincipal @ApiParam("The user to find games for.") User activeUser) {
      return gameStore.findGameIdsByUsername(activeUser.getUsername());
   }

   @RequestMapping(method = RequestMethod.POST)
   @ResponseStatus(HttpStatus.CREATED)
   @ApiOperation(value = "Creates a new game.", notes = "Two cards will automatically be dealt to the player and one to the dealer. If the player has blackjack then the game will immediately move to a completed state.")
   @ApiResponses(@ApiResponse(code = 201, message = "A new game has been succesfully created."))
   public GameState newGame(@AuthenticationPrincipal @ApiParam("The user to create a new game for.") User activeUser, HttpServletRequest request,
         HttpServletResponse response) {
      String username = activeUser.getUsername();
      Game game = gameStore.createGame(username);
      LOG.info("username: " + username + " has created: " + game.getId());
      response.setHeader("Location", createLocationHeaderValue(request, game));
      return game.snapshotCurrentState();
   }

   /**
    * Returns the value to set for the {@code Location} header of the response.
    * <p>
    * The path to the newly created game resource. e.g. {@code http://localhost:8080/blackjack/d344d4ec-00fc-42e0-b2f1-f5e9f77e69c8}
    */
   private String createLocationHeaderValue(HttpServletRequest request, Game game) {
      return request.getRequestURL().append("/").append(game.getId()).toString();
   }

   @RequestMapping(path = "/{gameId}", method = RequestMethod.GET)
   @ApiOperation(value = "Retrieves the state of an existing game.", notes = "Returns a snapshot of the current stage of a game.")
   @ApiResponses({ @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 404, message = "A game for the given ID and user cannot be found.", response = ErrorDetails.class) })
   public GameState viewGame(@PathVariable("gameId") @ApiParam("The ID of the game that is to be retrieved.") String gameId,
         @AuthenticationPrincipal @ApiParam("The user who owns the game that will be retrieved.") User activeUser) {
      Game game = gameStore.findByUsernameAndGameId(activeUser.getUsername(), gameId);
      return game.snapshotCurrentState();
   }

   @RequestMapping(path = "/{gameId}", method = RequestMethod.POST)
   @ApiOperation(value = "Updates an existing game.", notes = "A game will only be updatable if its current status is PLAYERS_TURN.")
   @ApiResponses({
      @ApiResponse(code = 200, message = "OK"),
      @ApiResponse(code = 404, message = "A game for the given ID and user cannot be found.", response = ErrorDetails.class),
      @ApiResponse(code = 405, message = "The game with the given ID is not in an appropriate state to be updated. A game will only be updatable if its current status is PLAYERS_TURN.", response = ErrorDetails.class) })
   public GameState updateGame(
         @PathVariable("gameId") @ApiParam("The ID of the game that is to be updated.") String gameId,
         @RequestParam("action") @ApiParam(value = "The action to perform on the game that is to be updated. HIT indicates that the player requests another card. STAND indicates that the player requests to end their turn.", required = true) Action action,
         @AuthenticationPrincipal @ApiParam("The user who owns the game that will be updated.") User activeUser) {
      String username = activeUser.getUsername();
      LOG.info("username: " + username + " game: " + gameId + " action: " + action);
      Game game = gameStore.findByUsernameAndGameId(username, gameId);
      return action.update(game);
   }
}
