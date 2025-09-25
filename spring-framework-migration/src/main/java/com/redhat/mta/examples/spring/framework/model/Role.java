package com.redhat.mta.examples.spring.framework.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Role entity demonstrating deprecated JPA patterns.
 */
@Entity
@Table(name = "roles")
public class Role implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    // Constructors
    public Role() {}
    
    public Role(String name) {
        this.name = name;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
