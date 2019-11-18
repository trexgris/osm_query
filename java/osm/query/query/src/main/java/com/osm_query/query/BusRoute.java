package com.osm_query.query;

import java.util.List;

public class BusRoute {
	
	private String type;
	private long id;
	private List<Member> members;
	private Tags tags;
	
	public List<Member> GetMembers() {
		return members;
	};
	
	public long GetId() {
		return id;
	}
}
