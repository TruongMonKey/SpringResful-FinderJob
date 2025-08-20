package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.User;
import com.example.JobFinder.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public User updateUser(long id, User updateUser) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            User exisUser = userOptional.get();
            exisUser.setName(updateUser.getName());
            exisUser.setEmail(updateUser.getEmail());
            exisUser.setPassWord(updateUser.getPassWord());
            return this.userRepository.save(exisUser);
        }
        return null;
    }
}
