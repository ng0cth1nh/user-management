package com.ng0cth1nh.management.security;

import com.ng0cth1nh.management.repository.UserRepository;
import com.ng0cth1nh.management.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.security.core.userdetails.UserDetails;

@Component("utilSecurity")
public class UtilSecurity {

    @Autowired
    private UserRepository userRepository;


    public boolean hasUserId(Authentication authentication, Integer userId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user  = userRepository.findUserByUsername(userDetails.getUsername()).get();
        return user.getId() == userId;
    }

    public boolean isUserInCompany(Authentication authentication, Integer companyId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user  = userRepository.findUserByUsername(userDetails.getUsername()).get();
        return user.getCompanyId() == companyId;
    }


    public boolean isAdminInCompany(Authentication authentication, Integer userId) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User userAdmin  = userRepository.findUserByUsername(userDetails.getUsername()).get();
        User user = userRepository.findById(userId).orElse(null);
        return user.getCompanyId() == userAdmin.getCompanyId();
    }
}
