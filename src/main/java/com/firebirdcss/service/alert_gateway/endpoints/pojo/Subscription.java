/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints.pojo;

import java.io.Serializable;

/**
 * @author Scott Griffis
 * <p>
 * Date: 03/22/2024
 *
 */
public class Subscription implements Serializable {
    private static final long serialVersionUID = 2L;
    
    private String email;
    private String sourceName;
    private Integer severityMask;
    
    public Subscription() {
        this.email = null;
        this.sourceName = null;
        this.severityMask = null;
    }
    
    public Subscription(String email, String sourceName, String sourceAddress, Integer severityMask) {
        this.email = email;
        this.sourceName = sourceName;
        this.severityMask = severityMask;
    }

    /**
     * @return Returns the email as String.
     */
    public String getEmail() {
        
        return email;
    }

    /**
     * @param email - The email to set as String.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the subMask as Integer.
     */
    public Integer getSeverityMask() {
        
        return severityMask;
    }

    /**
     * @param severityMask - The subMask to set as Integer.
     */
    public void setSeverityMask(Integer severityMask) {
        this.severityMask = severityMask;
    }

    /**
     * @return Returns the sourceName as String.
     */
    public String getSourceName() {
        
        return sourceName;
    }

    /**
     * @param sourceName - The sourceName to set as String.
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getId() {
        StringBuilder result = new StringBuilder();
        result
            .append(this.email == null ? "null" : this.email.toLowerCase())
            .append(':')
            .append(this.sourceName == null ? "null" : this.sourceName.toLowerCase())
        ;
        
        return result.toString();
    }
}
