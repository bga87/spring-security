package org.example.dto;

import org.example.model.Job;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.stereotype.Component;


@Component
@Scope("prototype")
public class UserDto {

    private Long id;
    private String name;
    private String surname;
    private byte age;
    private  String login;
    private String password;
    private String jobName;
    private int salary;

    public UserDto() {
        System.out.println("UserDto constructor called");
        this.name = "Введите имя";
        this.surname = "Ведите фамилию";
        this.login = "Введите логин";
        this.jobName = "Введите наименование профессии";
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
        login = user.getSecurityDetails().getLogin();
        password = user.getSecurityDetails().getPassword();
        jobName = user.getJob().map(Job::getName).orElse("");
        salary = user.getJob().map(Job::getSalary).orElse(0);
    }

    @Override
    public String toString() {
        return super.toString() +  " UserDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", jobName='" + jobName + '\'' +
                ", salary=" + salary +
                '}';
    }
}