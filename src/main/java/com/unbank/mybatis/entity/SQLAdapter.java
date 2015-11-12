package com.unbank.mybatis.entity;

import java.util.List;
import java.util.Map;

public class SQLAdapter {

	private Integer prikey;
	private String sql;
	private Map<String, Object> obj;
	private List<Integer> list;

	public SQLAdapter(Map<String, Object> objects) {
		this.obj = objects;
	}

	public SQLAdapter(String sql) {
		this.sql = sql;
	}

	public SQLAdapter(String sql, Map<String, Object> objects) {
		this.sql = sql;
		this.obj = objects;
	}

	public SQLAdapter(List<Integer> list) {
		this.list = list;
	}

	public SQLAdapter() {
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Map<String, Object> getObj() {
		return obj;
	}

	public void setObj(Map<String, Object> obj) {
		this.obj = obj;
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	public Integer getPrikey() {
		return prikey;
	}

	public void setPrikey(Integer prikey) {
		this.prikey = prikey;
	}


	

}
