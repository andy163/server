package com.pangbohao.server.db;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BasicJpaPageRepository<T> extends
		PagingAndSortingRepository<T, Long>, JpaSpecificationExecutor<T> {

}
