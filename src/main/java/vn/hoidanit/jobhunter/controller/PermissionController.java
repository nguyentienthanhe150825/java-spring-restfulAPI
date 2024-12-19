package vn.hoidanit.jobhunter.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a permission") 
    public ResponseEntity<Permission> createNewPermission(@Valid @RequestBody Permission requestPermission) throws IdInvalidException {
        // check module, apiPath, method exist
        boolean isPermissionExist = this.permissionService.isPermissionExist(requestPermission);
        if (isPermissionExist) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        Permission createPermission = this.permissionService.handleCreatePermission(requestPermission);
        return ResponseEntity.status(HttpStatus.CREATED).body(createPermission);
    } 

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission requestPermission) throws IdInvalidException {
        // check id exist
        Permission currentPermission = this.permissionService.fetchPermissionById(requestPermission.getId());
        if (currentPermission == null) {
            throw new IdInvalidException("Permission với id = " + requestPermission.getId() + " không tồn tại");
        }

        // check module, apiPath, method exist
        boolean isPermissionExist = this.permissionService.isPermissionExist(requestPermission);
        if (isPermissionExist) {
            throw new IdInvalidException("Permission đã tồn tại");
        }

        Permission updatePermission = this.permissionService.handleUpdatePermission(requestPermission, currentPermission);

        return ResponseEntity.status(HttpStatus.OK).body(updatePermission);
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch permissions with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(@Filter Specification<Permission> spec, Pageable pageable) {
        ResultPaginationDTO listPermissions = this.permissionService.fetchAllPermissions(spec, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(listPermissions);
    }
    
    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") long id) throws IdInvalidException {
        // check id exist
        Permission currentPermission = this.permissionService.fetchPermissionById(id);
        if (currentPermission == null) {
            throw new IdInvalidException("Permission với id = " + id + " không tồn tại");
        }
        this.permissionService.deletePermission(id);
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
