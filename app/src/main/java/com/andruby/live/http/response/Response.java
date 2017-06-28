package com.andruby.live.http.response;


import com.andruby.live.http.IDontObfuscate;
/**
 * @description: 基础返回数据
 *
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class Response<T>  extends IDontObfuscate {

	public int status;
	public String msg;
	public T data;
	@Override
	public String toString() {
		return "Response [code=" + status + ", msg=" + msg + ", data=" + data
				+ "]";
	}

}

