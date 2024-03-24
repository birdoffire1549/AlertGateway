/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.endpoints;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.firebirdcss.service.alert_gateway.endpoints.enums.ResponseTypes;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.ActionResponse;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.Alert;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.Subscription;
import com.firebirdcss.service.alert_gateway.exceptions.SubscriptionManagementException;
import com.firebirdcss.service.alert_gateway.service.Notifier;

/**
 * REST ENDPOINT:<br>
 * This is the '/alert' REST endpoint.<br>
 * This endpoint receives alerts from the network and passes them on
 * to the {@link Notifier} Service to be processed.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
@RestController
public class Alerts {
    /**
     * This method handles the incoming posting of alerts via REST.
     * 
     * @param alert - The incoming alert as {@link Alert}
     * @return Returns the result of the post action as an {@link ResponseEntity} of
     * type {@link ActionResponse}
     */
    @RequestMapping(value = "/alert", method = RequestMethod.POST)
    public ResponseEntity<ActionResponse> postAlert(@RequestBody Alert alert) {
        if (alert != null) { // Alert is not null...
            Notifier.getInstanceSilently().send(alert);
            
            /* Successful Reply */
            return ResponseEntity
                .accepted()
                .body(
                    new ActionResponse(
                        ResponseTypes.ACTION_SUCCESSFUL, 
                        "Action Successful"
                    )
                )
            ;
        }
        
        /* Invalid Input Reply */
        return ResponseEntity
            .badRequest()
            .body(
                new ActionResponse(
                    ResponseTypes.ACTION_FAILED,
                    "Action Failed",
                    "A null Alert was received; Null Alerts are unacceptable!"
                )
            )
        ;
    }
    
    @RequestMapping(value = "/alert/subscriptions", method = RequestMethod.GET)
    public ResponseEntity<List<Subscription>> getSubscriptions(@RequestParam(required = false) String email, @RequestParam(required = false) String sourceName) {
        List<Subscription> subs = Notifier.getInstanceSilently().querySubscriptions(email, sourceName);
        
        return ResponseEntity.ok().body(subs);
    }
    
    @RequestMapping(value = "/alert/subscriptions", method = RequestMethod.POST)
    public ResponseEntity<ActionResponse> subscribe(@RequestBody Subscription subRequest) {
        
        try {
            Notifier.getInstanceSilently().subscribe(subRequest);
            
            return ResponseEntity
                .accepted()
                .body(
                    new ActionResponse(
                        ResponseTypes.ACTION_SUCCESSFUL,
                        "Action Successful"
                    )
                )
            ;
        } catch (SubscriptionManagementException e) {
            
            return ResponseEntity
                .internalServerError()
                .body(
                    new ActionResponse(
                        ResponseTypes.ACTION_FAILED,
                        "Action Failed",
                        e.toString()
                    )
                )
            ;
        }
    }
}
