package com.redhat.mta.examples.migration.jdk8to21;

import java.lang.StackWalker.Option;
import java.util.Optional;

/**
 * Stack analysis service using StackWalker API.
 * Replaces deprecated sun.reflect.Reflection usage from JDK 9+
 */
public class SunReflectDeprecationExample {
    
    private StackWalker walker = StackWalker.getInstance();
    private StackWalker reflectionWalker = StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE);
    
    public String getCurrentMethodName() {
        Optional<String> methodName = walker.walk(frames -> 
            frames.findFirst().map(frame -> frame.getMethodName())
        );
        return methodName.orElse("unknown");
    }
    
    public String getCallerMethodName() {
        Optional<String> callerName = walker.walk(frames -> 
            frames.skip(1).findFirst().map(frame -> frame.getMethodName())
        );
        return callerName.orElse("unknown");
    }
    
    public Class<?> getCallerClass() {
        Optional<Class<?>> callerClass = reflectionWalker.walk(frames ->
            frames.skip(1).findFirst().map(frame -> frame.getDeclaringClass())
        );
        return callerClass.orElse(null);
    }
    
    public String getFullCallChain(int maxDepth) {
        return walker.walk(frames -> {
            StringBuilder chain = new StringBuilder();
            frames.limit(maxDepth).forEach(frame -> {
                if (chain.length() > 0) {
                    chain.append(" -> ");
                }
                chain.append(frame.getClassName())
                     .append(".")
                     .append(frame.getMethodName())
                     .append("(")
                     .append(frame.getLineNumber())
                     .append(")");
            });
            return chain.toString();
        });
    }
    
    public boolean isCalledFromPackage(String packagePrefix) {
        Optional<Boolean> result = reflectionWalker.walk(frames ->
            frames.skip(1)
                  .findFirst()
                  .map(frame -> frame.getDeclaringClass().getPackage().getName().startsWith(packagePrefix))
        );
        return result.orElse(false);
    }
    
    public StackFrame getCurrentFrame() {
        Optional<StackWalker.StackFrame> frame = walker.walk(frames -> frames.findFirst());
        if (frame.isPresent()) {
            StackWalker.StackFrame f = frame.get();
            return new StackFrame(f.getClassName(), f.getMethodName(), f.getLineNumber());
        }
        return null;
    }
    
    public StackFrame[] getStackFrames(int count) {
        return walker.walk(frames ->
            frames.limit(count)
                  .map(f -> new StackFrame(f.getClassName(), f.getMethodName(), f.getLineNumber()))
                  .toArray(StackFrame[]::new)
        );
    }
    
    public boolean isCallerTrusted() {
        Optional<Boolean> trusted = reflectionWalker.walk(frames ->
            frames.skip(1)
                  .findFirst()
                  .map(frame -> {
                      String className = frame.getDeclaringClass().getName();
                      return className.startsWith("java.") || 
                             className.startsWith("javax.") ||
                             className.startsWith("com.redhat.trusted");
                  })
        );
        return trusted.orElse(false);
    }
    
    public void validateCallerAccess(String requiredPackage) throws SecurityException {
        Optional<String> callerPackage = reflectionWalker.walk(frames ->
            frames.skip(1)
                  .findFirst()
                  .map(frame -> frame.getDeclaringClass().getPackage().getName())
        );
        
        if (!callerPackage.isPresent() || !callerPackage.get().startsWith(requiredPackage)) {
            throw new SecurityException("Access denied for caller: " + callerPackage.orElse("unknown"));
        }
    }
    
    public ClassLoader getCallerClassLoader() {
        Optional<ClassLoader> loader = reflectionWalker.walk(frames ->
            frames.skip(1)
                  .findFirst()
                  .map(frame -> frame.getDeclaringClass().getClassLoader())
        );
        return loader.orElse(null);
    }
    
    public static class StackFrame {
        public final String className;
        public final String methodName;
        public final int lineNumber;
        
        public StackFrame(String className, String methodName, int lineNumber) {
            this.className = className;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
        }
        
        @Override
        public String toString() {
            return className + "." + methodName + "(" + lineNumber + ")";
        }
    }
}