package com.pangbohao.server.db;

import com.pangbohao.server.rest.model.GetListResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PagerBean<T> {

	public GetListResult<T> getListData(int pageNumber, int pageSize) {
		return getListData(pageNumber, pageSize, "",
				new HashMap<String, Object>());
	}

	public GetListResult<T> getListData(int pageNumber, int pageSize,
			String sortType, Map<String, Object> searchParams) {
		Specification<T> spec = buildSpecification(searchParams);
		return getListData(pageNumber, pageSize, sortType, spec);

	}

	public GetListResult<T> getListData(int pageNumber, int pageSize,
			String sortType, Specification<T> spec) {
		PageRequest pageRequest = buildPageRequest(pageNumber, pageSize,
				sortType);

		Page<T> pg = getRepository().findAll(spec, pageRequest);

		GetListResult<T> rtn = new GetListResult<T>();

		rtn.setTotalpage(pg.getTotalPages());
		rtn.setHasNext(pg.hasNext());
		rtn.setCount((int) pg.getNumberOfElements());// 当前页条数
		rtn.setTotal((int) pg.getTotalElements());// 总条数
		int currentPage = (int) Math.min(pageNumber, pg.getTotalPages());
		rtn.setCurrPage(currentPage);
		rtn.setData(pg.getContent());
		return rtn;

	}

	public List<T> getListData(Map<String, Object> searchParams) {
		Specification<T> spec = buildSpecification(searchParams);
		return getListData(spec);

	}

	public List<T> getListData(Specification<T> spec) {
		return getRepository().findAll(spec);

	}

	/**
	 * 创建动态查询条件组合.searchParams 为 “与”
	 */
	public Specification<T> buildSpecification(Map<String, Object> searchParams) {
		if (searchParams == null) {
			return null;
		}
		return new JLSpecification(searchParams);
	}

	private class JLSpecification implements Specification<T> {
		private Map<String, Object> searchParams;

		public JLSpecification(Map<String, Object> searchParams) {
			this.searchParams = searchParams;
		}

		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
				CriteriaBuilder cb) {

			Predicate[] predicates = new Predicate[searchParams.size()];
			int i = 0;
			for (String key : searchParams.keySet()) {
				Path<String> path = root.get(key);
				predicates[i] = cb.equal(path, searchParams.get(key));
				i++;
			}

			return cb.and(predicates);
		}
	}

	public abstract BasicJpaPageRepository<T> getRepository();

	/**
	 * 创建分页请求.
	 */
	protected PageRequest buildPageRequest(int pageNumber, int pageSize,
			String sortType) {
		if (pageNumber < 1) {
			pageNumber = 1;
		}
		if (pageSize < 1) {
			pageSize = 50;
		}

		Sort sort = null;
		if ("desc".equals(sortType)) {
			sort = new Sort(Direction.DESC, "id");
		} else if ("name".equals(sortType)) {
			sort = new Sort(Direction.ASC, "name");
		}

		return new PageRequest(pageNumber - 1, pageSize, sort);
	}
}
