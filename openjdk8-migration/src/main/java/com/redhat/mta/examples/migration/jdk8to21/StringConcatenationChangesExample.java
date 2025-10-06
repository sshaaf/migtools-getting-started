package com.redhat.mta.examples.migration.jdk8to21;

/**
 * String processing service demonstrating concatenation patterns.
 * JDK 9+ uses invokedynamic for string concatenation optimization
 */
public class StringConcatenationChangesExample {
    
    public String processUserData(String name, int age, boolean active) {
        return "Name: " + name + ", Age: " + age + ", Active: " + active;
    }
    
    public String generateUserReport(String name, int age, boolean active) {
        // Complex concatenation with different types - JDK 9+ optimizes this
        return "User " + name + " (age " + age + ") has " + 
               (active ? "active" : "inactive") + " status";
    }
    
    public String generateItemList(int count) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            // Each iteration creates concatenation - behavior differs JDK 8 vs 9+
            String iteration = "Item " + i + " value " + (i * 10) + "; ";
            result.append(iteration);
        }
        return result.toString();
    }
    
    public String formatMessage(String template, Object... args) {
        // String concatenation in loops - performance characteristics change
        String result = template;
        for (int i = 0; i < args.length; i++) {
            result = result + " " + args[i].toString();
        }
        return result;
    }
    
    public String buildComplexString(String prefix, int number, double value, boolean flag) {
        // Multiple type concatenation - invokedynamic optimization in JDK 9+
        return prefix + "_" + number + "_" + value + "_" + (flag ? "Y" : "N");
    }
    
    public String processLargeDataset(String[] data) {
        String combined = "";
        for (String item : data) {
            // Inefficient pattern that benefits from JDK 9+ optimizations
            combined = combined + item + ",";
        }
        return combined;
    }
}
