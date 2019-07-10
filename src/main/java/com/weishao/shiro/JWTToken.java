package com.weishao.shiro;

import org.apache.shiro.authc.AuthenticationToken;

public class JWTToken implements AuthenticationToken {

	private static final long serialVersionUID = 8710830324536266430L;
	
	// 密钥
	private String token;

	public JWTToken(String token) {
		this.token = token;
	}

	@Override
	public Object getPrincipal() {
		return token;
	}

	@Override
	public Object getCredentials() {
		return token;
	}
}
