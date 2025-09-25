package com.redhat.mta.examples.hotrod.repair.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * ServiceOrder entity using deprecated JPA 2.1 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.persistence imports (migrate to jakarta.persistence)
 * - Legacy enum mapping strategies
 * - @Temporal annotation patterns
 */
@Entity
@Table(name = "service_orders")
@NamedQueries({
    @NamedQuery(name = "ServiceOrder.findByStatus", query = "SELECT s FROM ServiceOrder s WHERE s.status = :status"),
    @NamedQuery(name = "ServiceOrder.findByCustomer", query = "SELECT s FROM ServiceOrder s WHERE s.customer.customerId = :customerId"),
    @NamedQuery(name = "ServiceOrder.findByDateRange", 
                query = "SELECT s FROM ServiceOrder s WHERE s.serviceDate BETWEEN :startDate AND :endDate")
})
public class ServiceOrder implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_order_id")
    private Long serviceOrderId;

    @NotNull
    @Size(min = 5, max = 20)
    @Column(name = "order_number", nullable = false, unique = true, length = 20)
    private String orderNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "service_date", nullable = false)
    private Date serviceDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "estimated_completion")
    private Date estimatedCompletion;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "actual_completion")
    private Date actualCompletion;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ServiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false, length = 10)
    private ServicePriority priority;

    @NotNull
    @Size(min = 10, max = 1000)
    @Column(name = "service_description", nullable = false, length = 1000)
    private String serviceDescription;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "estimated_cost", precision = 10, scale = 2)
    private BigDecimal estimatedCost;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "actual_cost", precision = 10, scale = 2)
    private BigDecimal actualCost;

    @Column(name = "mechanic_notes", length = 2000)
    private String mechanicNotes;

    @Column(name = "customer_notes", length = 1000)
    private String customerNotes;

    @Column(name = "warranty_months")
    private Integer warrantyMonths;

    @Column(name = "is_warranty_work", nullable = false)
    private Boolean isWarrantyWork = Boolean.FALSE;

    // Deprecated ManyToOne relationships with EAGER fetch
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Deprecated bidirectional relationship
    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceItem> serviceItems;

    // Deprecated enum definitions - should be moved to separate classes in EAP 8
    public enum ServiceStatus {
        SCHEDULED, IN_PROGRESS, WAITING_PARTS, COMPLETED, CANCELLED, ON_HOLD
    }

    public enum ServicePriority {
        LOW, NORMAL, HIGH, URGENT
    }

    // Default constructor
    public ServiceOrder() {
        this.serviceDate = new Date();
        this.status = ServiceStatus.SCHEDULED;
        this.priority = ServicePriority.NORMAL;
    }

    // Constructor
    public ServiceOrder(String orderNumber, Customer customer, Vehicle vehicle, String serviceDescription) {
        this();
        this.orderNumber = orderNumber;
        this.customer = customer;
        this.vehicle = vehicle;
        this.serviceDescription = serviceDescription;
    }

    // Getters and Setters
    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public Date getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public void setEstimatedCompletion(Date estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }

    public Date getActualCompletion() {
        return actualCompletion;
    }

    public void setActualCompletion(Date actualCompletion) {
        this.actualCompletion = actualCompletion;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public ServicePriority getPriority() {
        return priority;
    }

    public void setPriority(ServicePriority priority) {
        this.priority = priority;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public BigDecimal getActualCost() {
        return actualCost;
    }

    public void setActualCost(BigDecimal actualCost) {
        this.actualCost = actualCost;
    }

    public String getMechanicNotes() {
        return mechanicNotes;
    }

    public void setMechanicNotes(String mechanicNotes) {
        this.mechanicNotes = mechanicNotes;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }

    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }

    public Integer getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(Integer warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public Boolean getIsWarrantyWork() {
        return isWarrantyWork;
    }

    public void setIsWarrantyWork(Boolean isWarrantyWork) {
        this.isWarrantyWork = isWarrantyWork;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public List<ServiceItem> getServiceItems() {
        return serviceItems;
    }

    public void setServiceItems(List<ServiceItem> serviceItems) {
        this.serviceItems = serviceItems;
    }

    @Override
    public String toString() {
        return "ServiceOrder{" +
                "serviceOrderId=" + serviceOrderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", serviceDate=" + serviceDate +
                ", status=" + status +
                ", priority=" + priority +
                ", estimatedCost=" + estimatedCost +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ServiceOrder that = (ServiceOrder) obj;
        return serviceOrderId != null ? serviceOrderId.equals(that.serviceOrderId) : that.serviceOrderId == null;
    }

    @Override
    public int hashCode() {
        return serviceOrderId != null ? serviceOrderId.hashCode() : 0;
    }
}
