package com.osm_query.query;

public class Place {
	private String type;
	private long id;
	private double lat;
	private double lon;
	private PlaceTags tags;
	
	public double GetLon() {
		return lon;
	}
	
	public double GetLat() {
		return lat;
	}
	
	public long GetId() {
		return id;
	}
}
