/**
 * Copyright 2024 by FirebirdCSS
 */
package com.firebirdcss.service.alert_gateway.service;

import java.util.concurrent.TimeUnit;

/**
 * ABSTRACT CLASS:<br>
 * This class makes implementing a standardized service 
 * simple. 
 * 
 * @author Scott Griffis
 * <p>
 * Date: 03/20/2024
 *
 */
public abstract class Service {
    private final String serviceName;
    private final long cycleSleepMillis;
    
    private final Object stateLock = new Object();
    private RuntimeThread runtimeThread = null;
    
    /**
     * CONSTRUCTOR: Takes its settings via passed in variables. 
     *
     * @param serviceName - A user friendly unique name for the implementing service as {@link String}
     * @param cycleSleepMillis - The number of milliseconds to sleep at the end of a cycle before begginning
     * the next cycle as <code>long</code>
     */
    protected Service(String serviceName, long cycleSleepMillis) {
        this.cycleSleepMillis = cycleSleepMillis;
        this.serviceName = serviceName;
    }
    
    /**
     * This method will contain code to be executed 
     * once start is called and just before the runtime
     * code begins it's cycle.
     */
    protected abstract void preRun();
    
    /**
     * This method will contain code to be executed once
     * the runtime completes it's execution and reaches a 
     * stopped state.
     */
    protected abstract void postRun();
    
    /**
     * This method will contain the runtime code
     * for the implementing service.
     */
    protected abstract void runtime();
    
    /**
     * This method is used to start a service that is
     * in the stopped state when this method is called.
     * This method does nothing if the service is already 
     * running.
     */
    public void start() {
        synchronized (stateLock) {
            if (runtimeThread == null || !runtimeThread.isAlive()) { // Thread is stopped...
                /* Define and prepare the Runtime Cycle Thread */
                this.runtimeThread = new RuntimeThread();
                this.runtimeThread.setName(serviceName);
                
                /* Perform preRun tasks */
                preRun();
                
                /* Start the runtime Thread */
                this.runtimeThread.start();
            }
        }
    }
    
    /**
     * This method is used to stop a service that is 
     * in a running state when this method is called.
     * If the service is already stopped or stopping
     * then this method does nothing.
     */
    public void stop() {
        synchronized (stateLock) {
            if (this.runtimeThread != null && this.runtimeThread.isAlive()) {
                this.runtimeThread.terminate();
            }
        }
    }
    
    /**
     * This method works just like the {@link stop()} method except when called
     * the calling thread will wait for the runtime thread to finish before its 
     * execution continues on.
     */
    public void stopAndWait() {
        synchronized (stateLock) {
            if (this.runtimeThread != null && this.runtimeThread.isAlive()) {
                this.runtimeThread.terminate();
                while (isAlive()) { // Wait for runtime thread to stop...
                    try {
                        TimeUnit.MILLISECONDS.sleep(500L);
                    } catch (InterruptedException e) {}
                }
            }
        }
    }
    
    /**
     * This method allows for someone to externally check on the running status of
     * the runtime Thread.
     * 
     * @return Returns <code>boolean</code> true if running otherwise returns false.
     */
    public boolean isAlive() {
        
        return (this.runtimeThread.isAlive());
    }
    
    
    /**
     * Allows for someone to externally fetch the name of this service.
     * 
     * @return Returns the name of the service as {@link String}
     */
    public String getName() {
        
        return this.serviceName;
    }
    
    /**
     * PRIVATE CLASS: This private class is a wrapper for the 
     * runtime thread. By using this class the thread can at worst
     * be stopped at the end of it's run cycle or during sleep. This is
     * in-case the implementor didn't persist and honor the thread's 
     * interrupts.
     * 
     * @author Scott Griffis
     * <p>
     * Date: 03/20/2024
     *
     */
    private class RuntimeThread extends Thread {
        private volatile boolean running = false;
        
        /*
         * (non-Javadoc)
         * @see java.lang.Thread#start()
         */
        @Override
        public void start() {
            running = true;
            super.start();
        }
        
        /*
         * (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        @Override
        public void run() {
            while (running) { // Is running...
                runtime();
                
                if (running) { // Is running...
                    try {
                        TimeUnit.MILLISECONDS.sleep(cycleSleepMillis);
                    } catch (InterruptedException e) {
                        // Just carry on...
                    }
                }
            }
        }
        
        /**
         * This method is used to stop the RuntimeThread.
         * Once an instance of RuntimeThread is stopped it cannot
         * be started again.
         */
        public void terminate() {
            this.running = false;
            this.interrupt();
        }
    }
}
