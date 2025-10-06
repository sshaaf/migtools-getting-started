package com.redhat.mta.examples.migration.jdk8to21;

import java.io.*;
import java.util.jar.*;
import java.nio.file.*;
import java.util.zip.Deflater;

/**
 * JAR compression service using Pack200 API.
 * Pack200 API removed in JDK 14+
 */
public class RemovedPack200Example {
    
    public void compressJar(String inputJarPath, String outputPackPath) throws IOException {
        // Using Pack200 API - removed in JDK 14+
        try (JarFile jarFile = new JarFile(inputJarPath);
             FileOutputStream fos = new FileOutputStream(outputPackPath)) {
            
            // These classes are removed in JDK 14+
            java.util.jar.Pack200.Packer packer = java.util.jar.Pack200.newPacker();
            packer.properties().put(java.util.jar.Pack200.Packer.EFFORT, "7");
            packer.properties().put(java.util.jar.Pack200.Packer.SEGMENT_LIMIT, "1000000");
            
            packer.pack(jarFile, fos);
        }
    }
    
    public void decompressPack(String inputPackPath, String outputJarPath) throws IOException {
        // Using Pack200 API - removed in JDK 14+
        try (FileInputStream fis = new FileInputStream(inputPackPath);
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJarPath))) {
            
            java.util.jar.Pack200.Unpacker unpacker = java.util.jar.Pack200.newUnpacker();
            unpacker.unpack(fis, jos);
        }
    }
    
    public long getCompressionRatio(String originalPath, String compressedPath) throws IOException {
        long originalSize = Files.size(Paths.get(originalPath));
        long compressedSize = Files.size(Paths.get(compressedPath));
        return ((originalSize - compressedSize) * 100) / originalSize;
    }
    
    public void createCompressedJar(String outputPath, String[] fileEntries) throws IOException {
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputPath))) {
            jos.setLevel(Deflater.BEST_COMPRESSION);
            
            for (String entry : fileEntries) {
                JarEntry jarEntry = new JarEntry(entry);
                jos.putNextEntry(jarEntry);
                
                // Write file content
                String content = "Sample content for " + entry;
                jos.write(content.getBytes());
                jos.closeEntry();
            }
        }
    }
    
    public void optimizeJarForDistribution(String inputJar, String outputJar) throws IOException {
        // Alternative compression approach since Pack200 is removed
        try (JarInputStream jis = new JarInputStream(new FileInputStream(inputJar));
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(outputJar))) {
            
            jos.setLevel(Deflater.BEST_COMPRESSION);
            
            JarEntry entry;
            byte[] buffer = new byte[8192];
            
            while ((entry = jis.getNextJarEntry()) != null) {
                jos.putNextEntry(new JarEntry(entry.getName()));
                
                int bytesRead;
                while ((bytesRead = jis.read(buffer)) != -1) {
                    jos.write(buffer, 0, bytesRead);
                }
                jos.closeEntry();
            }
        }
    }
    
    public boolean isPackFileValid(String packFilePath) {
        try {
            Path path = Paths.get(packFilePath);
            return Files.exists(path) && Files.size(path) > 0;
        } catch (IOException e) {
            return false;
        }
    }
    
    public void batchCompressJars(String[] jarPaths, String outputDirectory) throws IOException {
        for (String jarPath : jarPaths) {
            String fileName = Paths.get(jarPath).getFileName().toString();
            String outputPath = outputDirectory + "/" + fileName + ".pack";
            compressJar(jarPath, outputPath);
        }
    }
}