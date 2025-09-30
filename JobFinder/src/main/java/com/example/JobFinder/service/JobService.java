package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.Job;
import com.example.JobFinder.domain.Skill;
import com.example.JobFinder.domain.response.ResCreateJobDTO;
import com.example.JobFinder.domain.response.ResUpdateJobDTO;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.repository.JobRepository;
import com.example.JobFinder.repository.SkillRepository;
import com.example.JobFinder.util.mapper.JobMapper;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO createJob(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }
        Job currentJob = this.jobRepository.save(j);

        return JobMapper.mapToResCreateJobDTO(currentJob);
    }

    public ResUpdateJobDTO update(Job j) {
        Job currentJob = jobRepository.findById(j.getId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + j.getId()));

        currentJob.setName(j.getName());
        currentJob.setSalary(j.getSalary());
        currentJob.setQuantity(j.getQuantity());
        currentJob.setLocation(j.getLocation());
        currentJob.setLevel(j.getLevel());
        currentJob.setActive(j.isActive());
        currentJob.setStartDate(j.getStartDate());
        currentJob.setEndDate(j.getEndDate());

        if (j.getSkills() != null && !j.getSkills().isEmpty()) {
            List<Long> reqSkills = j.getSkills().stream().map(Skill::getId).toList();
            currentJob.setSkills(skillRepository.findByIdIn(reqSkills));
        }

        Job savedJob = jobRepository.save(currentJob);
        return JobMapper.mapToResUpdateJobDTO(savedJob);
    }

    public Optional<Job> fetchJobById(long id) {
        return jobRepository.findById(id);
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginationDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> pageUser = this.jobRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageUser.getTotalPages());
        mt.setTotal(pageUser.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageUser.getContent());

        return rs;
    }

}
