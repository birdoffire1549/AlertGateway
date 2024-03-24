/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
public enum MessageSeverity {
    TEST(1),
    INFORMATIONAL(2),
    WARNING(3),
    CRITICAL(4),
    EMERGENCY(5);
    
    private Integer value;
    
    MessageSeverity(Integer value) {
        this.value = value;
    }
    
    public Integer getValue() {
        return value;
    }
    
    public static String getName(Integer value) {
        Optional<MessageSeverity> v = Arrays.asList(MessageSeverity.values()).stream().filter((s) -> s.value.intValue() == value.intValue()).findFirst();
        if (v.isPresent()) {
            
            return v.get().name();
        } 
        
        return null;
    }
}
