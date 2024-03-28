/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.firebirdcss.service.alert_gateway.data.Settings;
import com.firebirdcss.service.alert_gateway.endpoints.enums.MessageSeverity;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.Alert;
import com.firebirdcss.service.alert_gateway.endpoints.pojo.Subscription;
import com.firebirdcss.service.alert_gateway.exceptions.SubscriptionManagementException;
import com.firebirdcss.service.alert_gateway.util.SubscriptionManager;

/**
 * This is the Notifier Service. 
 * <p>
 * The objective of this service is to run on
 * it's own thread and continually check a queue of some sort for
 * messages which need to be sent out as alerts. Once such a message is
 * available it gets picked up, processed and sent out by this service.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
public class Notifier extends Service {
    private static final Logger log = LogManager.getLogger(Notifier.class);
    private static Notifier instance = null;
    
    private final Queue<Alert> alertQueue = new PriorityBlockingQueue<>(
        10 /*InitialCompacity*/, 
        (a1, a2) -> a1.getSeverity().intValue() - a2.getSeverity().intValue()
    );
    
    private final SubscriptionManager subManager;
    private Properties mailerProps = System.getProperties();
    
    /**
     * STATIC MEHTOD:<br>
     * Used to fetch an instance of this singleton service.
     * <p>
     * Please Note: The initial fetch of this service doesn't come 
     * automatically started. It is the job of the implementor to start
     * and stop the service as needed.
     * 
     * @return Returns an instance of this class as {@link Notifier}
     * 
     * @throws SubscriptionManagementException Notifier is having issues with Subscription persistence file.
     */
    public synchronized static Notifier getInstance() throws SubscriptionManagementException {
        if (instance == null) {
            instance = new Notifier();
        }
        
        return instance;
    }
    
    /**
     * STATIC METHOD:<br>
     * Same as the {@link #getInstance()} method except that this one will NOT
     * throw and error and may return a null if an exception does occur during 
     * instantiation of the object.
     * 
     * @return Returns an instance of this class as {@link Notifier} or null if an
     * exception occurs.
     */
    public synchronized static Notifier getInstanceSilently() {
        if (instance == null) {
            try {
                getInstance();
            } catch (Exception e) {
                // Suppress the exception...
            }
        }
        
        return instance;
    }
    
    /**
     * PRIVATE CONSTRUCTOR:<br> 
     * This class constructor allows for required settings to be passed in.
     * 
     * @param serviceName - The name of this instance of the service as {@link String}
     * @param cycleSleepMillis - The number of milliseconds to sleep between cycles as <code>long</code>
     * 
     * @throws SubscriptionManagementException Notifier issue loading Subscriptions from disk. 
     */
    private Notifier() throws SubscriptionManagementException {
        super("Notifier", 500L);
        
        /* Load the Mailer Properties */
        mailerProps.put("mail.smtp.host", Settings.smtpServerAddress);
        mailerProps.put("mail.smtp.socketFactory.port", Settings.smtpServerPort);
        mailerProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        mailerProps.put("mail.smtp.auth", "true");
        mailerProps.put("mail.smtp.port", Settings.smtpServerPort);
        
        subManager = new SubscriptionManager(Settings.NOTIFIER_SUBSCRIPTIONS_FILE);
    }
    
    /**
     * This method allows for an alert to be added to the alertQueue.
     * 
     * @param alert - The alert to add to the queue as {@link Alert}
     */
    public void send(Alert alert) {
        if (
            alert != null 
            && alert.getMessage() != null 
            && alert.getSeverity() != null
            && alert.getSourceName() != null
        ) { // Message is ok to send...
            log.info("An Alert was added to the alertQueue.");
            alertQueue.add(alert);
        } else {
            log.warn("An invalid alert was discarded."); // TODO: Add alert details...
        }
    }
    
    /**
     * Allows for subscribers and subscriptions to be added, modified
     * or removed based on the severityMask.
     * <p>
     * A severity is considered to be subscribed to by the given email address
     * when: 2^severity AND severityMask != 0
     * 
     * @param subscription - The incoming subscription record as {@link Subscription}
     * @throws SubscriptionManagementException Indicates an issue with persisting updates to disk.
     */
    public void subscribe(Subscription subscription) throws SubscriptionManagementException {
        try {
            subManager.update(subscription);
        } catch (SubscriptionManagementException e) {
            // TODO: Alert to Admin!? but also toss upstream incase invoker is interested in it.
            log.fatal("Unable to persist subscriptions to disk; See stacktrace for more:", e);
            
            throw e; // toss upstream so user can be notified
        }
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
    public List<Subscription> querySubscriptions(String email, String sourceName) {
        
        return subManager.query(email, sourceName);
    }
    
    /*
     * (non-Javadoc)
     * @see com.firebirdcss.service.alert_gateway.service.Service#preRun()
     */
    @Override
    protected void preRun() {
        // Not needed by this service.
    }
    
    /*
     * (non-Javadoc)
     * @see com.firebirdcss.service.alert_gateway.service.Service#postRun()
     */
    @Override
    protected void postRun() {
        // Not needed by this service.
    }
    
    /*
     * (non-Javadoc)
     * @see com.firebirdcss.service.alert_gateway.service.Service#runtime()
     */
    @Override
    protected void runtime() {
        while(alertQueue.peek() != null) { // There are alerts to process...
            Alert alert = alertQueue.poll();
            sendEmail(alert, subManager.getSubscribers(alert));
        }
    }
    
    /**
     * PRIVATE METHOD:<br>
     * Sends the contents of the incoming {@link Alert} to the 
     * email addresses contained in the provided emailList.
     * 
     * @param alert - The given alert as {@link Alert}
     * @param emailList - The emailList as a {@link List} of {@link String} emails
     */
    private void sendEmail(Alert alert, List<String> emailList) {
        if (emailList != null && !emailList.isEmpty()) {
            Session session = Session.getDefaultInstance(
                mailerProps,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        
                        return new PasswordAuthentication(Settings.smtpUser, Settings.smtpPassword);
                    }
                }
            );
            
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Settings.notifierFromEmailAddress, Settings.notifierFromEmailName));
                
                /* Add subscribed recipients to message */
                emailList.forEach(
                    (email) -> {
                        try {
                            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
                        } catch (AddressException e) {
                            log.warn(String.format("Invalid alert recipient of '%s', was skipped!", email));
                        } catch (MessagingException e) {
                            log.error(String.format("Encountered an error of: '%s', while attempting add recipients to the outgoing message! See stacktrace for details:", e.getMessage()), e);
                        }
                    }
                );
                
                message.setSubject(String.format("Alert from '%s (%s)'", alert.getSourceAddress(), alert.getSourceName()));
                message.setText(String.format("%s: %s\n%s", alert.getDate(), MessageSeverity.getName(alert.getSeverity()), alert.getMessage()));
                
                /* Send the message to SMTP Server */
                Transport.send(message);
            } catch (MessagingException e) {
                log.error("An exception occurred while attempting to send out alerts: ", e);
            } catch (UnsupportedEncodingException e) { // From address or name is invalid...
                log.error("The provided 'From' address is invalid! Please correct the 'From' address and bounce the applicaiton! I show the 'From' address as: '" + Settings.notifierFromEmailName + " (" + Settings.notifierFromEmailAddress + "')");
            }
        } else {
            log.warn("Attempted to send an Alert but no matching subscriptions were found!");
        }
    }
}
