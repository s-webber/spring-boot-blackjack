package com.example.blackjack.controller;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.blackjack.model.Game;
import com.example.blackjack.model.GameAlreadyCompleteException;
import com.example.blackjack.model.GameNotFoundException;
import com.example.blackjack.model.GameStore;
import com.example.blackjack.view.Card;
import com.example.blackjack.view.GameState;
import com.example.blackjack.view.Hand;
import com.example.blackjack.view.Rank;
import com.example.blackjack.view.Status;
import com.example.blackjack.view.Suit;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@SpringBootTest
public class BlackjackControllerTest {
   private static final String AUTHORIZATION_HEADER = "Authorization";
   private static final String BLACKJACK_PATH = "/blackjack";
   private static final String DUMMY_GAME_ID = "1fb8aae0-0305-4088-9866-769a7a1a37a8";
   private static final String DUMMY_GAME_ID_PATH = BLACKJACK_PATH + "/" + DUMMY_GAME_ID;
   private static final String DUMMY_GAME_ID_LOCATION = "http://localhost:8080" + DUMMY_GAME_ID_PATH;
   private static final String DUMMY_USER_NAME = "username";
   private static final String BASIC_DIGEST_HEADER_VALUE = createBasicDigestHeaderValue(DUMMY_USER_NAME, "password");
   private static final String DUMMY_RESPONSE = "{\"id\":\"1fb8aae0-0305-4088-9866-769a7a1a37a8\",\"status\":\"PLAYERS_TURN\","
                                                + "\"dealer\":{\"cards\":[{\"rank\":\"ACE\",\"suit\":\"SPADES\"}],\"value\":11},"
                                                + "\"player\":{\"cards\":[{\"rank\":\"EIGHT\",\"suit\":\"DIAMONDS\"},{\"rank\":\"QUEEN\",\"suit\":\"HEARTS\"}],\"value\":18}}";

   private RestDocumentationResultHandler document;
   /** This is required so tests use spring security */
   @Autowired
   private FilterChainProxy springSecurityFilterChain;
   @Autowired
   private WebApplicationContext webApplicationContext;
   @MockBean
   private GameStore gameStore;
   private MockMvc mockMvc;

   @BeforeEach
   public void setUp(RestDocumentationContextProvider restDocumentation) {
      System.out.println("RestDocumentationExtension " + restDocumentation);
      document = document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));
      mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).apply(documentationConfiguration(restDocumentation))
                  .alwaysDo(document).build();
   }

   /** Test that requests to list games must include valid authentication details. */
   @Test
   public void listGames_AuthenticateFail() throws Exception {
      assertAuthenticateFailure(() -> get(BLACKJACK_PATH));
   }

   /** Test that requests to create a new game must include valid authentication details. */
   @Test
   public void newGame_AuthenticateFail() throws Exception {
      assertAuthenticateFailure(() -> post(BLACKJACK_PATH));
   }

   /** Test that requests to view a game must include valid authentication details. */
   @Test
   public void viewGame_AuthenticateFail() throws Exception {
      assertAuthenticateFailure(() -> get(DUMMY_GAME_ID_PATH));
   }

   /** Test that requests to update a game must include valid authentication details. */
   @Test
   public void updateGame_AuthenticateFail() throws Exception {
      assertAuthenticateFailure(() -> post(DUMMY_GAME_ID_PATH));
   }

   /** Test that DELETE requests are not supported. */
   @Test
   public void deleteNotSupported() throws Exception {
      assertMethodNotAllowed(() -> delete(BLACKJACK_PATH));
      assertMethodNotAllowed(() -> delete(DUMMY_GAME_ID_PATH));
   }

   /** Test that PUT requests are not supported. */
   @Test
   public void putNotSupported() throws Exception {
      assertMethodNotAllowed(() -> put(BLACKJACK_PATH));
      assertMethodNotAllowed(() -> put(DUMMY_GAME_ID_PATH));
   }

   /** Test that a 404 Not Found response is returned for view requests that specify a game that does not exist. */
   @Test
   public void viewGame_NotFound() throws Exception {
      assertGameNotFound(() -> get(DUMMY_GAME_ID_PATH));
   }

   /** Test that a 404 Not Found response is returned for update requests that specify a game that does not exist. */
   @Test
   public void updateGame_NotFound() throws Exception {
      assertGameNotFound(() -> post(DUMMY_GAME_ID_PATH));
   }

   /** Test requesting a the creation of a new game. */
   @Test
   public void newGame() throws Exception {
      Game mockGame = createMockGame();
      when(gameStore.createGame(DUMMY_USER_NAME)).thenReturn(mockGame);
      when(mockGame.snapshotCurrentState()).thenReturn(createDummyGameState());

      MvcResult result = mockMvc.perform(post(BLACKJACK_PATH).header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE)).andExpect(status().isCreated())
                  .andExpect(header().string("Location", DUMMY_GAME_ID_LOCATION)).andReturn();
      assertResponse(result);
      verifySnapshotCurrentStateCalled(mockGame);
   }

   /** Test requesting a the retrieval of an existing game's state. */
   @Test
   public void viewGame() throws Exception {
      Game mockGame = createMockGame();
      when(gameStore.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID)).thenReturn(mockGame);
      when(mockGame.snapshotCurrentState()).thenReturn(createDummyGameState());

      MvcResult result = mockMvc.perform(get(DUMMY_GAME_ID_PATH).header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE)).andExpect(status().isOk()).andReturn();
      assertResponse(result);
      verifySnapshotCurrentStateCalled(mockGame);
   }

   /** Test that a 400 Bad Request response is returned for update requests that do not specify an action. */
   @Test
   public void updateGame_NoActionParameter() throws Exception {
      MvcResult result = mockMvc.perform(post(DUMMY_GAME_ID_PATH).header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE)).andExpect(status().isBadRequest()).andReturn();
      assertBadRequestException(MissingServletRequestParameterException.class, "Required request parameter 'action' for method parameter type Action is not present", result);
   }

   /** Test that a 400 Bad Request response is returned for update requests that contain an unknown action value. */
   @Test
   public void updateGame_InvalidActionParameter() throws Exception {
      MvcResult result = requestUpdateWithAction("DOUBLE").andExpect(status().isBadRequest()).andReturn();
      String expectedExceptionMessage = "Failed to convert value of type 'java.lang.String' to required type 'com.example.blackjack.view.Action'; "
                                        + "nested exception is org.springframework.core.convert.ConversionFailedException: Failed to convert from type [java.lang.String] to type "
                                        + "[@org.springframework.web.bind.annotation.RequestParam @io.swagger.annotations.ApiParam com.example.blackjack.view.Action] for value 'DOUBLE'; "
                                        + "nested exception is java.lang.IllegalArgumentException: No enum constant com.example.blackjack.view.Action.DOUBLE";
      assertBadRequestException(MethodArgumentTypeMismatchException.class, expectedExceptionMessage, result);
   }

   /**
    * Test that a 405 Method Not Allowed response is returned for "hit" update requests for games that are already
    * complete.
    */
   @Test
   public void updateGame_Hit_InvalidState() throws Exception {
      GameAlreadyCompleteException gameAlreadyCompleteException = new GameAlreadyCompleteException(DUMMY_GAME_ID);
      Game mockGame = createMockGame();
      when(gameStore.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID)).thenReturn(mockGame);
      when(mockGame.hit()).thenThrow(gameAlreadyCompleteException);

      MvcResult result = requestHit().andExpect(status().isMethodNotAllowed()).andReturn();
      assertException(gameAlreadyCompleteException, result);
      verifyHitCalled(mockGame);
   }

   /**
    * Test that a 405 Method Not Allowed response is returned for "stand" update requests for games that are already
    * complete.
    */
   @Test
   public void updateGame_Stand_InvalidState() throws Exception {
      GameAlreadyCompleteException gameAlreadyCompleteException = new GameAlreadyCompleteException(DUMMY_GAME_ID);
      Game mockGame = createMockGame();
      when(gameStore.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID)).thenReturn(mockGame);
      when(mockGame.stand()).thenThrow(gameAlreadyCompleteException);

      MvcResult result = requestStand().andExpect(status().isMethodNotAllowed()).andReturn();
      assertException(gameAlreadyCompleteException, result);
      verifyStandCalled(mockGame);
   }

   /** Test requesting a game is updated with a HIT action. */
   @Test
   public void updateGame_Hit() throws Exception {
      Game mockGame = createMockGame();
      when(gameStore.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID)).thenReturn(mockGame);
      when(mockGame.hit()).thenReturn(createDummyGameState());

      MvcResult result = requestHit().andExpect(status().isOk()).andReturn();
      assertResponse(result);
      verifyHitCalled(mockGame);
   }

   /** Test requesting a game is updated with a STAND action. */
   @Test
   public void updateGame_Stand() throws Exception {
      Game mockGame = createMockGame();
      when(gameStore.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID)).thenReturn(mockGame);
      when(mockGame.stand()).thenReturn(createDummyGameState());

      MvcResult result = requestStand().andExpect(status().isOk()).andReturn();
      assertResponse(result);
      verifyStandCalled(mockGame);
   }

   /** Test requests to list games for a user who does not currently have any games. */
   @Test
   public void listGames_Empty() throws Exception {
      when(gameStore.findGameIdsByUsername(DUMMY_USER_NAME)).thenReturn(Collections.emptySet());
      MvcResult result = performListGames();
      assertResponse("[]", result);
   }

   /** Test requests to list games for a user who has multiple games. */
   @Test
   public void listGames() throws Exception {
      Set<String> gameIds = new LinkedHashSet<>(asList());
      gameIds.add("d441c5be-5820-400a-8f69-fe14da65e883");
      gameIds.add("7bcae0a1-6b50-42cb-bb5c-b62a927aa68a");
      gameIds.add("b586176b-1f9a-42fd-9fa8-5d015083c125");
      when(gameStore.findGameIdsByUsername(DUMMY_USER_NAME)).thenReturn(gameIds);

      MvcResult result = performListGames();
      assertResponse("[\"d441c5be-5820-400a-8f69-fe14da65e883\",\"7bcae0a1-6b50-42cb-bb5c-b62a927aa68a\",\"b586176b-1f9a-42fd-9fa8-5d015083c125\"]", result);
   }

   private void assertAuthenticateFailure(Supplier<MockHttpServletRequestBuilder> supplier) throws Exception {
      assertInvalidPassword(supplier);
      assertNoAuthorizationDetails(supplier);
   }

   private void assertInvalidPassword(Supplier<MockHttpServletRequestBuilder> supplier) throws Exception {
      String incorrectPasswordBasicDigestHeaderValue = createBasicDigestHeaderValue(DUMMY_USER_NAME, "invalid password");
      mockMvc.perform(supplier.get().header(AUTHORIZATION_HEADER, incorrectPasswordBasicDigestHeaderValue)).andExpect(status().isUnauthorized()).andReturn();
   }

   private void assertNoAuthorizationDetails(Supplier<MockHttpServletRequestBuilder> supplier) throws Exception {
      mockMvc.perform(supplier.get()).andExpect(status().isUnauthorized()).andReturn();
   }

   private void assertMethodNotAllowed(Supplier<MockHttpServletRequestBuilder> supplier) throws Exception {
      mockMvc.perform(supplier.get().header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE)).andExpect(status().isMethodNotAllowed()).andReturn();
   }

   private void assertGameNotFound(Supplier<MockHttpServletRequestBuilder> supplier) throws Exception {
      GameNotFoundException gameNotFoundException = new GameNotFoundException(DUMMY_USER_NAME, DUMMY_GAME_ID);
      when(gameStore.findByUsernameAndGameId(DUMMY_USER_NAME, DUMMY_GAME_ID)).thenThrow(gameNotFoundException);
      MvcResult result = mockMvc.perform(get(DUMMY_GAME_ID_PATH).header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE)).andExpect(status().isNotFound()).andReturn();
      assertException(gameNotFoundException, result);
   }

   private void assertException(Throwable expected, MvcResult actualResult) {
      Exception actualException = actualResult.getResolvedException();
      assertNotNull(actualException);
      assertSame(expected, actualException);
   }

   private void verifyStandCalled(Game mockGame) {
      verifyGameMethodCalls(mockGame, 1, 0, 0);
   }

   private void verifyHitCalled(Game mockGame) {
      verifyGameMethodCalls(mockGame, 0, 1, 0);
   }

   private void verifySnapshotCurrentStateCalled(Game mockGame) {
      verifyGameMethodCalls(mockGame, 0, 0, 1);
   }

   private void verifyGameMethodCalls(Game mockGame, int standCtr, int hitCtr, int snapshotCtr) {
      verify(mockGame, times(standCtr)).stand();
      verify(mockGame, times(hitCtr)).hit();
      verify(mockGame, times(snapshotCtr)).snapshotCurrentState();
   }

   private ResultActions requestHit() throws Exception {
      return requestUpdateWithAction("HIT");
   }

   private ResultActions requestStand() throws Exception {
      return requestUpdateWithAction("STAND");
   }

   private ResultActions requestUpdateWithAction(String actionName) throws Exception {
      return mockMvc.perform(post(DUMMY_GAME_ID_PATH).header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE).param("action", actionName));
   }

   private MvcResult performListGames() throws Exception {
      return mockMvc.perform(get(BLACKJACK_PATH).header(AUTHORIZATION_HEADER, BASIC_DIGEST_HEADER_VALUE)).andExpect(status().isOk()).andReturn();
   }

   private void assertBadRequestException(Class<? extends Throwable> expectedException, String exectedMessage, MvcResult result) {
      Throwable actualException = result.getResolvedException();
      assertNotNull(actualException);
      assertSame(expectedException, actualException.getClass());
      assertEquals(exectedMessage, actualException.getMessage());
   }

   private void assertResponse(MvcResult result) throws UnsupportedEncodingException {
      assertResponse(DUMMY_RESPONSE, result);
   }

   private void assertResponse(String expected, MvcResult result) throws UnsupportedEncodingException {
      assertEquals(expected, result.getResponse().getContentAsString());
   }

   private static String createBasicDigestHeaderValue(String username, String password) {
      String credentials = username + ":" + password;
      String encodedCredentials = base64Encode(credentials);
      return "Basic " + encodedCredentials;
   }

   private static String base64Encode(String credentials) {
      return Base64.getEncoder().encodeToString(credentials.getBytes(Charset.defaultCharset()));
   }

   private Game createMockGame() {
      Game mockGame = mock(Game.class);
      when(mockGame.getId()).thenReturn(DUMMY_GAME_ID);
      return mockGame;
   }

   private GameState createDummyGameState() {
      List<Card> dealersHand = asList(new Card(Rank.ACE, Suit.SPADES));
      List<Card> playersHand = asList(new Card(Rank.EIGHT, Suit.DIAMONDS), new Card(Rank.QUEEN, Suit.HEARTS));
      GameState gameState = new GameState(DUMMY_GAME_ID, Status.PLAYERS_TURN, new Hand(dealersHand), new Hand(playersHand));
      return gameState;
   }
}
