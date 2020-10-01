package com.neo.nbdapi.services;

import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.entity.UserInfo;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserInfoDAO userInfoDAO;

    public DomainUserDetailsService(UserInfoDAO userInfoDAO) {
        this.userInfoDAO = userInfoDAO;
    }

    @SneakyThrows
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String username) {
        UserInfo userInfo = userInfoDAO.findUserInfoByUsername(username);
        if (userInfo == null)
            throw new UsernameNotFoundException("User " + username + " was not found in the database");
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();
        return new org.springframework.security.core.userdetails.User(userInfo.getId(),
                userInfo.getPassword(), simpleGrantedAuthorities);

    }
}
