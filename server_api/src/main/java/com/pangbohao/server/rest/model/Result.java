package com.pangbohao.server.rest.model;

import java.io.Serializable;

public class Result<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final int SUCC = 0;
	public static final int FAILED = 1;

	private int statusCode = 0;
	private String msg;
	private T data;

	public Result() {

	}

	public Result(int statusCode, String msg) {
		this.statusCode = statusCode;
		this.msg = msg;
	}

	public Result(int statusCode, String msg, T data) {
		this.statusCode = statusCode;
		this.msg = msg;
		this.data = data;
	}

	public static Result<Object> SuccResult() {
		return new Result<Object>(SUCC, "成功");
	}

	public static Result<Object> SuccResult(String msg) {
		return new Result<Object>(SUCC, msg);
	}

	public static Result<Object> FailedResult() {
		return new Result<Object>(FAILED, "失败");
	}

	public static Result<Object> FailedResult(String msg) {
		return new Result<Object>(FAILED, msg);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
