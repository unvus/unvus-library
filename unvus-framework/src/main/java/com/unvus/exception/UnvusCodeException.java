package com.unvus.exception;

public class UnvusCodeException extends UnvusException {
    private String code;

    /**
     * Constructor for WonlabCodeException.
     *
     * @param message exception message
     */
    public UnvusCodeException(final String code, final String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
