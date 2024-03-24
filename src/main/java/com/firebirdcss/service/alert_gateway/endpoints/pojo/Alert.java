/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints.pojo;

import com.firebirdcss.service.alert_gateway.endpoints.enums.MessageSeverity;

/**
 * This is a general alert POJO.
 * The idea is that this data format can be universally used by any device or 
 * application to send alerts into the system.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
public class Alert {
    private String sourceAddress = null;
    private String sourceName = null;
    private String date = null;
    private String dateMask = null;
    private Integer severity = MessageSeverity.INFORMATIONAL.getValue(); // defaults to Informational 
    private String message = null;
    
    /**
     * CONSTRUCTOR: This constructor is only to ensure that default instantiation of this 
     * object can take place.
     *
     */
    public Alert() {}
    
    /**
     * CONSTRUCTOR: This constructor is used to initialize the data of this POJO. 
     *
     * @param sourceAddress - The address of the sending device as {@link String}
     * @param sourceName - The friendly name of the sending device as {@link String}
     * @param date - The date/time of the alert as {@link String}
     * @param dateMask - The date/time mask to specify the format 
     *                      of the data in the date field as {@link String}
     * @param message - The alert message from the device as {@link String}
     */
    public Alert(String sourceAddress, String sourceName, String date, String dateMask, String message) {
        this.sourceAddress = sourceAddress;
        this.sourceName = sourceName;
        this.date = date;
        this.dateMask = dateMask;
        this.message = message;
    }

    /**
     * @return Returns the sourceAddress, as {@link String}
     */
    public String getSourceAddress() {
    
        return sourceAddress;
    }

    /**
     * @param sourceAddress - The sourceAddress to set, as {@link String}
     */
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    /**
     * @return Returns the sourceName, as {@link String}
     */
    public String getSourceName() {
    
        return sourceName;
    }

    /**
     * @param sourceName - The sourceName to set, as {@link String}
     */
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * @return Returns the date, as {@link String}
     */
    public String getDate() {
    
        return date;
    }

    /**
     * @param date - The date to set, as {@link String}
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return Returns the dateMask, as {@link String}
     */
    public String getDateMask() {
    
        return dateMask;
    }

    /**
     * @param dateMask - The dateMask to set, as {@link String}
     */
    public void setDateMask(String dateMask) {
        this.dateMask = dateMask;
    }
    
    /**
     * @return Returns the severity number as {@link Integer}
     */
    public Integer getSeverity() {
        
        return severity;
    }
    
    /**
     * @param severity - The message severity as {@link Integer}
     */
    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    /**
     * @return Returns the message, as {@link String}
     */
    public String getMessage() {
    
        return message;
    }

    /**
     * @param message - The message to set, as {@link String}
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
