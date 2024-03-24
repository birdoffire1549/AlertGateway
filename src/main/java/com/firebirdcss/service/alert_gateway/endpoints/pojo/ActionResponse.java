/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints.pojo;

import com.firebirdcss.service.alert_gateway.endpoints.enums.ResponseTypes;

/**
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
public class ActionResponse {
    private Integer type = 1;
    private String message = "";
    private String addInfo = "";
    
    public ActionResponse() {}
    
    public ActionResponse(Integer type, String message) {
        this.type = type;
        this.message = message;
    }
    
    public ActionResponse(ResponseTypes type, String message) {
        this.type = type.getValue();
        this.message = message;
    }
    
    public ActionResponse(Integer type, String message, String addInfo) {
        this.type = type;
        this.message = message;
        this.addInfo = addInfo;
    }
    
    public ActionResponse(ResponseTypes type, String message, String addInfo) {
        this.type = type.getValue();
        this.message = message;
        this.addInfo = addInfo;
    }
    
    /**
     * @return Returns the type as {@link Integer}
     */
    public Integer getType() {
        
        return type;
    }

    /**
     * @param type - The type to set as {@link Integer}
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return Returns the message as {@link String}
     */
    public String getMessage() {
        
        return message;
    }

    /**
     * @param message - The message to set as {@link String}
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return Returns the addInfo as {@link String}
     */
    public String getAddInfo() {
        
        return addInfo;
    }

    /**
     * @param addInfo - The addInfo to set as {@link String}
     */
    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }
}
