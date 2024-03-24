/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.exceptions;

/**
 * @author Scott Griffis
 * <p>
 * Date: 03/23/2024
 *
 */
public class ApplicationInitializationException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * CONSTRUCTOR: 
     *
     */
    public ApplicationInitializationException() {}
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     */
    public ApplicationInitializationException(String message) {
        super(message);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param cause
     */
    public ApplicationInitializationException(Throwable cause) {
        super(cause);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     * @param cause
     */
    public ApplicationInitializationException(String message, Throwable cause) {
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
    public ApplicationInitializationException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
}
