package com.example.JobFinder.util.mapper;

import com.example.JobFinder.domain.Resume;
import com.example.JobFinder.domain.response.ResFetchResumeDTO;

public class ResumeMapper {
    public static ResFetchResumeDTO mapToResFetchResumeDTO(Resume resume) {
        ResFetchResumeDTO dto = new ResFetchResumeDTO();
        dto.setId(resume.getId());
        dto.setEmail(resume.getEmail());
        dto.setUrl(resume.getUrl());
        dto.setStatus(resume.getStatus());
        dto.setCreatedAt(resume.getCreatedAt());
        dto.setCreatedBy(resume.getCreatedBy());
        dto.setUpdatedAt(resume.getUpdatedAt());
        dto.setCreatedBy(resume.getUpdatedBy());

        dto.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(), resume.getUser().getName()));
        dto.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(), resume.getJob().getName()));

        return dto;
    }
}
