/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.exceptions;

/**
 * This is a generalized Exception class to be used by the
 * {@link SubscriptionManager} for throwing errors upstream.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/23/2024
 *
 */
public class SubscriptionManagementException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * CONSTRUCTOR: 
     *
     */
    public SubscriptionManagementException() {}
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     */
    public SubscriptionManagementException(String message) {
        super(message);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param cause
     */
    public SubscriptionManagementException(Throwable cause) {
        super(cause);
    }
    
    /**
     * CONSTRUCTOR: 
     *
     * @param message
     * @param cause
     */
    public SubscriptionManagementException(String message, Throwable cause) {
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
    public SubscriptionManagementException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
