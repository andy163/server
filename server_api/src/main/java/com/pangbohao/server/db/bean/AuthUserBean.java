package com.pangbohao.server.db.bean;

import com.pangbohao.server.db.CrudBean;
import com.pangbohao.server.db.model.AuthUser;
import com.pangbohao.server.db.repo.AuthUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthUserBean extends CrudBean<AuthUser> {

	@Autowired
	private AuthUserRepository repository;

	public AuthUserBean() {

	}

	public AuthUserRepository getRepository() {
		return repository;
	}

}
