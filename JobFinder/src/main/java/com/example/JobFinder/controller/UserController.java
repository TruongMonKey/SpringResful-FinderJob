package com.example.JobFinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.JobFinder.domain.User;
import com.example.JobFinder.service.UserService;

@RestController
public class UserController {

    public final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("user/create")
    public User createNewUser(@RequestBody User postUser) {
        User monkeyUser = this.userService.handleCreateUser(postUser);
        return monkeyUser;
    }

    @DeleteMapping("user/delete/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        this.userService.handleDeleteUser(id);
        return "delete User";
    }

    @GetMapping("user/{id}")
    public User fetchUserById(@PathVariable("id") long id) {
        return this.userService.fetchUserById(id);
    }

    @GetMapping("user")
    public List<User> fetchAllUser() {
        return this.userService.fetchAllUser();
    }

    @PutMapping("user/update/{id}")
    public User updateUserById(@PathVariable("id") long id, @RequestBody User updateUser) {
        return this.userService.updateUser(id, updateUser);
    }
}
