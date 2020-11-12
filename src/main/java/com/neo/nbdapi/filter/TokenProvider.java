package com.neo.nbdapi.filter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neo.nbdapi.dao.UserInfoDAO;
import com.neo.nbdapi.entity.UserInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@Component
public class TokenProvider {

    private Logger logger = LogManager.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private Key key;

    private long tokenValidityInMilliseconds;

    private String secret = "124f093edb90d9bd3c3bdf846a9069d654073b44473bae0794465923e593d9d8bb39278fa487681404e71e7cf45c45cd398d9f351055c22a95448b8d13706ce7";

    @Autowired
    private Enforcer enforcer;

    @Autowired
    UserInfoDAO userInfoDAO;

    @Autowired
    ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = 8640000;
    }

    /**
     * function generate token from username
     * @param authentication
     * @return
     */
    public String createToken(Authentication authentication) throws SQLException, JsonProcessingException {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        validity = new Date(now + this.tokenValidityInMilliseconds);
        UserInfo userInfo = userInfoDAO.findUserInfo(authentication.getName());
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("userBO",objectMapper.writeValueAsString(userInfo))
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * function get information from token
     * @param token
     * @param path
     * @param method
     * @return
     */
    public Authentication getAuthentication(String token, String path, String method) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        User principal = new User(claims.getSubject(), "", authorities);

        // log test
        logger.debug("username: {}", claims.getSubject());

        if (claims.getSubject() != null && !enforcer.enforce(claims.getSubject(), path, method)) { // thang sua o day
            return new UsernamePasswordAuthenticationToken(null, token, authorities);
        }

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * function validate token
     * @param authToken
     * @return
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.info("Invalid JWT token.");
            logger.trace("Invalid JWT token trace.", e);
        }
        return false;
    }
}
