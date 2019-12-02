package com.osm_query.query;

import java.util.List;

public class Way implements Element {
	private long id;
	private String type;
	List<Long> nodes;
	Tags tags;
	
	public long GetId() {
		return id;
	}
	public List<Long> GetNodes() {
		return nodes;
	}
}
