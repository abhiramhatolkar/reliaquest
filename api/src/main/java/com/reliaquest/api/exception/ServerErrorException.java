package com.reliaquest.api.exception;

public class ServerErrorException extends RuntimeException {
  public ServerErrorException(String message) {
    super(message);
}

public ServerErrorException(String message, Throwable cause) {
    super(message, cause);
}
}
