/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RestController;

import com.firebirdcss.service.alert_gateway.data.Settings;
import com.firebirdcss.service.alert_gateway.exceptions.ApplicationInitializationException;
import com.firebirdcss.service.alert_gateway.exceptions.FatalApplicationException;
import com.firebirdcss.service.alert_gateway.exceptions.SubscriptionManagementException;
import com.firebirdcss.service.alert_gateway.service.Notifier;
import com.firebirdcss.service.alert_gateway.service.Service;

/**
 * This is the main Class of the application and contains the main method
 * that is used to run the application.
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
@RestController
@SpringBootApplication // same as @Configuration @EnableAutoConfiguration @ComponentScan
public class ApplicationMain {
    private static final Logger log = LogManager.getLogger(ApplicationMain.class);
    private static final List<Service> services = new ArrayList<>();
    
    private static ConfigurableApplicationContext springCtx = null;
    
    private static volatile boolean running = true;
    /**
     * MAIN METHOD:
     * This is the main method of the application and where all of 
     * the execution begins.
     * 
     * @param args - NOT USED
     */
    public static void main(String[] args) {
        Thread.currentThread().setName("AlertGateway");
        try {
            log.info("Alert Gateway (version:" + Settings.APPLICATION_VERSION + ")");
            log.info("Alert Gateway is being started...");
            
            init();
            
            log.info("Alert Gateway has started.");
            
            sleepMainThread();
        } catch (FatalApplicationException e) {
            log.fatal("The application encountered a fatal exception:", e);
        }
        
        shutdown();
    }
    
    /**
     * PRIVATE METHOD:<br>
     * This method is used to cause the Main Application thread to 
     * remain in a sleeping state after it has completed its tasks
     * for the duration of the service's runtime. This method is only
     * to exit when the application is trying to shutdown. 
     */
    private static void sleepMainThread() {
        while(running) { // Application running so sleep is ok...
            try {
                TimeUnit.MINUTES.sleep(1L);
            } catch (InterruptedException e) {
                if (running) { // Running so interrupt shouldn't have happened...
                    Thread.interrupted(); // clear it
                }
            }
        }
    }
    
    /**
     * PRIVATE METHOD:<br>
     * This method is used upon startup of the service to perform all
     * of the initialization tasks needed to get the service up and running.
     * 
     * @throws FatalApplicationException Indicates something happened which should result in the termination of the application. 
     */
    private static void init() throws FatalApplicationException {
        /* Register the ShutdownHook */
        Runtime.getRuntime().addShutdownHook(new ShutdownHook(Thread.currentThread()));
        
        /* Load the Applicaiton's Settings */
        try {
            Settings.loadSettings();
        } catch (ApplicationInitializationException e) {
            throw new FatalApplicationException(e);
        }
        
        /* Starting the Notifier Service */
        Notifier notifierService;
        try {
            notifierService = Notifier.getInstance();
        } catch (SubscriptionManagementException e) {
            throw new FatalApplicationException(e);
        }
        
        services.add(notifierService);
        notifierService.start();
        
        /* Start the SpringBoot service */
        springCtx = SpringApplication.run(ApplicationMain.class);
    }
    
    /**
     * PRIVATE METHOD:<br>
     * This method is used by the main to shutdown all of its
     * children processes and services. 
     */
    private static void shutdown() {
        /* Shutdown the SpringBoot service */
        if (springCtx != null) {
            SpringApplication.exit(
                springCtx, 
                new ExitCodeGenerator() {
                    @Override
                    public int getExitCode() {
                        return 0;
                    }
                    
                }
            );
        }
        
        services.forEach((s) -> {
            log.info(String.format("Stopping the %s Service...", s.getName()));
            s.stopAndWait();
            log.info(String.format("%s Service has stopped.", s.getName()));
        });
        
        log.info("Shutdown complete.");
    }
    
    
    /**
     * PRIVATE CLASS:
     * This private class is a shutdown-hook for the application's
     * main thread.
     * 
     * @author Scott Griffis
     * <p>
     * Date: 03/20/2024
     *
     */
    private static class ShutdownHook extends Thread {
        private Thread main;
        
        /**
         * CONSTRUCTOR:<br>
         * This constructor allows for a reference to the mainThread
         * to be passed in. 
         *
         * @param mainThread - A reference to the mainThread as {@link Thread}
         */
        public ShutdownHook(Thread mainThread) {
            this.main = mainThread;
        }
        
        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            log.info("Shutdown in progress...");
            running = false;
            main.interrupt();
        }
    }
}
