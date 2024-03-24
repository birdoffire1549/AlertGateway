package com.firebirdcss.service.alert_gateway.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.firebirdcss.service.alert_gateway.exceptions.ApplicationInitializationException;

public class Settings {
    public static final String APPLICATION_VERSION;
    static {
        Properties prop = new Properties();
        String temp = "unknown";
        try (InputStream is = Settings.class.getClassLoader().getResourceAsStream("version.properties");) {
            prop.load(is);
            temp = prop.getProperty("appVersion", temp);
        } catch (IOException e) {
            // Do nothing...
        }
        APPLICATION_VERSION = temp;
    }
    
    public static final String HOME_DIR = "/opt/alert_gateway/";
    public static final String CONFIG_DIR = HOME_DIR + "config/";
    public static final String DATA_DIR = HOME_DIR + "data/";
    
    public static final String NOTIFIER_SUBSCRIPTIONS_FILE = DATA_DIR + "notifier-subscriptions.ser";
    public static final String NOTIFIER_APPLICATION_PROPERTIES_FILE = CONFIG_DIR + "application.properties";
    
    public static String notifierFromEmailAddress = "no-reply@domain.com";
    public static String notifierFromEmailName = "Alert Gateway";
    public static String smtpServerAddress = "127.0.0.1";
    public static String smtpServerPort = "465";
    public static String smtpUser = "User";
    public static String smtpPassword = "password";
    
    /**
     * STATIC METHOD:<br>
     * This method allows for the application to determine when to load the settings
     * and to watch for and report on any problems which may occur.
     * 
     * @throws ApplicationInitializationException Indicates a problem loading the settings.
     */
    public static void loadSettings() throws ApplicationInitializationException {
        File propFile = new File(Settings.NOTIFIER_APPLICATION_PROPERTIES_FILE);
        if (propFile.exists()) { // File exists...
            if (propFile.canRead()) { // ...and it's readable...
                Properties props = new Properties();
                try (FileInputStream fIn = new FileInputStream(propFile);) {
                    props.load(fIn);
                    /* Optional Properties */
                    notifierFromEmailAddress = props.getProperty(PropertyFields.NOTIFIER_FROM_ADDRESS, notifierFromEmailAddress);
                    notifierFromEmailName = props.getProperty(PropertyFields.NOTIFIER_FROM_NAME, notifierFromEmailName);
                    smtpServerAddress = props.getProperty(PropertyFields.NOTIFIER_SMTP_ADDRESS, smtpServerAddress);
                    smtpServerPort = props.getProperty(PropertyFields.NOTIFIER_SMTP_PORT, smtpServerPort);
                    /* Required Properties */
                    smtpUser = props.getProperty(PropertyFields.NOTIFIER_SMTP_USER, null);
                    smtpPassword = props.getProperty(PropertyFields.NOTIFIER_SMTP_PASSWORD, null);
                    
                    /* Verify required properties are not missing */
                    if (smtpUser == null || smtpPassword == null) { // Something was missing...
                        throw new ApplicationInitializationException("Required properties were missing, without them the application cannot be initialized properly!");
                    }
                } catch (IOException e) {
                    throw new ApplicationInitializationException("There was a problem reading the Application's properties from the file; See stacktrace for details:", e);
                }
            } else { // Cannot read the file...
                throw new ApplicationInitializationException(String.format("Application properties file exists but cannot read it! File path: '%s'", Settings.NOTIFIER_APPLICATION_PROPERTIES_FILE));
            }
        } else {
            throw new ApplicationInitializationException(String.format("Application cannot find the properties file! Expected file path is: '%s'", Settings.NOTIFIER_APPLICATION_PROPERTIES_FILE));
        }
    }
}
