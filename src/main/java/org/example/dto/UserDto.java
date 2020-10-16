package org.example.dto;

import org.example.model.Job;
import org.example.model.User;


public class UserDto {
    private Long id;
    private String name;
    private String surname;
    private byte age;
    private String jobName;
    private int salary;

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

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public User getUser() {
        Job job = !jobName.isEmpty() ? new Job(jobName, salary) : null;
        return new User(name, surname, age, job);
    }

    public void extractDataFrom(User user) {
        id = user.getId();
        name = user.getName();
        surname = user.getSurname();
        age = user.getAge();
        jobName = user.getJob().map(Job::getName).orElse("");
        salary = user.getJob().map(Job::getSalary).orElse(0);
    }
}