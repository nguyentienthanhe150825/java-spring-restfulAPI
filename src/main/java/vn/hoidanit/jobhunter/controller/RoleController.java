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
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
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

    @GetMapping("/roles")
    @ApiMessage("Get role with pagination")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(@Filter Specification<Role> spec, Pageable pageable) {
        ResultPaginationDTO listRoles = this.roleService.fetchAllRoles(spec, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(listRoles);
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        // check id exist
        Role currentRole = this.roleService.fetchRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.deleteRole(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> fetchRoleById(@PathVariable("id") long id) throws IdInvalidException {
        // check role id exist
        Role currentRole = this.roleService.fetchRoleById(id);
        if (currentRole == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(currentRole);
    }

}
