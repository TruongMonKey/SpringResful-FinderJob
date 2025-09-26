package com.example.JobFinder.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.JobFinder.domain.Company;
import com.example.JobFinder.domain.dto.ResultPaginationDTO;
import com.example.JobFinder.service.CompanyService;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> fetchAllCompany(
            @Filter Specification<Company> spec, Pageable pageable) {
        return ResponseEntity.ok(this.companyService.fetchAllCompany(spec, pageable));
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company postCompany) {
        Company monkeyCompany = this.companyService.handleSaveCompany(postCompany);
        return ResponseEntity.status(HttpStatus.CREATED).body(monkeyCompany);
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompanyById(@Valid @RequestBody Company updateCompany) {
        Company update = this.companyService.updateCompany(updateCompany);
        if (update != null) {
            return ResponseEntity.ok(update);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        boolean deleted = this.companyService.deleteCompany(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // HTTP 204
        } else {
            return ResponseEntity.notFound().build(); // HTTP 404
        }
    }
}
