package com.example.JobFinder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JobFinder.domain.Skill;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.service.SkillService;
import com.example.JobFinder.util.annotation.ApiMessage;
import com.example.JobFinder.util.errors.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create a new Skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) throws IdInvalidException {
        boolean existsByName = this.skillService.existsByName(skill.getName());
        if (existsByName) {
            throw new IdInvalidException(
                    "This skill " + skill.getName() + " already exists, please select another skill.");
        }
        Skill savedSkill = skillService.createSkill(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill);
    }

    @PutMapping("/skills")
    @ApiMessage("Update Skill")
    public ResponseEntity<Skill> updateSkill(@RequestBody Skill skill) throws IdInvalidException {
        Skill updateSkill = skillService.updateSkill(skill);
        if (updateSkill == null) {
            throw new IdInvalidException("Skill with id :" + skill.getId() + " no exits");
        }
        return ResponseEntity.ok(updateSkill);
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all Skill")
    public ResponseEntity<ResultPaginationDTO> getAllSkill(@Filter Specification<Skill> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.getAllSkill(spec, pageable));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") long id) throws IdInvalidException {
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id : " + id + "not exits");
        }
        this.skillService.deleteSkill(id);
        return ResponseEntity.ok().body(null);
    }

}
