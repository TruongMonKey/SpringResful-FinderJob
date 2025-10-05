package com.example.JobFinder.controller;

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

import com.example.JobFinder.domain.User;
import com.example.JobFinder.domain.request.ReqLoginDTO;
import com.example.JobFinder.domain.response.ResCreateUserDTO;
import com.example.JobFinder.domain.response.ResLoginDTO;
import com.example.JobFinder.service.UserService;
import com.example.JobFinder.util.SecurityUtil;
import com.example.JobFinder.util.annotation.ApiMessage;
import com.example.JobFinder.util.errors.IdInvalidException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Value("${developer.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // ===================== LOGIN =====================
    @PostMapping("/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(), loginDto.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(loginDto.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        String refresh_token = this.securityUtil.createRefreshToken(loginDto.getUsername(), res);

        // update user token
        this.userService.updateUserToken(refresh_token, loginDto.getUsername());

        ResponseCookie resCookies = ResponseCookie.from("refresh_token", refresh_token)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    // ===================== GET ACCOUNT =====================
    @GetMapping("/auth/account")
    @ApiMessage("Fetch account information")
    public ResponseEntity<ResLoginDTO.UserLogin> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        User user = this.userService.handleGetUserByUsername(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole());

        return ResponseEntity.ok(userLogin);
    }

    // ===================== REFRESH TOKEN =====================
    @GetMapping("/auth/refresh")
    @ApiMessage("Get new Access Token using Refresh Token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refreshToken)
            throws IdInvalidException {
        if (refreshToken.equals("abc")) {
            throw new IdInvalidException("Không có refresh token trong cookie");
        }

        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refreshToken);
        String email = decodedToken.getSubject();

        // check user by token + email
        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUsername(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(new_refresh_token, email);

        ResponseCookie resCookies = ResponseCookie.from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    // ===================== LOGOUT =====================
    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logoutUser() throws IdInvalidException {
        String emailUser = SecurityUtil.getCurrentUserLogin().orElse("");
        if (emailUser.isEmpty()) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        this.userService.updateUserToken(null, emailUser);

        ResponseCookie deleteCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        // Xóa context security
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    // ===================== REGISTER =====================
    @PostMapping("/register")
    @ApiMessage("Register a new User")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + postUser.getEmail() + " đã tồn tại");
        }

        String hashPassword = this.passwordEncoder.encode(postUser.getPassWord());
        postUser.setPassWord(hashPassword);

        User newUser = this.userService.handleCreateUser(postUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertCreateUserDTO(newUser));
    }
}
