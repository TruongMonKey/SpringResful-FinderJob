package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.Company;
import com.example.JobFinder.domain.User;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.repository.CompanyRepository;
import com.example.JobFinder.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

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

    public void handleDeleteCompany(Long id) {
        Optional<Company> companyOptional = this.companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company com = companyOptional.get();
            List<User> users = this.userRepository.findByCompany(com);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }

    public Optional<Company> findById(Long id) {
        return this.companyRepository.findById(id);
    }

}
