package vn.hoidanit.jobhunter.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.domain.request.ReqLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO.UserGetAccount;
import vn.hoidanit.jobhunter.domain.response.ResLoginDTO.UserLogin;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

    // Tham khảo tại link github dự án: jhipster
    // https://github.com/hoidanit-be-java-spring-rest/02-java-jhipster-with-filter/blob/master/src/main/java/com/mycompany/myapp/web/rest/AuthenticateController.java?ref_type=heads#L54
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Value("${hoidanit.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        // Nạp input gồm username/password vào Security
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        // xác thực người dùng => cần viết hàm loadUserByUsername
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // Set information to SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Format response access_token
        ResLoginDTO res = new ResLoginDTO();

        // Query database để lấy thông tin user sau đó gán vào UserLogin
        User currentUserDB = this.userService.handleGetUserByUsername(loginDTO.getUsername());
        if (currentUserDB != null) {
            // Set Data into Inner class: UserLogin
            UserLogin resUserLogin = res.new UserLogin();

            resUserLogin.setId(currentUserDB.getId());
            resUserLogin.setEmail(currentUserDB.getEmail());
            resUserLogin.setName(currentUserDB.getName());
            resUserLogin.setRole(currentUserDB.getRole());

            res.setUser(resUserLogin);
        }

        // create access_token
        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);

        res.setAccessToken(access_token);

        // create refresh_token
        String refresh_token = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);

        // Save refresh_token to database
        this.userService.updateUserToken(refresh_token, loginDTO.getUsername());

        // Create cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("Get user information")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        ResLoginDTO res = new ResLoginDTO();
        // Constructor Inner Class
        UserLogin userLogin = res.new UserLogin();

        UserGetAccount userGetAccount = res.new UserGetAccount();

        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            // Set Data into Inner class: UserLogin
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());

            // Set userLogin into UserGetAccount
            userGetAccount.setUser(userLogin);
        }

        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get user by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(@CookieValue(name = "refresh_token") String refresh_token)
            throws IdInvalidException {
        // Check token valid
        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        // Lưu ý: Double Check: Check user by refresh_token and email => increase
        // security
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token invalid");
        }

        // issue new token/set refresh token as cookies

        // Format response access_token
        ResLoginDTO res = new ResLoginDTO();

        // Query database để lấy thông tin user sau đó gán vào UserLogin
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            // Set Data into Inner class: UserLogin
            UserLogin userLogin = res.new UserLogin();

            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUser.getRole());

            res.setUser(userLogin);
        }

        // create new_access_token
        String new_access_token = this.securityUtil.createAccessToken(email, res);

        res.setAccessToken(new_access_token);

        // create new_refresh_token
        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);

        // Save new_refresh_token to database
        this.userService.updateUserToken(new_refresh_token, email);

        // Create new cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.equals("")) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        // update refresh token = null in database
        this.userService.updateUserToken(null, email);

        // remove refresh token cookies
        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)   // Cookies will immediately expire
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString()).body(null);
    }


    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User requestUser) throws IdInvalidException {
        // check email exist in database
        boolean isEmailExist = this.userService.isEmailExist(requestUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + requestUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        }
        String hashPassword = this.passwordEncoder.encode(requestUser.getPassword());
        requestUser.setPassword(hashPassword);

        User newUser = this.userService.handleCreateUser(requestUser);
        ResCreateUserDTO resCreateUserDTO = this.userService.convertToResCreateUserDTO(newUser);

        return ResponseEntity.status(HttpStatus.OK).body(resCreateUserDTO);
    }

}

// Create Cookies: https://reflectoring.io/spring-boot-cookies/
// Document: https://developer.mozilla.org/en-US/docs/Web/HTTP/Cookies
// secure(true): cookies chỉ được sử dụng với https (thay vì http)
// Khi sử dụng localhost: set secure = true hay false đều ko có tác dụng
