package com.yj.peuteu.api.user.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
	public String email;
	public String accessToken;
	public String refreshToken;
}
