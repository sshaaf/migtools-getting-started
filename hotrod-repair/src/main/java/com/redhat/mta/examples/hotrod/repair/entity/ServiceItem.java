package com.redhat.mta.examples.hotrod.repair.entity;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ServiceItem entity using deprecated JPA 2.1 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.persistence imports (migrate to jakarta.persistence)
 * - Legacy validation annotations
 */
@Entity
@Table(name = "service_items")
public class ServiceItem implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_item_id")
    private Long serviceItemId;

    @NotNull
    @Size(min = 2, max = 100)
    @Column(name = "item_description", nullable = false, length = 100)
    private String itemDescription;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @DecimalMin(value = "0.0")
    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "part_number", length = 50)
    private String partNumber;

    @Column(name = "labor_hours", precision = 4, scale = 2)
    private BigDecimal laborHours;

    @Column(name = "item_notes", length = 500)
    private String itemNotes;

    // Deprecated ManyToOne relationship with EAGER fetch
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_order_id", nullable = false)
    private ServiceOrder serviceOrder;

    // Deprecated enum definition
    public enum ItemType {
        PART, LABOR, SERVICE, DIAGNOSTIC, FLUID, TIRE, BATTERY, OTHER
    }

    // Default constructor
    public ServiceItem() {}

    // Constructor
    public ServiceItem(String itemDescription, ItemType itemType, Integer quantity, BigDecimal unitPrice) {
        this.itemDescription = itemDescription;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    // Business method to calculate total price
    public void calculateTotalPrice() {
        if (quantity != null && unitPrice != null) {
            this.totalPrice = unitPrice.multiply(new BigDecimal(quantity));
        }
    }

    // Getters and Setters
    public Long getServiceItemId() {
        return serviceItemId;
    }

    public void setServiceItemId(Long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public BigDecimal getLaborHours() {
        return laborHours;
    }

    public void setLaborHours(BigDecimal laborHours) {
        this.laborHours = laborHours;
    }

    public String getItemNotes() {
        return itemNotes;
    }

    public void setItemNotes(String itemNotes) {
        this.itemNotes = itemNotes;
    }

    public ServiceOrder getServiceOrder() {
        return serviceOrder;
    }

    public void setServiceOrder(ServiceOrder serviceOrder) {
        this.serviceOrder = serviceOrder;
    }

    @Override
    public String toString() {
        return "ServiceItem{" +
                "serviceItemId=" + serviceItemId +
                ", itemDescription='" + itemDescription + '\'' +
                ", itemType=" + itemType +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ServiceItem that = (ServiceItem) obj;
        return serviceItemId != null ? serviceItemId.equals(that.serviceItemId) : that.serviceItemId == null;
    }

    @Override
    public int hashCode() {
        return serviceItemId != null ? serviceItemId.hashCode() : 0;
    }
}
