package org.example.model;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(
        columnNames = {
                "name", "surname", "age", "job_id"
        }))
public class User {

    @Id
    @GeneratedValue(generator = "ID_GENERATOR")
    private Long id;

    private String name;

    private String surname;

    private byte age;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn
    private Job job;

    public User() {}

    public User(String name, String surname, byte age, Job job) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.job = job;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public byte getAge() {
        return age;
    }

    public void setAge(byte age) {
        this.age = age;
    }

    public Optional<Job> getJob() {
        return Optional.ofNullable(job);
    }

    public void setJob(Job job) {
        this.job = job;
    }

    @Override
    public boolean equals(Object obj) {
//        System.out.println("In user equals");
        return this == obj || (obj instanceof User &&
                ((User) obj).name.equalsIgnoreCase(name) &&
                ((User) obj).surname.equalsIgnoreCase(surname) &&
                ((User) obj).age == age &&
                Objects.equals(job, ((User) obj).job));
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, age, job);
    }

    @Override
    public String toString() {
//        return "User{" +
//                "name='" + name + '\'' +
//                ", surname='" + surname + '\'' +
//                ", age=" + age +
//                ", job=" + job +
//                '}';
        return name + ' ' + surname + ' ' + age + ' ' + job;
    }
}
