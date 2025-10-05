package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.Permission;
import com.example.JobFinder.domain.Role;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.repository.PermissionRepository;
import com.example.JobFinder.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean isRoleExist(String name) {
        return roleRepository.existsByName(name);
    }

    public Role fetchById(Long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        return roleOptional.orElse(null);
    }

    public Role create(Role r) {

        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions().stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            r.setPermissions(dbPermissions);
        }

        return this.roleRepository.save(r);
    }

    public Role update(Role r) {
        Role roleDB = this.fetchById(r.getId());
        if (roleDB == null) {
            return null;
        }

        if (r.getPermissions() != null) {
            List<Long> reqPermissions = r.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> dbPermissions = this.permissionRepository.findByIdIn(reqPermissions);
            roleDB.setPermissions(dbPermissions);
        }

        roleDB.setName(r.getName());
        roleDB.setDescription(r.getDescription());
        roleDB.setActive(r.isActive());

        return this.roleRepository.save(roleDB);
    }

    public void delete(long id) {
        this.roleRepository.deleteById(id);
    }

    public ResultPaginationDTO getRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRoles = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageRoles.getTotalPages());
        mt.setTotal(pageRoles.getTotalElements());
        rs.setMeta(mt);

        rs.setResult(pageRoles.getContent());
        return rs;
    }

}
