package com.pangbohao.server.rest.model;

public class PostResult<T> extends Result<T> {

	private static final long serialVersionUID = 1L;

	public static final int SUCC = 0;
	public static final int WRONG_PASS = 1;
	public static final int FAILED = 1;

	public PostResult() {
		super();
	}

	public PostResult(int status, String msg) {
		super(status, msg);
	}

	public PostResult(int status, String msg, T data) {
		super(status, msg, data);
	}

}
