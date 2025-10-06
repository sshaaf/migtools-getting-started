package com.redhat.mta.examples.hotrod.repair.service;

import com.redhat.mta.examples.hotrod.repair.entity.Customer;

import javax.ejb.Local;
import java.util.List;

/**
 * Local interface for CustomerService using deprecated EJB 3.2 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.ejb imports (migrate to jakarta.ejb)
 * - Separate local interface pattern (can be simplified in EAP 8)
 */
@Local
public interface CustomerServiceLocal {
    
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
