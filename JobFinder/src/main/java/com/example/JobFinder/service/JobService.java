package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.Company;
import com.example.JobFinder.domain.Job;
import com.example.JobFinder.domain.Skill;
import com.example.JobFinder.domain.response.ResCreateJobDTO;
import com.example.JobFinder.domain.response.ResUpdateJobDTO;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.repository.CompanyRepository;
import com.example.JobFinder.repository.JobRepository;
import com.example.JobFinder.repository.SkillRepository;
import com.example.JobFinder.util.mapper.JobMapper;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public ResCreateJobDTO createJob(Job j) {
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }

        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent()) {
                j.setCompany(cOptional.get());
            }
        }

        Job currentJob = this.jobRepository.save(j);

        return JobMapper.mapToResCreateJobDTO(currentJob);
    }

    public ResUpdateJobDTO update(Job j, Job jobDB) {
        Job currentJob = jobRepository.findById(j.getId())
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + j.getId()));
        if (j.getSkills() != null && !j.getSkills().isEmpty()) {
            List<Long> reqSkills = j.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobDB.setSkills(dbSkills);
        }

        if (j.getCompany() != null) {
            Optional<Company> cOptional = this.companyRepository.findById(j.getCompany().getId());
            if (cOptional.isPresent()) {
                jobDB.setCompany(cOptional.get());
            }
        }

        jobDB.setName(j.getName());
        jobDB.setSalary(j.getSalary());
        jobDB.setQuantity(j.getQuantity());
        jobDB.setLocation(j.getLocation());
        jobDB.setLevel(j.getLevel());
        jobDB.setActive(j.isActive());
        jobDB.setStartDate(j.getStartDate());
        jobDB.setEndDate(j.getEndDate());

        Job savedJob = jobRepository.save(jobDB);
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
