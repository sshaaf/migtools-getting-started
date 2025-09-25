package com.redhat.mta.examples.migration.jdk8to21;

import javax.rmi.PortableRemoteObject;
import javax.activity.ActivityRequiredException;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Distributed service using CORBA and RMI-IIOP APIs.
 * CORBA module removed in JDK 11+
 */
public class RemovedCORBAExample {
    
    public RemoteCalculator createCalculator() throws RemoteException {
        return new CalculatorImpl();
    }
    
    public void bindService(Remote obj, String name) throws Exception {
        java.rmi.Naming.rebind("//localhost/" + name, obj);
    }
    
    public Remote lookupService(String url) throws Exception {
        return java.rmi.Naming.lookup(url);
    }
    
    public interface RemoteCalculator extends Remote {
        int calculate(int a, int b) throws RemoteException;
    }
    
    public static class CalculatorImpl extends PortableRemoteObject implements RemoteCalculator {
        public CalculatorImpl() throws RemoteException {
            super();
        }
        
        @Override
        public int calculate(int a, int b) throws RemoteException {
            return a + b;
        }
    }
}
