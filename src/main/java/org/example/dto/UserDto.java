package org.example.dto;

import org.example.model.Job;
import org.example.model.User;


public class UserDto {
    private User user;
    private Job job;

    public User getUser() {
        return user;
    }

    public Job getJob() {
        return job;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public User extractUser() {
        if (!job.getName().isEmpty()) {
            user.setJob(job);
        }

        return user;
    }

    @Override
    public String toString() {
        return super.toString() +  " UserDto{" +
                "user=" + user +
                ", job=" + job +
                '}';
    }
}
