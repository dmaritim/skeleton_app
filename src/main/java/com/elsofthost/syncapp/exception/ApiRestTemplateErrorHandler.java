package com.elsofthost.syncapp.exception;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class ApiRestTemplateErrorHandler extends DefaultResponseErrorHandler {

	  @Override
	  public void handleError(ClientHttpResponse response) throws IOException {
	    if (response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError()) {
	      try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getBody()))) {
	        String httpBodyResponse = reader.lines().collect(Collectors.joining(""));

	        // TODO deserialize (could be JSON, XML, whatever...) httpBodyResponse to a POJO that matches the error structure for that specific API, then extract the error message.
	        // Here the whole response will be treated as the error message, you probably don't want that.
	        String errorMessage = httpBodyResponse;

	        throw new RestTemplateException(response.getStatusCode(), errorMessage);
	      }
	    }
	  }
	}