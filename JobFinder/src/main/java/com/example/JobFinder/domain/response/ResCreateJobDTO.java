package com.example.JobFinder.domain.response;

import java.time.Instant;
import java.util.List;

import com.example.JobFinder.util.constant.LevelEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    private List<String> skills;
}
