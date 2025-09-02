package com.example.JobFinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.JobFinder.domain.User;
import com.example.JobFinder.service.UserService;
import com.example.JobFinder.util.errors.IdInvalidException;

@RestController
public class UserController {

    public final UserService userService;
    public final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("users/create")
    public ResponseEntity<User> createNewUser(@RequestBody User postUser) {
        String hashPassword = this.passwordEncoder.encode(postUser.getPassWord());
        postUser.setPassWord(hashPassword);
        User monkeyUser = this.userService.handleCreateUser(postUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(monkeyUser);
    }

    @DeleteMapping("users/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        if (id >= 1500) {
            throw new IdInvalidException("Id khong lon hon 1500");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("ericUser");
    }

    @GetMapping("users/{id}")
    public ResponseEntity<User> fetchUserById(@PathVariable("id") long id) {
        User user = this.userService.fetchUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user); // 200 OK + data
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found
        }
    }

    @GetMapping("users")
    public ResponseEntity<List<User>> fetchAllUser() {
        List<User> users = this.userService.fetchAllUser();
        return ResponseEntity.ok(users);
    }

    @PutMapping("users/update/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable("id") long id, @RequestBody User updateUser) {
        User updatedUser = this.userService.updateUser(id, updateUser);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
