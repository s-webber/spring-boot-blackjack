package com.example.blackjack.controller;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.example.blackjack.view.ErrorDetails;

/**
 * The {@code ErrorController} which is used to handle errors that occur during the processing of requests.
 * <p>
 * Based on {@code https://gist.github.com/jonikarppinen/662c38fb57a23de61c8b} which was influenced by
 * {@code http://stackoverflow.com/questions/25356781/spring-boot-remove-whitelabel-error-page}.
 */
@RestController
@ManagedResource(objectName = "com.example.blackjack.controller:name=CustomErrorController", description = "Custom error controller.")
public final class CustomErrorController implements ErrorController {
   private static final Logger LOG = LoggerFactory.getLogger(CustomErrorController.class);
   private static final String PATH = "/error";

   private final AtomicLong errorCounter = new AtomicLong();
   private ErrorAttributes errorAttributes;

   @RequestMapping(PATH)
   public ErrorDetails error(HttpServletRequest request, HttpServletResponse response) {
      errorCounter.incrementAndGet();
      Map<String, Object> errorAttributes = getErrorAttributes(request);
      int statusCode = response.getStatus();
      String error = (String) errorAttributes.get("error");
      String message = (String) errorAttributes.get("message");
      LOG.error("Got " + errorAttributes.get("exception") + " while processing request for " + errorAttributes.get("path") + " so will return " + statusCode
            + " " + error + " error with message: " + message);
      return new ErrorDetails(statusCode, error, message);
   }

   @Override
   public String getErrorPath() {
      return PATH;
   }

   private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
      RequestAttributes requestAttributes = new ServletRequestAttributes(request);
      return errorAttributes.getErrorAttributes(requestAttributes, false);
   }

   @ManagedAttribute(description = "Number of errors handled by CustomErrorController.")
   public long getErrorCount() {
      return errorCounter.get();
   }

   @Autowired
   public void setErrorAttributes(ErrorAttributes errorAttributes) {
      this.errorAttributes = errorAttributes;
   }
}
