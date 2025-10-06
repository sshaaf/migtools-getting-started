package com.redhat.mta.examples.migration.jdk8to21;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Resource management using finalize() method.
 * finalize() deprecated for removal in JDK 9+
 */
public class DeprecatedFinalizeExample {
    
    public DatabaseConnection createDatabaseConnection(String url) throws SQLException {
        return new DatabaseConnection(url);
    }
    
    public FileManager createFileManager(String filename) throws IOException {
        return new FileManager(filename);
    }
    
    public NetworkConnection createNetworkConnection(String host, int port) {
        return new NetworkConnection(host, port);
    }
    
    /**
     * Database connection with finalize cleanup
     */
    public static class DatabaseConnection {
        private Connection connection;
        private String url;
        
        public DatabaseConnection(String url) throws SQLException {
            this.url = url;
            this.connection = DriverManager.getConnection(url);
        }
        
        public void executeQuery(String sql) throws SQLException {
            if (connection != null && !connection.isClosed()) {
                connection.createStatement().execute(sql);
            }
        }
        
        @Override
        protected void finalize() throws Throwable {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                // Ignore cleanup errors in finalizer
            } finally {
                super.finalize();
            }
        }
    }
    
    /**
     * File manager with finalize cleanup
     */
    public static class FileManager {
        private FileOutputStream outputStream;
        private String filename;
        
        public FileManager(String filename) throws IOException {
            this.filename = filename;
            this.outputStream = new FileOutputStream(filename);
        }
        
        public void writeData(String data) throws IOException {
            if (outputStream != null) {
                outputStream.write(data.getBytes());
                outputStream.flush();
            }
        }
        
        @Override
        protected void finalize() throws Throwable {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                // Ignore cleanup errors in finalizer
            } finally {
                super.finalize();
            }
        }
    }
    
    /**
     * Network connection with finalize cleanup
     */
    public static class NetworkConnection {
        private Socket socket;
        private PrintWriter writer;
        private String host;
        private int port;
        
        public NetworkConnection(String host, int port) {
            this.host = host;
            this.port = port;
            try {
                this.socket = new Socket(host, port);
                this.writer = new PrintWriter(socket.getOutputStream(), true);
            } catch (IOException e) {
                // Connection failed, socket remains null
            }
        }
        
        public void sendMessage(String message) {
            if (writer != null) {
                writer.println(message);
            }
        }
        
        @Override
        protected void finalize() throws Throwable {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                // Ignore cleanup errors in finalizer
            } finally {
                super.finalize();
            }
        }
    }
    
    /**
     * Memory buffer using finalize() - deprecated pattern
     */
    static class NativeBuffer {
        private long nativePointer;
        private int size;
        
        public NativeBuffer(int size) {
            this.size = size;
            this.nativePointer = allocateNative(size); // Simulated native allocation
        }
        
        public void write(byte[] data, int offset) {
            if (nativePointer != 0) {
                // Simulated native write operation
                writeNative(nativePointer, data, offset);
            }
        }
        
        // Using finalize() for native cleanup - deprecated in JDK 9+
        @Override
        protected void finalize() throws Throwable {
            try {
                if (nativePointer != 0) {
                    freeNative(nativePointer); // Simulated native cleanup
                    nativePointer = 0;
                }
            } finally {
                super.finalize();
            }
        }
        
        // Simulated native methods
        private native long allocateNative(int size);
        private native void writeNative(long pointer, byte[] data, int offset);
        private native void freeNative(long pointer);
    }
}
