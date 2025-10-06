package com.redhat.mta.examples.migration.jdk8to21;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.File;
import java.io.IOException;

/**
 * Employee management service using Java EE APIs.
 * JAXB, JAF, and Common Annotations removed from JDK 11+
 */
public class RemovedJavaEEAPIsExample {
    
    @Resource(name = "applicationConfig")
    private String configData;
    
    @XmlRootElement
    public static class Employee {
        private String name;
        private int id;
        private String department;
        private DataHandler attachment;
        
        public Employee() {}
        
        public Employee(String name, int id, String department) {
            this.name = name;
            this.id = id;
            this.department = department;
        }
        
        @XmlElement
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        @XmlElement
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        @XmlElement
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public DataHandler getAttachment() { return attachment; }
        public void setAttachment(DataHandler attachment) { this.attachment = attachment; }
        
        @PostConstruct
        public void initialize() {
            if (name == null) {
                name = "Unknown Employee";
            }
        }
        
        @PreDestroy
        public void cleanup() {
            // Cleanup resources
        }
    }
    
    public void initialize() {
        initializeApplication();
    }
    
    public void shutdown() {
        shutdownApplication();
    }
    
    public Employee processEmployeeData(String xmlData) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Employee.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (Employee) unmarshaller.unmarshal(new StringReader(xmlData));
    }
    
    public String serializeEmployee(Employee emp) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Employee.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        StringWriter xmlOutput = new StringWriter();
        marshaller.marshal(emp, xmlOutput);
        return xmlOutput.toString();
    }
    
    public DataHandler createAttachment(File file) throws IOException {
        DataSource dataSource = new FileDataSource(file);
        return new DataHandler(dataSource);
    }
    
    public void processAttachment(DataHandler attachment) throws IOException {
        String contentType = attachment.getContentType();
        String fileName = attachment.getName();
        
        if (attachment.getDataSource() instanceof FileDataSource) {
            // Process file-based attachment
            processFileAttachment((FileDataSource) attachment.getDataSource());
        }
    }
    
    private void processFileAttachment(FileDataSource fileSource) {
        File file = fileSource.getFile();
        // Process file attachment
    }
    
    @PostConstruct
    public void initializeApplication() {
        configData = "Default Configuration";
        // Application initialization logic
    }
    
    @PreDestroy
    public void shutdownApplication() {
        // Cleanup application resources
        configData = null;
    }
    
    private void processEmployee(Employee emp) {
        // Business logic using Common Annotations
        if (emp != null) {
            emp.initialize(); // Calls @PostConstruct method
            
            // Process employee data
            String report = generateReport(emp);
            
            emp.cleanup(); // Calls @PreDestroy method
        }
    }
    
    private String generateReport(Employee emp) {
        return String.format("Employee Report: %s (ID: %d) in %s department", 
                           emp.getName(), emp.getId(), emp.getDepartment());
    }
}
