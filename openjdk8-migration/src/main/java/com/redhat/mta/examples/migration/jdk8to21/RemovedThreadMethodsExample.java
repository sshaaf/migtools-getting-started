package com.redhat.mta.examples.migration.jdk8to21;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Background task manager using deprecated Thread methods.
 * Thread.stop() and Thread.destroy() removed in JDK 11+
 */
public class RemovedThreadMethodsExample {
    
    private final ConcurrentHashMap<String, Object> dataCache = new ConcurrentHashMap<>();
    private final AtomicInteger processedCount = new AtomicInteger(0);
    
    private Thread backgroundWorker;
    private Thread dataProcessor;
    private Thread cleanupWorker;
    
    public void startBackgroundTasks() {
        backgroundWorker = new Thread(new DataProcessor());
        backgroundWorker.setName("background-worker");
        backgroundWorker.start();
        
        dataProcessor = new Thread(new AnalysisEngine());
        dataProcessor.setName("data-processor");
        dataProcessor.start();
        
        cleanupWorker = new Thread(new CleanupTask());
        cleanupWorker.setName("cleanup-worker");
        cleanupWorker.start();
    }
    
    public void emergencyShutdown() {
        // Force stop background worker - deprecated method
        if (backgroundWorker != null && backgroundWorker.isAlive()) {
            backgroundWorker.stop();
        }
        
        // Force stop with exception - deprecated method
        if (dataProcessor != null && dataProcessor.isAlive()) {
            dataProcessor.stop(new RuntimeException("Emergency shutdown"));
        }
        
        // Destroy cleanup thread - deprecated method
        if (cleanupWorker != null && cleanupWorker.isAlive()) {
            cleanupWorker.destroy();
        }
    }
    
    private class DataProcessor implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String data = generateData();
                    dataCache.put("data_" + processedCount.incrementAndGet(), data);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        
        private String generateData() {
            return "processed_data_" + System.currentTimeMillis();
        }
    }
    
    private class AnalysisEngine implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    analyzeDataBatch();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
        
        private void analyzeDataBatch() {
            dataCache.values().forEach(data -> {
                // Perform analysis on data
                data.toString().hashCode();
            });
        }
    }
    
    private class CleanupTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    if (dataCache.size() > 1000) {
                        dataCache.clear();
                        processedCount.set(0);
                    }
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
