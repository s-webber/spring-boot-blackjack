package com.example.blackjack.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;

import com.example.blackjack.view.ErrorDetails;

public class CustomErrorControllerTest {
   @Test
   public void testGetPath() {
      assertEquals("/error", new CustomErrorController().getErrorPath());
   }

   @Test
   public void testErrorDetails() {
      int expectedStatus = 42;
      String expectedError = "error details";
      String expectedMessage = "message details";
      HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);
      when(mockHttpServletResponse.getStatus()).thenReturn(expectedStatus);
      Map<String, Object> errorMap = new HashMap<>();
      errorMap.put("error", expectedError);
      errorMap.put("message", expectedMessage);
      ErrorAttributes mockErrorAttributes = mock(ErrorAttributes.class);
      when(mockErrorAttributes.getErrorAttributes(any(), eq(false))).thenReturn(errorMap);

      CustomErrorController testObject = new CustomErrorController();
      testObject.setErrorAttributes(mockErrorAttributes);
      ErrorDetails result = testObject.error(mock(HttpServletRequest.class), mockHttpServletResponse);

      assertEquals(expectedStatus, result.getStatus());
      assertEquals(expectedError, result.getError());
      assertEquals(expectedMessage, result.getMessage());
   }

   @Test
   public void testErrorCounter() {
      HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
      HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);

      CustomErrorController testObject = new CustomErrorController();
      testObject.setErrorAttributes(mock(ErrorAttributes.class));

      assertEquals(0L, testObject.getErrorCount());
      testObject.error(mockHttpServletRequest, mockHttpServletResponse);
      assertEquals(1L, testObject.getErrorCount());
      testObject.error(mockHttpServletRequest, mockHttpServletResponse);
      assertEquals(2L, testObject.getErrorCount());
      testObject.error(mockHttpServletRequest, mockHttpServletResponse);
      assertEquals(3L, testObject.getErrorCount());
   }
}
