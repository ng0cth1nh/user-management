package com.ng0cth1nh.management.controller;


import com.ng0cth1nh.management.exceptionHandling.RecordNotFoundException;
import com.ng0cth1nh.management.security.JwtUtil;
import com.ng0cth1nh.management.model.*;
import com.ng0cth1nh.management.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping(path = "/api/v1/")
public class UserController {

    @Autowired
    private UserService userService;


    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("user/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        String result = "";
        HttpStatus httpStatus = null;
        User userPrincipal = userService.findByUsername(user.getUsername());
        if (null == user
                || userPrincipal == null
                || !new BCryptPasswordEncoder().matches(user.getPassword(),
                userPrincipal.getPassword())
        ) {
            result = "Wrong userId and password";
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            result = jwtUtil.generateToken(user.getUsername());
            httpStatus = HttpStatus.OK;
        }
        return new ResponseEntity<>(result, httpStatus);
    }

    @PostMapping("user/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setActive(true);
        Set<Role> roles = new HashSet<>();
        roles.add(new Role(2));
        user.setRoles(roles);
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.OK);
    }

    @GetMapping("user/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN') or @utilSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<Object> getUser(@PathVariable Integer id, Authentication authentication) {
        User user = null;

            user = userService.findById(id).get();

        return new ResponseEntity<Object>(user, HttpStatus.OK);
    }

    @PutMapping("user/update/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN') or @utilSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<Object> updateUser(@PathVariable Integer id,
                                             @RequestParam(required = false) String password,
                                             @RequestParam(required = false) String username,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) Integer companyId,
                                             @RequestParam(required = false) Boolean active
            , Authentication authentication) {
        User user = null;
        try {
            user = userService.updateUser(id, username, name, password, companyId, active);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Object>(user, HttpStatus.OK);
    }


    @DeleteMapping("user/delete/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN') or @utilSecurity.hasUserId(authentication,#id)")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id, Authentication authentication) {
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            return new ResponseEntity<Object>(e, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Object>("Deleted!", HttpStatus.OK);
    }


    @GetMapping("users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Object> getUsers() {
        List<User> users = userService.getUsers();
        if (users != null) {
            return new ResponseEntity<Object>(users, HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Not Found User", HttpStatus.NOT_FOUND);
    }

}