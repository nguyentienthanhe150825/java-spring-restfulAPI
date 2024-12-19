package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role handleCreateRole(Role requestRole) {
        // if permission exist
        if (requestRole.getPermissions() != null) {
            List<Long> listPermissionId = requestRole.getPermissions()
                    .stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            
            List<Permission> listPermissions = this.permissionRepository.findByIdIn(listPermissionId);
            
            // RequestRole chỉ có thông tin của Id Permission và sau khi tìm
            // Set tất cả Attribute của Permission vào RequestRole
            requestRole.setPermissions(listPermissions);
        }
        return this.roleRepository.save(requestRole);
    }
}
