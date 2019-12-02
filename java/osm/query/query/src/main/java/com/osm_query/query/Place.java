package com.osm_query.query;

public class Place implements Element {
	private long id;
	private double lat;
	private double lon;
	private PlaceTags tags;
	private String type;

	
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
