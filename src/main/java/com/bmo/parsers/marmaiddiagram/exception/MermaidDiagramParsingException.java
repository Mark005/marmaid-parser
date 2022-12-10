package com.bmo.parsers.marmaiddiagram.exception;

public class MermaidDiagramParsingException extends RuntimeException {

  public MermaidDiagramParsingException() {
  }

  public MermaidDiagramParsingException(String message) {
    super(message);
  }

  public MermaidDiagramParsingException(String message, Object... args) {
    super(String.format(message, args));
  }

  public MermaidDiagramParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public MermaidDiagramParsingException(Throwable cause) {
    super(cause);
  }

  public MermaidDiagramParsingException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
