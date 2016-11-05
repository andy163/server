package com.pangbohao.server.db;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class CrudBean<T> {

	public List<T> getListData(Specification<T> spec) {
		List<T> objs = new ArrayList<T>();
		Iterable<T> objsItr = getRepository().findAll();
		Iterator<T> itr = objsItr.iterator();
		while (itr.hasNext()) {
			objs.add(itr.next());
		}
		return objs;

	}

	public abstract CrudRepository<T, Integer> getRepository();

}
