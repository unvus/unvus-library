package com.unvus.exception;

public class UnvusRuntimeException extends RuntimeException {
    /**
     * Constructor for WonlabRuntimeException.
     *
     * @param message exception message
     */
    public UnvusRuntimeException(final String message) {
        super(message);
    }

    public UnvusRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
