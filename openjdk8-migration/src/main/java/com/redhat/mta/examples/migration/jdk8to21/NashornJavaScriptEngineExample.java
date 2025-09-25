package com.redhat.mta.examples.migration.jdk8to21;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.Invocable;
import javax.script.Bindings;
import java.util.Map;
import java.util.HashMap;

/**
 * Business rules engine using Nashorn JavaScript.
 * Nashorn deprecated in JDK 11, removed in JDK 15
 */
public class NashornJavaScriptEngineExample {
    
    private ScriptEngine engine;
    private ConfigurationProcessor configProcessor;
    private final Map<String, Object> ruleCache = new HashMap<>();
    
    public void initialize() {
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("nashorn");
        
        configProcessor = new ConfigurationProcessor();
        engine.put("config", configProcessor);
        engine.put("utils", new UtilityFunctions());
        
        loadBusinessRules();
    }
    
    public Object validateOrder(Map<String, Object> order) throws ScriptException {
        Invocable invocable = (Invocable) engine;
        try {
            return invocable.invokeFunction("validateOrder", order);
        } catch (NoSuchMethodException e) {
            throw new ScriptException("Validation function not found");
        }
    }
    
    public Object calculateDiscount(Map<String, Object> customer, Map<String, Object> order) throws ScriptException {
        Invocable invocable = (Invocable) engine;
        try {
            return invocable.invokeFunction("calculateDiscount", customer, order);
        } catch (NoSuchMethodException e) {
            throw new ScriptException("Discount function not found");
        }
    }
    
    private void loadBusinessRules() throws ScriptException {
        String businessRules = 
            "function validateOrder(order) {" +
            "    if (order.amount > 10000) {" +
            "        return { valid: false, reason: 'Amount exceeds limit' };" +
            "    }" +
            "    if (order.items.length === 0) {" +
            "        return { valid: false, reason: 'No items in order' };" +
            "    }" +
            "    return { valid: true };" +
            "}" +
            "function calculateDiscount(customer, order) {" +
            "    var discount = 0;" +
            "    if (customer.type === 'PREMIUM') {" +
            "        discount = order.amount * 0.1;" +
            "    } else if (customer.type === 'GOLD') {" +
            "        discount = order.amount * 0.05;" +
            "    }" +
            "    return Math.min(discount, 1000);" +
            "}";
        
        engine.eval(businessRules);
        ruleCache.put("businessRules", businessRules);
    }
    
    public String generateCustomReport(Map<String, Object> data) throws ScriptException {
        Invocable invocable = (Invocable) engine;
        try {
            return (String) invocable.invokeFunction("customReportFormat", data);
        } catch (NoSuchMethodException e) {
            throw new ScriptException("Report function not found");
        }
    }
    
    public Object updateConfiguration() throws ScriptException {
        Invocable invocable = (Invocable) engine;
        try {
            return invocable.invokeFunction("processConfiguration");
        } catch (NoSuchMethodException e) {
            throw new ScriptException("Configuration function not found");
        }
    }
    
    public void loadUserScript(String script) throws ScriptException {
        engine.eval(script);
        ruleCache.put("userScript", script);
    }
    
    public void performCalculations() throws ScriptException {
        // Complex mathematical calculations in JavaScript
        String mathScript = 
            "function calculateCompoundInterest(principal, rate, time, frequency) {" +
            "    return principal * Math.pow(1 + (rate / frequency), frequency * time);" +
            "}" +
            "" +
            "function analyzeDataSet(numbers) {" +
            "    var sum = numbers.reduce(function(a, b) { return a + b; }, 0);" +
            "    var mean = sum / numbers.length;" +
            "    var variance = numbers.reduce(function(acc, val) {" +
            "        return acc + Math.pow(val - mean, 2);" +
            "    }, 0) / numbers.length;" +
            "    return {" +
            "        sum: sum," +
            "        mean: mean," +
            "        variance: variance," +
            "        stdDev: Math.sqrt(variance)" +
            "    };" +
            "}";
        
        engine.eval(mathScript);
        
        // Use JavaScript for calculations
        Bindings bindings = engine.createBindings();
        bindings.put("principal", 10000.0);
        bindings.put("rate", 0.05);
        bindings.put("time", 10);
        bindings.put("frequency", 12);
        
        Object interestResult = engine.eval("calculateCompoundInterest(principal, rate, time, frequency)", bindings);
        
        double[] dataSet = {1.2, 3.4, 5.6, 7.8, 9.0, 2.3, 4.5, 6.7, 8.9, 1.1};
        Object analysisResult = ((Invocable) engine).invokeFunction("analyzeDataSet", dataSet);
    }
    
    public static class ConfigurationProcessor {
        private Map<String, String> properties = new HashMap<>();
        
        public void setProperty(String key, String value) {
            properties.put(key, value);
        }
        
        public String getProperty(String key) {
            return properties.get(key);
        }
        
        public Map<String, String> getAllProperties() {
            return new HashMap<>(properties);
        }
    }
    
    public static class UtilityFunctions {
        public String formatCurrency(double amount) {
            return String.format("$%.2f", amount);
        }
        
        public long getCurrentTimestamp() {
            return System.currentTimeMillis();
        }
        
        public String generateId() {
            return "ID_" + System.currentTimeMillis();
        }
    }
}
