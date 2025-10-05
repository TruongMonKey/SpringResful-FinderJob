package com.example.JobFinder.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.JobFinder.domain.Permission;
import com.example.JobFinder.domain.response.ResultPaginationDTO;
import com.example.JobFinder.service.PermissionService;
import com.example.JobFinder.util.annotation.ApiMessage;
import com.example.JobFinder.util.errors.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class PermissionController {
    public final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission")
    public ResponseEntity<Permission> create(@Valid @RequestBody Permission p) throws IdInvalidException {
        if (this.permissionService.isPermissionExist(p)) {
            throw new IdInvalidException("Permission already exists");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(p));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> update(@RequestBody Permission p) throws IdInvalidException {
        if (this.permissionService.fetchById(p.getId()) == null) {
            throw new IdInvalidException("Permission with id: " + p.getId() + " does not exist");
        }

        if (this.permissionService.isPermissionExist(p)) {
            if (this.permissionService.isSameName(p)) {
                throw new IdInvalidException("Permission already exists");
            }
        }

        return ResponseEntity.ok().body(this.permissionService.update(p));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        if (this.permissionService.fetchById(id) == null) {
            throw new IdInvalidException("Permission with id: " + id + " does not exist");
        }
        this.permissionService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch a permission")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.getPermissions(spec, pageable));
    }
}
