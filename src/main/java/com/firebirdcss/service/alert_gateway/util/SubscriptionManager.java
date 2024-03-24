/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.firebirdcss.service.alert_gateway.data.Settings;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.Alert;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.Subscription;
import com.firebirdcss.service.alert_gateway.exceptions.SubscriptionManagementException;

/**
 * This class is used to manage alert subscriptions.
 * <p>
 * The goal of this class is to take care of the maintenance related tasks 
 * surrounding the alert subscriptions so that the external code can easily
 * interact with the underline subscriptions easily without worrying about 
 * the details.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/22/2024
 *
 */
public class SubscriptionManager {
    private final HashMap<String, Subscription> subscriptions = new HashMap<>();
    private final String saveFile;
    
    /**
     * CONSTRUCTOR:<br>
     * Used to provide required information to the class upon
     * its instantiation. 
     *
     * @param saveFile - The path to the file where subscription information will
     * be saved and loaded from as {@link String}
     * 
     * @throws SubscriptionManagementException Indicates a problem with loading Subscriptions from disk.
     */
    public SubscriptionManager(String saveFile) throws SubscriptionManagementException {
        this.saveFile = saveFile;
        loadSubscriptions();
    }
    
    /**
     * This method is used to invoke an update to a managed subscription.
     * <p>
     * This method is capable of adding, updating or removing an existing 
     * subscription. If the incoming subscription is unknown then it is added.
     * If the incoming subscription is known then its settings replace those
     * of the existing one. If the incoming one isn't subscribed to anything then
     * the existing managed one is deleted. In each scenario the incoming subscription
     * is the record of truth.
     * 
     * @param subscription - The incoming subscription modifications as {@link Subscription}
     * 
     * @throws SubscriptionManagementException Indicates a problem with persisting Subscriptions to disk. 
     */
    public synchronized void update(Subscription subscription) throws SubscriptionManagementException {
        synchronized(subscriptions) {
            if (subscription.getSeverityMask().intValue() == 0) { // Perform removal...
                this.subscriptions.remove(subscription.getId());
            } else { // Add/Update subscription...
                this.subscriptions.put(subscription.getId(), subscription);
            }
            
            persistSubscriptions();
        }
    }
    
    /**
     * Provides a {@link List} of email addresses which are subscribed to 
     * receive alerts for the given {@link Alert}.
     * 
     * @param alert - An alert to look up subscriber emails for as {@link Alert}
     * @return Returns a {@link List} containing the emails as {@link String}
     */
    public List<String/*Email*/> getSubscribers(Alert alert) {
        return subscriptions.entrySet().stream().filter(
            (e) -> {
                return 
                    e.getKey().endsWith(":" + alert.getSourceName().toLowerCase()) 
                    && (
                        ((int) Math.pow(2D, alert.getSeverity().doubleValue()) 
                            & e.getValue().getSeverityMask().intValue()) != 0
                        )
                ;
            }
        ).map((e) -> e.getValue().getEmail()).collect(Collectors.toList());
    }
    
    /**
     * Used to query for a {@link List} of {@link Subscription}s which optionally
     * match the given query parameters.
     * 
     * @param email - Pulls results matching the supplied email address as {@link String}, this parameter is
     * optional and may be null if not desired.
     * @param sourceName - Pulls results matching the supplied sourceName as {@link String}, this parameter is
     * optional and may be null if not desired.
     * 
     * @return Returns the results as a {@link List} of {@link Subscription}s
     */
    public List<Subscription> query(String email, String sourceName) {
        return subscriptions.entrySet().stream()
                .filter(
                    (e) -> {
                        return (
                            (email != null ? e.getValue().getEmail().equalsIgnoreCase(email) : true)
                            && (sourceName != null ? e.getValue().getSourceName().equalsIgnoreCase(sourceName) : true)
                        );
                    }
                )
                .map(e -> e.getValue())
                .collect(Collectors.toList())
        ;
    }
    
    /**
     * PRIVATE METHOD:
     * Used to load the subscriptions from a file.
     * 
     * @throws SubscriptionManagementException Indicates a problem with recalling persisted Subscriptions from disk. 
     */
    private void loadSubscriptions() throws SubscriptionManagementException {
        File file = new File(this.saveFile);
        if (file.exists()) { // File doesn't yet exist; create it...
            if (file.canRead()) { // File is readable; attempt to load...
                try (FileInputStream fIs = new FileInputStream(file); ObjectInputStream in = new ObjectInputStream(fIs);) {
                    Object oIn = in.readObject();
                    if (oIn != null && oIn instanceof HashMap) {
                        subscriptions.clear();
                        ((HashMap<?,?>) oIn).values().forEach((o) -> {
                            if (o instanceof Subscription) {
                                subscriptions.put(((Subscription) o).getId(), ((Subscription) o));
                            }
                        });
                    }
                } catch (IOException e) {
                    throw new SubscriptionManagementException("Application might not have required access to manage Subscriptions file!", e);
                } catch (ClassNotFoundException e) { // Don't know what we are reading in...
                    throw new SubscriptionManagementException("Could not properly deserialize Subscription data; File may be corrupt!", e);
                }
            } else { // File has a problem...
                throw new SubscriptionManagementException("Application might not have required access to manage Subscriptions file!");
            }
        }
    }
    
    /**
     * PRIVATE METHOD:<br>
     * Used to persist subscriptions to disk.
     * 
     * @throws SubscriptionManagementException Indicates a problem when persisting the Subscriptions to disk.
     */
    private void persistSubscriptions() throws SubscriptionManagementException {
        File file = new File(Settings.NOTIFIER_SUBSCRIPTIONS_FILE);
        if (file.exists()) { // Existing file should be removed...
            file.delete();
        }
        try {
            file.createNewFile();
            try (FileOutputStream fOut = new FileOutputStream(file); ObjectOutputStream out = new ObjectOutputStream(fOut);) {
                out.writeObject(subscriptions);
            }
        } catch (IOException e) {
            throw new SubscriptionManagementException("Application might not have required access to manage Subscriptions file!", e);
        }
    }
}
