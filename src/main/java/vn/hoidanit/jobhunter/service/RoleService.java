package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.Meta;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
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

    public Role fetchRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent()) {
            return roleOptional.get();
        }

        return null;
    }

    public Role updateRole(Role requestRole, Role currentRole) {
        // if permission exist
        if (requestRole.getPermissions() != null) {
            List<Long> listPermissionId = requestRole.getPermissions()
                    .stream().map(item -> item.getId())
                    .collect(Collectors.toList());

            List<Permission> listPermissions = this.permissionRepository.findByIdIn(listPermissionId);
            
            // set permission
            currentRole.setPermissions(listPermissions);
        }

        currentRole.setName(requestRole.getName());
        currentRole.setDescription(requestRole.getDescription());
        currentRole.setActive(requestRole.isActive());

        currentRole = this.roleRepository.save(currentRole);

        return currentRole;
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageRole.getTotalPages());
        meta.setTotal(pageRole.getTotalElements());

        result.setMeta(meta);
        result.setResult(pageRole.getContent());

        return result;
    }
}
