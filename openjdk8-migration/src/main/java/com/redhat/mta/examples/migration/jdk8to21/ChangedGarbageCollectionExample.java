package com.redhat.mta.examples.migration.jdk8to21;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory management service monitoring GC behavior.
 * Default GC changed from Parallel GC (JDK 8) to G1GC (JDK 9+)
 */
public class ChangedGarbageCollectionExample {
    
    private final ConcurrentHashMap<String, Object> memoryCache = new ConcurrentHashMap<>();
    
    public GCInfo getCurrentGCInfo() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        
        long totalCollections = 0;
        long totalTime = 0;
        StringBuilder gcNames = new StringBuilder();
        
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            totalCollections += gcBean.getCollectionCount();
            totalTime += gcBean.getCollectionTime();
            
            if (gcNames.length() > 0) {
                gcNames.append(", ");
            }
            gcNames.append(gcBean.getName());
        }
        
        return new GCInfo(gcNames.toString(), totalCollections, totalTime);
    }
    
    public void performMemoryIntensiveOperation() {
        // Record initial GC stats
        GCInfo initialStats = getCurrentGCInfo();
        
        // Allocate various object sizes to trigger different GC behaviors
        allocateSmallObjects();
        allocateMediumObjects();
        allocateLargeObjects();
        
        // Record final GC stats
        GCInfo finalStats = getCurrentGCInfo();
        
        // Calculate GC activity during operation
        long collectionsTriggered = finalStats.totalCollections - initialStats.totalCollections;
        long additionalGCTime = finalStats.totalTime - initialStats.totalTime;
    }
    
    private void allocateSmallObjects() {
        // Small objects (Eden space in generational GC)
        for (int i = 0; i < 10000; i++) {
            String smallObject = "Small_" + i;
            // Objects go out of scope quickly
        }
    }
    
    private void allocateMediumObjects() {
        // Medium objects (might survive to survivor space)
        Object[] mediumObjects = new Object[1000];
        for (int i = 0; i < mediumObjects.length; i++) {
            mediumObjects[i] = new byte[1024]; // 1KB objects
        }
        
        // Store some in cache to keep them alive
        for (int i = 0; i < 100; i++) {
            memoryCache.put("medium_" + i, mediumObjects[i]);
        }
    }
    
    private void allocateLargeObjects() {
        // Large objects (might go directly to old generation)
        byte[][] largeObjects = new byte[10][];
        for (int i = 0; i < largeObjects.length; i++) {
            largeObjects[i] = new byte[1024 * 1024]; // 1MB objects
        }
        
        // Store references to prevent immediate cleanup
        for (int i = 0; i < largeObjects.length; i++) {
            memoryCache.put("large_" + i, largeObjects[i]);
        }
    }
    
    public MemoryInfo getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();
        
        return new MemoryInfo(usedMemory, totalMemory, maxMemory, freeMemory);
    }
    
    public void clearCache() {
        memoryCache.clear();
        // Hint to run GC after clearing cache
        System.gc();
    }
    
    public int getCacheSize() {
        return memoryCache.size();
    }
    
    public void triggerGCActivity() {
        // Force some GC activity
        System.gc();
        System.runFinalization();
    }
    
    public boolean isG1GCActive() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        return gcBeans.stream().anyMatch(bean -> bean.getName().contains("G1"));
    }
    
    public boolean isParallelGCActive() {
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        return gcBeans.stream().anyMatch(bean -> bean.getName().contains("Parallel"));
    }
    
    public static class GCInfo {
        public final String gcNames;
        public final long totalCollections;
        public final long totalTime;
        
        public GCInfo(String gcNames, long totalCollections, long totalTime) {
            this.gcNames = gcNames;
            this.totalCollections = totalCollections;
            this.totalTime = totalTime;
        }
    }
    
    public static class MemoryInfo {
        public final long used;
        public final long total;
        public final long max;
        public final long free;
        
        public MemoryInfo(long used, long total, long max, long free) {
            this.used = used;
            this.total = total;
            this.max = max;
            this.free = free;
        }
    }
}