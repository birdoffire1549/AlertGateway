/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.firebirdcss.service.alert_gateway.data.Settings;

/**
 * REST CONTROLLER:<br> 
 * This is where interaction happens with the default 
 * rest path.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
@RestController
@RequestMapping(value = "/")
public class Default {
    
    /**
     * This is the root or default path's GET METHOD response.
     * 
     * @return Returns the response as {@link String}
     */
    @RequestMapping(method = RequestMethod.GET)
    public String homePage() {
        String messageHtml = ""
            + "<html>"
            + "    <body>"
            + "        <h1>Alert Gateway</h1>"
            + "        <h2>(version: " + Settings.APPLICATION_VERSION + ")</h2>"
            + "    </body>"
            + "</html>"
        ;
        return messageHtml;
    }
}
