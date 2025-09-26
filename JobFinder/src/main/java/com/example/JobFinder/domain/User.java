package com.example.JobFinder.domain;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String email;
    private String passWord;

    private int age;
    private String gender;
    private String address;
    private String refreshToken;
    private Instant createAt;
    private Instant updateAt;
    private String createBy;
    private String updateBy;

}
