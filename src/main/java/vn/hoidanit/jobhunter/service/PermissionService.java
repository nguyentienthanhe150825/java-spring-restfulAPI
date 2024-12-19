package vn.hoidanit.jobhunter.service;

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
        return this.permissionRepository.existsByModuleAndApiPathAndMethod(permission.getModule(), permission.getApiPath(), permission.getMethod());
    }

    public Permission handleCreatePermission(Permission permission) {
        return this.permissionRepository.save(permission);
    }
}
