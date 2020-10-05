package com.neo.nbdapi.exception;

import java.io.Serializable;

public class ResponseBasicObj implements Serializable {
	private int code;

	private String message;

	private Object data;

	public ResponseBasicObj() {
	}

	public ResponseBasicObj(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public ResponseBasicObj(int code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
