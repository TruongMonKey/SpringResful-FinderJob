package com.example.JobFinder.util.mapper;

import java.util.stream.Collectors;

import com.example.JobFinder.domain.Job;
import com.example.JobFinder.domain.Skill;
import com.example.JobFinder.domain.response.ResCreateJobDTO;
import com.example.JobFinder.domain.response.ResUpdateJobDTO;

public class JobMapper {

    public static ResCreateJobDTO mapToResCreateJobDTO(Job job) {
        ResCreateJobDTO dto = new ResCreateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setActive(job.isActive());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setCreatedBy(job.getCreatedBy());

        if (job.getSkills() != null) {
            dto.setSkills(job.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public static ResUpdateJobDTO mapToResUpdateJobDTO(Job job) {
        ResUpdateJobDTO dto = new ResUpdateJobDTO();
        dto.setId(job.getId());
        dto.setName(job.getName());
        dto.setSalary(job.getSalary());
        dto.setQuantity(job.getQuantity());
        dto.setLocation(job.getLocation());
        dto.setLevel(job.getLevel());
        dto.setActive(job.isActive());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setCreatedBy(job.getCreatedBy());

        if (job.getSkills() != null) {
            dto.setSkills(job.getSkills()
                    .stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
