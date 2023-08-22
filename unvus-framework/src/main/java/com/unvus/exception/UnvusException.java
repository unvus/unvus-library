package com.unvus.exception;

public class UnvusException extends Exception {
    /**
     * Constructor for WonlabException.
     *
     * @param message exception message
     */
    public UnvusException(final String message) {
        super(message);
    }

	public UnvusException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UnvusException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public UnvusException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UnvusException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}


}
