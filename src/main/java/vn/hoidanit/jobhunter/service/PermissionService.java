package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean isPermissionExist(Permission permission) {
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(),
                permission.getApiPath(), permission.getMethod());
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }

    public Permission fetchPermissionById(long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if (permissionOptional.isPresent()) {
            return permissionOptional.get();
        }
        return null;
    }

    public Permission handleUpdatePermission(Permission requestPermission, Permission currentPermission) {
        // set name, apiPath, method, module
        currentPermission.setName(requestPermission.getName());
        currentPermission.setApiPath(requestPermission.getApiPath());
        currentPermission.setMethod(requestPermission.getMethod());
        currentPermission.setModule(requestPermission.getModule());    

        // update
        currentPermission = this.permissionRepository.save(currentPermission);
        return currentPermission;
    }
}
