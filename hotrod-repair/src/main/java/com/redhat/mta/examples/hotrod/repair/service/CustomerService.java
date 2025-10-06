package com.redhat.mta.examples.hotrod.repair.service;

import com.redhat.mta.examples.hotrod.repair.entity.Customer;
import com.redhat.mta.examples.hotrod.repair.entity.Vehicle;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Customer service using deprecated EJB 3.2 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.ejb imports (migrate to jakarta.ejb)
 * - javax.persistence imports (migrate to jakarta.persistence)
 * - Legacy @Local and @Remote interface patterns
 * - Deprecated EntityManager usage patterns
 */
@Stateless
@Local(CustomerServiceLocal.class)
@Remote(CustomerServiceRemote.class)
public class CustomerService implements CustomerServiceLocal, CustomerServiceRemote {
    
    private static final Logger logger = Logger.getLogger(CustomerService.class.getName());

    @PersistenceContext(unitName = "hotrodsPU")
    private EntityManager entityManager;

    @Override
    public Customer createCustomer(Customer customer) {
        logger.info("Creating new customer: " + customer.getEmail());
        
        // Check for duplicate email using deprecated Query API
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.email = :email");
        query.setParameter("email", customer.getEmail());
        Long count = (Long) query.getSingleResult();
        
        if (count > 0) {
            throw new IllegalArgumentException("Customer with email already exists: " + customer.getEmail());
        }
        
        customer.setRegistrationDate(new Date());
        customer.setIsActive(Boolean.TRUE);
        
        entityManager.persist(customer);
        entityManager.flush(); // Deprecated pattern - should rely on transaction boundary
        
        logger.info("Customer created successfully with ID: " + customer.getCustomerId());
        return customer;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        logger.info("Updating customer: " + customer.getCustomerId());
        
        Customer existingCustomer = entityManager.find(Customer.class, customer.getCustomerId());
        if (existingCustomer == null) {
            throw new IllegalArgumentException("Customer not found: " + customer.getCustomerId());
        }
        
        // Deprecated manual merge pattern
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        existingCustomer.setAddress(customer.getAddress());
        existingCustomer.setCity(customer.getCity());
        existingCustomer.setState(customer.getState());
        existingCustomer.setZipCode(customer.getZipCode());
        existingCustomer.setCustomerNotes(customer.getCustomerNotes());
        existingCustomer.setPreferredContactMethod(customer.getPreferredContactMethod());
        
        Customer mergedCustomer = entityManager.merge(existingCustomer);
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Customer updated successfully: " + mergedCustomer.getCustomerId());
        return mergedCustomer;
    }

    @Override
    public Customer findCustomerById(Long customerId) {
        logger.info("Finding customer by ID: " + customerId);
        
        Customer customer = entityManager.find(Customer.class, customerId);
        if (customer != null) {
            // Deprecated eager loading pattern
            customer.getVehicles().size(); // Force lazy loading
            customer.getServiceOrders().size(); // Force lazy loading
        }
        
        return customer;
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        logger.info("Finding customer by email: " + email);
        
        // Using deprecated named query
        TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findByEmail", Customer.class);
        query.setParameter("email", email);
        
        List<Customer> customers = query.getResultList();
        return customers.isEmpty() ? null : customers.get(0);
    }

    @Override
    public List<Customer> findAllCustomers() {
        logger.info("Finding all customers");
        
        // Using deprecated named query
        TypedQuery<Customer> query = entityManager.createNamedQuery("Customer.findAll", Customer.class);
        List<Customer> customers = query.getResultList();
        
        // Deprecated pattern - forcing lazy loading in service layer
        for (Customer customer : customers) {
            customer.getVehicles().size();
        }
        
        return customers;
    }

    @Override
    public List<Customer> findActiveCustomers() {
        logger.info("Finding active customers");
        
        // Deprecated raw query usage
        Query query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.isActive = true ORDER BY c.lastName, c.firstName");
        @SuppressWarnings("unchecked")
        List<Customer> customers = query.getResultList();
        
        return customers;
    }

    @Override
    public void deactivateCustomer(Long customerId) {
        logger.info("Deactivating customer: " + customerId);
        
        Customer customer = entityManager.find(Customer.class, customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        customer.setIsActive(Boolean.FALSE);
        customer.setLastVisitDate(new Date());
        
        // Deprecated pattern - should use merge or let transaction handle it
        entityManager.persist(customer);
        entityManager.flush();
        
        logger.info("Customer deactivated: " + customerId);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        logger.info("Deleting customer: " + customerId);
        
        Customer customer = entityManager.find(Customer.class, customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + customerId);
        }
        
        // Deprecated cascade deletion pattern - should be handled by database constraints
        List<Vehicle> vehicles = customer.getVehicles();
        if (vehicles != null && !vehicles.isEmpty()) {
            for (Vehicle vehicle : vehicles) {
                entityManager.remove(vehicle);
            }
        }
        
        entityManager.remove(customer);
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Customer deleted: " + customerId);
    }

    @Override
    public Long getCustomerCount() {
        logger.info("Getting total customer count");
        
        // Deprecated raw query
        Query query = entityManager.createQuery("SELECT COUNT(c) FROM Customer c");
        return (Long) query.getSingleResult();
    }

    @Override
    public List<Customer> searchCustomersByName(String searchTerm) {
        logger.info("Searching customers by name: " + searchTerm);
        
        // Deprecated string concatenation in queries
        String queryString = "SELECT c FROM Customer c WHERE " +
                           "LOWER(c.firstName) LIKE :searchTerm OR " +
                           "LOWER(c.lastName) LIKE :searchTerm " +
                           "ORDER BY c.lastName, c.firstName";
        
        TypedQuery<Customer> query = entityManager.createQuery(queryString, Customer.class);
        query.setParameter("searchTerm", "%" + searchTerm.toLowerCase() + "%");
        
        return query.getResultList();
    }

    @Override
    public void updateLastVisitDate(Long customerId) {
        logger.info("Updating last visit date for customer: " + customerId);
        
        // Deprecated bulk update pattern
        Query query = entityManager.createQuery("UPDATE Customer c SET c.lastVisitDate = :visitDate WHERE c.customerId = :customerId");
        query.setParameter("visitDate", new Date());
        query.setParameter("customerId", customerId);
        
        int updatedRows = query.executeUpdate();
        logger.info("Updated last visit date, rows affected: " + updatedRows);
    }
}
