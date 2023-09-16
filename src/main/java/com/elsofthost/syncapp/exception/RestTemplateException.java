package com.elsofthost.syncapp.exception;

import org.springframework.http.HttpStatus;

public class RestTemplateException extends RuntimeException {

	  private HttpStatus statusCode;
	  private String error;

	  public RestTemplateException(HttpStatus statusCode, String error) {
	    super(error);
	    this.statusCode = statusCode;
	    this.error = error;
	  }


	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public String getError() {
		return error;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public void setError(String error) {
		this.error = error;
	}
	  

	}