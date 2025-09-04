package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.Company;
import com.example.JobFinder.domain.User;
import com.example.JobFinder.domain.dto.Meta;
import com.example.JobFinder.domain.dto.ResultPaginationDTO;
import com.example.JobFinder.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompany(Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta mt = new Meta();

        mt.setPage(pageCompany.getNumber());
        mt.setPageSize(pageCompany.getSize());
        mt.setPages(pageCompany.getTotalPages());
        mt.setTotal(pageCompany.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(pageCompany.getContent());
        return rs;
    }

    public Company updateCompany(Company updateCompany) {
        Optional<Company> optionalCompany = this.companyRepository.findById(updateCompany.getId());
        if (optionalCompany.isPresent()) {
            Company exitsCompany = optionalCompany.get();
            exitsCompany.setName(updateCompany.getName());
            exitsCompany.setDescription(updateCompany.getDescription());
            exitsCompany.setAddress(updateCompany.getAddress());
            exitsCompany.setLogo(updateCompany.getLogo());
            return this.companyRepository.save(exitsCompany);
        }
        return null;
    }

    public boolean deleteCompany(Long id) {
        Optional<Company> company = companyRepository.findById(id);
        if (company.isPresent()) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
