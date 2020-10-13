package com.neo.nbdapi.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.neo.nbdapi.dto.UserAndMenuDTO;
import com.neo.nbdapi.filter.JWTFilter;
import com.neo.nbdapi.filter.TokenProvider;
import com.neo.nbdapi.rest.vm.LoginVM;
import com.neo.nbdapi.services.UserInfoService;
import com.neo.nbdapi.utils.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.SQLException;

/**
 * @author thanglv on 10/9/2020
 * @project NBD
 */
@RestController
@RequestMapping(Constants.APPLICATION_API.API_PREFIX + Constants.APPLICATION_API.MODULE.URI_LOGIN)
public class UserJWTController {

    private Logger logger = LogManager.getLogger(UserJWTController.class);

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserInfoService userInfoService;

	@Autowired
	PasswordEncoder encoder;

	public UserJWTController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
	}

    /**
     * API authenticate
     * @param loginVM
     * @return
     * @throws SQLException
     */
    @PostMapping
    public ResponseEntity<UserAndMenuDTO> authorize(@Valid @RequestBody LoginVM loginVM) throws SQLException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

		String encodepass = encoder.encode(loginVM.getPassword());
		UserAndMenuDTO userAndMenuDTO = userInfoService.getUserInfoAndListMenu();
		userAndMenuDTO.setPassword(encodepass);
		return new ResponseEntity<>(userAndMenuDTO,
				httpHeaders, HttpStatus.OK);
	}

	/**
	 * Object to return as body in JWT Authentication.
	 */
	static class JWTToken {

        private String idToken;

        JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
