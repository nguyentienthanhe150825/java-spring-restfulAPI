package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
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
    
    
}
