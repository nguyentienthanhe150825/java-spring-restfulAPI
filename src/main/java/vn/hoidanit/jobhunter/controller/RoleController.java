package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> createNewRole(@Valid @RequestBody Role requestRole) throws IdInvalidException {
        // check name exist
        boolean isExistName = this.roleService.existByName(requestRole.getName());
        if (isExistName) {
            throw new IdInvalidException("Role với name = " + requestRole.getName() + " đã tồn tại");
        }
        Role createRole = this.roleService.handleCreateRole(requestRole);

        return ResponseEntity.status(HttpStatus.CREATED).body(createRole);
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role requestRole) throws IdInvalidException {
        // check id exist
        Role currentRole = this.roleService.fetchRoleById(requestRole.getId());
        if (currentRole == null) {
            throw new IdInvalidException("Role với id = " + requestRole.getId() + " không tồn tại");
        }

        // check if the name is being updated
        if (!currentRole.getName().equals(requestRole.getName())) {
            // check name exist
            boolean isExistName = this.roleService.existByName(requestRole.getName());
            if (isExistName) {
                throw new IdInvalidException("Role với name = " + requestRole.getName() + " đã tồn tại");
            }
        }
        Role updateRole = this.roleService.updateRole(requestRole, currentRole);
        return ResponseEntity.status(HttpStatus.OK).body(updateRole);
    }
}
