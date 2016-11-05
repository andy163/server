package com.pangbohao.server.db.repo;

import com.pangbohao.server.db.model.AuthUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserRepository extends CrudRepository<AuthUser, Integer> {

	@Query("select a from AuthUser a where a.username=?1")
	public AuthUser findByUser(String username);
	
	@Query("select a from AuthUser a where a.id=?1")
	public AuthUser findById(int id);
}
