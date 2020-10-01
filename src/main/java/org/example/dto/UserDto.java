package org.example.dto;

import org.example.model.Job;
import org.example.model.User;


public class UserDto {
    private final User user = new User();
    private final Job job = new Job();

    public User getUser() {
        return user;
    }

    public Job getJob() {
        return job;
    }
}
