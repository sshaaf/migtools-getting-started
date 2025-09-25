package com.redhat.mta.examples.hotrod.repair.service;

import com.redhat.mta.examples.hotrod.repair.entity.Customer;

import javax.ejb.Remote;
import java.util.List;

/**
 * Remote interface for CustomerService using deprecated EJB 3.2 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.ejb imports (migrate to jakarta.ejb)
 * - Remote interface pattern (often unnecessary in modern architectures)
 */
@Remote
public interface CustomerServiceRemote {
    
    Customer createCustomer(Customer customer);
    
    Customer updateCustomer(Customer customer);
    
    Customer findCustomerById(Long customerId);
    
    Customer findCustomerByEmail(String email);
    
    List<Customer> findAllCustomers();
    
    List<Customer> findActiveCustomers();
    
    void deactivateCustomer(Long customerId);
    
    void deleteCustomer(Long customerId);
    
    Long getCustomerCount();
    
    List<Customer> searchCustomersByName(String searchTerm);
    
    void updateLastVisitDate(Long customerId);
}
