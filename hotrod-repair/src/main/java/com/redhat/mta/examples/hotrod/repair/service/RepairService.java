package com.redhat.mta.examples.hotrod.repair.service;

import com.redhat.mta.examples.hotrod.repair.entity.ServiceOrder;
import com.redhat.mta.examples.hotrod.repair.entity.ServiceItem;
import com.redhat.mta.examples.hotrod.repair.entity.Customer;
import com.redhat.mta.examples.hotrod.repair.entity.Vehicle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Repair service using deprecated EJB 3.2 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.ejb imports (migrate to jakarta.ejb)
 * - javax.persistence imports (migrate to jakarta.persistence)
 * - Deprecated transaction attribute patterns
 * - Legacy EJB injection patterns
 */
@Stateless
public class RepairService {
    
    private static final Logger logger = Logger.getLogger(RepairService.class.getName());

    @PersistenceContext(unitName = "hotrodsPU")
    private EntityManager entityManager;

    // Deprecated EJB injection pattern
    @EJB
    private CustomerServiceLocal customerService;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServiceOrder createServiceOrder(ServiceOrder serviceOrder) {
        logger.info("Creating new service order: " + serviceOrder.getOrderNumber());
        
        // Validate customer and vehicle exist
        Customer customer = customerService.findCustomerById(serviceOrder.getCustomer().getCustomerId());
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        Vehicle vehicle = findVehicleById(serviceOrder.getVehicle().getVehicleId());
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle not found");
        }
        
        // Update customer's last visit date
        customer.setLastVisitDate(new Date());
        entityManager.merge(customer);
        
        serviceOrder.setServiceDate(new Date());
        serviceOrder.setStatus(ServiceOrder.ServiceStatus.SCHEDULED);
        
        entityManager.persist(serviceOrder);
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Service order created with ID: " + serviceOrder.getServiceOrderId());
        return serviceOrder;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServiceOrder updateServiceOrder(ServiceOrder serviceOrder) {
        logger.info("Updating service order: " + serviceOrder.getServiceOrderId());
        
        ServiceOrder existingOrder = entityManager.find(ServiceOrder.class, serviceOrder.getServiceOrderId());
        if (existingOrder == null) {
            throw new IllegalArgumentException("Service order not found");
        }
        
        // Deprecated manual property copying
        existingOrder.setServiceDescription(serviceOrder.getServiceDescription());
        existingOrder.setStatus(serviceOrder.getStatus());
        existingOrder.setPriority(serviceOrder.getPriority());
        existingOrder.setEstimatedCompletion(serviceOrder.getEstimatedCompletion());
        existingOrder.setActualCompletion(serviceOrder.getActualCompletion());
        existingOrder.setEstimatedCost(serviceOrder.getEstimatedCost());
        existingOrder.setActualCost(serviceOrder.getActualCost());
        existingOrder.setMechanicNotes(serviceOrder.getMechanicNotes());
        existingOrder.setCustomerNotes(serviceOrder.getCustomerNotes());
        existingOrder.setWarrantyMonths(serviceOrder.getWarrantyMonths());
        existingOrder.setIsWarrantyWork(serviceOrder.getIsWarrantyWork());
        
        ServiceOrder updatedOrder = entityManager.merge(existingOrder);
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Service order updated: " + updatedOrder.getServiceOrderId());
        return updatedOrder;
    }

    public ServiceOrder findServiceOrderById(Long serviceOrderId) {
        logger.info("Finding service order by ID: " + serviceOrderId);
        
        ServiceOrder serviceOrder = entityManager.find(ServiceOrder.class, serviceOrderId);
        if (serviceOrder != null) {
            // Deprecated eager loading pattern
            serviceOrder.getServiceItems().size(); // Force lazy loading
            serviceOrder.getCustomer().getFirstName(); // Force lazy loading
            serviceOrder.getVehicle().getMake(); // Force lazy loading
        }
        
        return serviceOrder;
    }

    public List<ServiceOrder> findServiceOrdersByStatus(ServiceOrder.ServiceStatus status) {
        logger.info("Finding service orders by status: " + status);
        
        // Using deprecated named query
        TypedQuery<ServiceOrder> query = entityManager.createNamedQuery("ServiceOrder.findByStatus", ServiceOrder.class);
        query.setParameter("status", status);
        
        List<ServiceOrder> orders = query.getResultList();
        
        // Deprecated pattern - forcing lazy loading in service layer
        for (ServiceOrder order : orders) {
            order.getServiceItems().size();
        }
        
        return orders;
    }

    public List<ServiceOrder> findServiceOrdersByCustomer(Long customerId) {
        logger.info("Finding service orders by customer: " + customerId);
        
        TypedQuery<ServiceOrder> query = entityManager.createNamedQuery("ServiceOrder.findByCustomer", ServiceOrder.class);
        query.setParameter("customerId", customerId);
        
        return query.getResultList();
    }

    public List<ServiceOrder> findServiceOrdersByDateRange(Date startDate, Date endDate) {
        logger.info("Finding service orders by date range: " + startDate + " to " + endDate);
        
        TypedQuery<ServiceOrder> query = entityManager.createNamedQuery("ServiceOrder.findByDateRange", ServiceOrder.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        
        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServiceItem addServiceItem(Long serviceOrderId, ServiceItem serviceItem) {
        logger.info("Adding service item to order: " + serviceOrderId);
        
        ServiceOrder serviceOrder = entityManager.find(ServiceOrder.class, serviceOrderId);
        if (serviceOrder == null) {
            throw new IllegalArgumentException("Service order not found");
        }
        
        serviceItem.setServiceOrder(serviceOrder);
        serviceItem.calculateTotalPrice();
        
        entityManager.persist(serviceItem);
        
        // Deprecated pattern - manual total recalculation
        recalculateServiceOrderTotal(serviceOrder);
        
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Service item added with ID: " + serviceItem.getServiceItemId());
        return serviceItem;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void removeServiceItem(Long serviceItemId) {
        logger.info("Removing service item: " + serviceItemId);
        
        ServiceItem serviceItem = entityManager.find(ServiceItem.class, serviceItemId);
        if (serviceItem == null) {
            throw new IllegalArgumentException("Service item not found");
        }
        
        ServiceOrder serviceOrder = serviceItem.getServiceOrder();
        entityManager.remove(serviceItem);
        
        // Deprecated pattern - manual total recalculation
        recalculateServiceOrderTotal(serviceOrder);
        
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Service item removed: " + serviceItemId);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public ServiceOrder completeServiceOrder(Long serviceOrderId, BigDecimal finalCost, String mechanicNotes) {
        logger.info("Completing service order: " + serviceOrderId);
        
        ServiceOrder serviceOrder = entityManager.find(ServiceOrder.class, serviceOrderId);
        if (serviceOrder == null) {
            throw new IllegalArgumentException("Service order not found");
        }
        
        serviceOrder.setStatus(ServiceOrder.ServiceStatus.COMPLETED);
        serviceOrder.setActualCompletion(new Date());
        serviceOrder.setActualCost(finalCost);
        serviceOrder.setMechanicNotes(mechanicNotes);
        
        // Update customer's last visit date
        customerService.updateLastVisitDate(serviceOrder.getCustomer().getCustomerId());
        
        ServiceOrder completedOrder = entityManager.merge(serviceOrder);
        entityManager.flush(); // Deprecated explicit flush
        
        logger.info("Service order completed: " + completedOrder.getServiceOrderId());
        return completedOrder;
    }

    public Vehicle findVehicleById(Long vehicleId) {
        logger.info("Finding vehicle by ID: " + vehicleId);
        return entityManager.find(Vehicle.class, vehicleId);
    }

    public List<Vehicle> findVehiclesByCustomer(Long customerId) {
        logger.info("Finding vehicles by customer: " + customerId);
        
        TypedQuery<Vehicle> query = entityManager.createNamedQuery("Vehicle.findByCustomer", Vehicle.class);
        query.setParameter("customerId", customerId);
        
        return query.getResultList();
    }

    // Deprecated helper method with manual calculation
    private void recalculateServiceOrderTotal(ServiceOrder serviceOrder) {
        logger.info("Recalculating service order total: " + serviceOrder.getServiceOrderId());
        
        // Deprecated raw query for calculation
        Query query = entityManager.createQuery(
            "SELECT SUM(si.totalPrice) FROM ServiceItem si WHERE si.serviceOrder.serviceOrderId = :orderId");
        query.setParameter("orderId", serviceOrder.getServiceOrderId());
        
        BigDecimal total = (BigDecimal) query.getSingleResult();
        serviceOrder.setEstimatedCost(total != null ? total : BigDecimal.ZERO);
        
        entityManager.merge(serviceOrder);
    }

    public BigDecimal calculateMonthlyRevenue(int year, int month) {
        logger.info("Calculating monthly revenue for: " + year + "-" + month);
        
        // Deprecated string concatenation in query
        String queryString = "SELECT SUM(so.actualCost) FROM ServiceOrder so " +
                           "WHERE YEAR(so.actualCompletion) = :year " +
                           "AND MONTH(so.actualCompletion) = :month " +
                           "AND so.status = :status";
        
        Query query = entityManager.createQuery(queryString);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("status", ServiceOrder.ServiceStatus.COMPLETED);
        
        BigDecimal revenue = (BigDecimal) query.getSingleResult();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }
}
