package org.konveyor.springboot.annotations;

import javax.persistence.*;

@Entity
@Table(name = "heroes")
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int level;
    private String power;

    public Hero() {}

    public Hero(String name, int level, String power) {
        this.name = name;
        this.level = level;
        this.power = power;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getPower() { return power; }
    public void setPower(String power) { this.power = power; }
}
