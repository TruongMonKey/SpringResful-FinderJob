package com.example.JobFinder.domain.dto;

import java.time.Instant;

import com.example.JobFinder.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserDTO {

    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createAt;
}
