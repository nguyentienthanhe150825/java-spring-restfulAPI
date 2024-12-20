package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.response.Meta;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUpdateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginationDTO;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    // Dependency Injection
    private final UserRepository userRepository;
    private final CompanyService companyService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompanyService companyService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companyService = companyService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        // check company
        if (user.getCompany() != null) {
            Company company = this.companyService.fetchCompanyById(user.getCompany().getId());
            if (company != null) {
                user.setCompany(company);
            } else {
                user.setCompany(null);
            }
        }

        // check role
        if (user.getRole() != null) {
            Role role = this.roleService.fetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }

        return this.userRepository.save(user);
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    // public List<User> fetchAllUsers(Pageable pageable) {
    //     Page<User> pageUser = this.userRepository.findAll(pageable);
    //     // Convert Page int List
    //     List<User> listUsers = pageUser.getContent();
    //     return listUsers;
    // }

    public ResultPaginationDTO fetchAllUsers(Specification<User> specification, Pageable pag) {
        Page<User> pUser = this.userRepository.findAll(specification, pag);
        ResultPaginationDTO result = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pag.getPageNumber() + 1);
        meta.setPageSize(pag.getPageSize());

        meta.setPages(pUser.getTotalPages());
        meta.setTotal(pUser.getTotalElements());

        result.setMeta(meta);

        // Refactoring code
        List<ResUserDTO> listUser = pUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());

        result.setResult(listUser);
        return result;
    }

    public User handleUpdateUser(User requestUser) {
        User currentUser = this.fetchUserById(requestUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(requestUser.getAddress());
            currentUser.setName(requestUser.getName());
            currentUser.setGender(requestUser.getGender());
            currentUser.setAge(requestUser.getAge());

            // check company: If user change company
            if (requestUser.getCompany() != null) {
                Company companyOptional = this.companyService.fetchCompanyById(requestUser.getCompany().getId());
                currentUser.setCompany(companyOptional != null ? companyOptional : null);
            } 

            // check role exist
            if (requestUser.getRole() != null) {
                Role role = this.roleService.fetchRoleById(requestUser.getRole().getId());
                currentUser.setRole(role != null ? role : null);
            }

            // update
            currentUser = this.userRepository.save(currentUser);
        }
        return currentUser;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }

    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();
        ResCreateUserDTO.CompanyUser company = new ResCreateUserDTO.CompanyUser();
        ResCreateUserDTO.RoleUser role = new ResCreateUserDTO.RoleUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }

        return res;
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.CompanyUser company = new ResUserDTO.CompanyUser();
        ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();

        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getCompany() != null) {
            company.setId(user.getCompany().getId());
            company.setName(user.getCompany().getName());
            res.setCompany(company);
        }

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }

        return res;
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        
        ResUpdateUserDTO.CompanyUser com = new ResUpdateUserDTO.CompanyUser();
        if (user.getCompany() != null) {
            com.setId(user.getCompany().getId());
            com.setName(user.getCompany().getName());

            res.setCompany(com);
        }

        ResUpdateUserDTO.RoleUser role = new ResUpdateUserDTO.RoleUser();
        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());

            res.setRole(role);
        }

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        return res;
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }
}
