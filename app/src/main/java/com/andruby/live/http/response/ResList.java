package com.andruby.live.http.response;


import com.andruby.live.http.IDontObfuscate;

import java.util.List;
/**
 * @description: 列表返回数据
 *
 * @author: Andruby
 * @time: 2016/11/2 18:07
 */
public class ResList<T>  extends IDontObfuscate {

	public int currentPage;
	public int totalRow;
	public int totalPage;

	public List<T> items;

	@Override
	public String toString() {
		return "ResList{" +
				"currentPage=" + currentPage +
				", totalRow=" + totalRow +
				", totalPage=" + totalPage +
				", items=" + items +
				'}';
	}
}
