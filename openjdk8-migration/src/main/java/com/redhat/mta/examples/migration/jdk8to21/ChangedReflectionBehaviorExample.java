package com.redhat.mta.examples.migration.jdk8to21;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Reflection utilities accessing internal JDK APIs.
 * Strong encapsulation in JDK 9+ restricts access to internal APIs
 */
public class ChangedReflectionBehaviorExample {
    
    public Object accessUnsafeInstance() throws Exception {
        // Access sun.misc.Unsafe - restricted in JDK 9+
        Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
        Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        return theUnsafeField.get(null);
    }
    
    public long allocateMemory(long size) throws Exception {
        Object unsafe = accessUnsafeInstance();
        Class<?> unsafeClass = unsafe.getClass();
        Method allocateMemoryMethod = unsafeClass.getMethod("allocateMemory", long.class);
        return (Long) allocateMemoryMethod.invoke(unsafe, size);
    }
    
    public void freeMemory(long address) throws Exception {
        Object unsafe = accessUnsafeInstance();
        Class<?> unsafeClass = unsafe.getClass();
        Method freeMemoryMethod = unsafeClass.getMethod("freeMemory", long.class);
        freeMemoryMethod.invoke(unsafe, address);
    }
    
    public Object accessStringInternals(String testString) throws Exception {
        // Access String internal value field
        Field valueField = String.class.getDeclaredField("value");
        valueField.setAccessible(true);
        return valueField.get(testString);
    }
    
    public Object accessSystemSecurity() throws Exception {
        // Access System.security field - restricted in JDK 9+
        Class<?> systemClass = Class.forName("java.lang.System");
        Field securityField = systemClass.getDeclaredField("security");
        securityField.setAccessible(true);
        return securityField.get(null);
    }
    
    public Method getClassLoaderMethod() throws Exception {
        // Access ClassLoader.findLoadedClass - restricted in JDK 9+
        Class<?> classLoaderClass = ClassLoader.class;
        Method findLoadedClassMethod = classLoaderClass.getDeclaredMethod("findLoadedClass", String.class);
        findLoadedClassMethod.setAccessible(true);
        return findLoadedClassMethod;
    }
    
    public Object findLoadedClass(ClassLoader loader, String className) throws Exception {
        Method findLoadedClassMethod = getClassLoaderMethod();
        return findLoadedClassMethod.invoke(loader, className);
    }
    
    public String getModuleInfo(Class<?> clazz) {
        try {
            // JDK 9+ Module API
            Class<?> moduleClass = Class.forName("java.lang.Module");
            Method getModuleMethod = Class.class.getMethod("getModule");
            Object thisModule = getModuleMethod.invoke(clazz);
            
            Method getNameMethod = moduleClass.getMethod("getName");
            Object moduleName = getNameMethod.invoke(thisModule);
            
            return moduleName != null ? moduleName.toString() : "unnamed";
        } catch (Exception e) {
            return "module-info-unavailable";
        }
    }
    
    public boolean canAccessInternalAPI(String apiClassName) {
        try {
            Class.forName(apiClassName);
            return true;
        } catch (ClassNotFoundException | SecurityException e) {
            return false;
        }
    }
    
    public void testInternalAPIAccess() {
        // Test various internal API access patterns
        boolean canAccessUnsafe = canAccessInternalAPI("sun.misc.Unsafe");
        boolean canAccessReflection = canAccessInternalAPI("sun.reflect.Reflection");
        boolean canAccessJVMInternals = canAccessInternalAPI("jdk.internal.misc.Unsafe");
        
        // Store results for analysis
    }
    
    public ClassLoader getBootstrapClassLoader() {
        // Bootstrap classloader is null
        return String.class.getClassLoader();
    }
    
    public boolean isSystemClass(Class<?> clazz) {
        ClassLoader loader = clazz.getClassLoader();
        return loader == null; // Bootstrap classloader indicates system class
    }
}