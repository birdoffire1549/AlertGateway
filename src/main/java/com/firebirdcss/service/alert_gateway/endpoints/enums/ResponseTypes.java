/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints.enums;

/**
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
public enum ResponseTypes {
    ACTION_SUCCESSFUL(1),
    ACTION_FAILED(2);
    
    private Integer value;
    
    ResponseTypes(Integer value) {
        this.value = value;
    }
    
    public Integer getValue() {
        
        return this.value;
    }
}
