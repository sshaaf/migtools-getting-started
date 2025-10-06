package com.redhat.mta.examples.hotrod.repair.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Vehicle entity using deprecated JPA 2.1 patterns.
 * 
 * Deprecated features for EAP 8 migration:
 * - javax.persistence imports (migrate to jakarta.persistence)
 * - Legacy cascade and fetch strategies
 * - @Temporal usage patterns
 */
@Entity
@Table(name = "vehicles")
@NamedQueries({
    @NamedQuery(name = "Vehicle.findByVin", query = "SELECT v FROM Vehicle v WHERE v.vinNumber = :vin"),
    @NamedQuery(name = "Vehicle.findByCustomer", query = "SELECT v FROM Vehicle v WHERE v.customer.customerId = :customerId"),
    @NamedQuery(name = "Vehicle.findByMakeModel", query = "SELECT v FROM Vehicle v WHERE v.make = :make AND v.model = :model")
})
public class Vehicle implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @NotNull
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Invalid VIN number format")
    @Column(name = "vin_number", nullable = false, unique = true, length = 17)
    private String vinNumber;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "make", nullable = false, length = 50)
    private String make;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "model", nullable = false, length = 50)
    private String model;

    @NotNull
    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "engine_type", length = 50)
    private String engineType;

    @Column(name = "transmission", length = 30)
    private String transmission;

    @Column(name = "mileage")
    private Integer mileage;

    @Pattern(regexp = "^[A-Z0-9-]{1,20}$", message = "Invalid license plate format")
    @Column(name = "license_plate", length = 20)
    private String licensePlate;

    @Temporal(TemporalType.DATE)
    @Column(name = "purchase_date")
    private Date purchaseDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "registration_date", nullable = false)
    private Date registrationDate;

    @Column(name = "vehicle_notes", length = 1000)
    private String vehicleNotes;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    // Deprecated ManyToOne relationship pattern
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // Deprecated bidirectional relationship
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceOrder> serviceOrders;

    // Default constructor
    public Vehicle() {
        this.registrationDate = new Date();
    }

    // Constructor
    public Vehicle(String vinNumber, String make, String model, Integer year, Customer customer) {
        this();
        this.vinNumber = vinNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.customer = customer;
    }

    // Getters and Setters
    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getVehicleNotes() {
        return vehicleNotes;
    }

    public void setVehicleNotes(String vehicleNotes) {
        this.vehicleNotes = vehicleNotes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<ServiceOrder> getServiceOrders() {
        return serviceOrders;
    }

    public void setServiceOrders(List<ServiceOrder> serviceOrders) {
        this.serviceOrders = serviceOrders;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleId=" + vehicleId +
                ", vinNumber='" + vinNumber + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", licensePlate='" + licensePlate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return vehicleId != null ? vehicleId.equals(vehicle.vehicleId) : vehicle.vehicleId == null;
    }

    @Override
    public int hashCode() {
        return vehicleId != null ? vehicleId.hashCode() : 0;
    }
}
