package com.neo.nbdapi.rest.vm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginVM implements Serializable {

	@NotNull
	@Size(min = 1, max = 50)
	private String username;

	@NotNull
	@Size(min = 4, max = 100)
	private String password;
}
