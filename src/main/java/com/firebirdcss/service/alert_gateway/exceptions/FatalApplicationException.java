/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.exceptions;

/**
 * This exception indicates a fatal exception which should result in the 
 * termination of the application.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/23/2024
 *
 */
public class FatalApplicationException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * CONSTRUCTOR: 
     *
     */
    public FatalApplicationException() {}
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     */
    public FatalApplicationException(String message) {
        super(message);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param cause
     */
    public FatalApplicationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     * @param cause
     */
    public FatalApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     * @param cause
     * @param enableSuppression
     * @param writableStackTrace
     */
    public FatalApplicationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
