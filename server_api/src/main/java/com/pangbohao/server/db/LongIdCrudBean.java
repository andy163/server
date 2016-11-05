package com.pangbohao.server.db;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class LongIdCrudBean<T> {

	//private static final Logger logger = Logger.getLogger(CrudBean.class);

	public List<T> getListData(Specification<T> spec) {
		List<T> objs = new ArrayList<T>();
		Iterable<T> objsItr = getRepository().findAll();
		Iterator<T> itr = objsItr.iterator();
		while (itr.hasNext()) {
			objs.add(itr.next());
		}
		return objs;

	}

	public abstract CrudRepository<T, Long> getRepository();

}
