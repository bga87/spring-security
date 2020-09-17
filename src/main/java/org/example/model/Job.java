package org.example.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Job {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int salary;

    public Job() {}

    public Job(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || ((obj != null) &&
                (obj.getClass() == Job.class) &&
                ((Job) obj).name.equalsIgnoreCase(name) &&
                ((Job) obj).salary == salary);
    }
}
