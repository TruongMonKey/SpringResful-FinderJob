package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.JobFinder.domain.Skill;
import com.example.JobFinder.domain.response.ResSkillDTO;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.repository.SkillRepository;
import com.example.JobFinder.util.errors.IdInvalidException;

@Service
public class SkillService {

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public boolean existsByName(String name) {
        return this.skillRepository.existsByName(name);
    }

    public Skill createSkill(Skill skill) {
        return this.skillRepository.save(skill);
    }

    public Skill updateSkill(Skill skill) throws IdInvalidException {
        Skill currentSkill = this.fetchSkillById(skill.getId());
        if (currentSkill == null) {
            return null;
        }
        if (!currentSkill.getName().equalsIgnoreCase(skill.getName())) {
            boolean exists = skillRepository.existsByName(skill.getName());
            if (exists) {
                throw new IdInvalidException("Skill name '" + skill.getName() + "' already exists!");
            }
        }

        currentSkill.setName(skill.getName());
        return skillRepository.save(currentSkill);
    }

    public Skill fetchSkillById(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        if (skillOptional.isPresent()) {
            return skillOptional.get();
        }
        return null;
    }

    public ResultPaginationDTO getAllSkill(Specification<Skill> spec, Pageable pageable) {
        Page<Skill> pageSkill = this.skillRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageSkill.getTotalPages());
        mt.setTotal(pageSkill.getTotalElements());
        rs.setMeta(mt);

        List<ResSkillDTO> resSkill = pageSkill.getContent()
                .stream()
                .map(item -> new ResSkillDTO(item.getId(), item.getName()))
                .collect(Collectors.toList());

        rs.setResult(resSkill);
        return rs;
    }

    public void deleteSkill(long id) {
        Optional<Skill> skillOptional = this.skillRepository.findById(id);
        Skill currentSkill = skillOptional.get();
        currentSkill.getJobs().forEach(job -> job.getSkills().remove(currentSkill));
        currentSkill.getSubscribers().forEach(job -> job.getSkills().remove(currentSkill));

        this.skillRepository.delete(currentSkill);
    }

    public List<Skill> getSkillsByIds(List<Long> ids) {
        return skillRepository.findByIdIn(ids);
    }

}
