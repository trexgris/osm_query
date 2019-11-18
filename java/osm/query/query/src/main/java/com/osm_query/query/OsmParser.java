package com.osm_query.query;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.*;
import org.omg.CORBA.portable.InputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;


public class OsmParser {
	public OsmParser() {
		bus_routes = new ArrayList<BusRoute>();
		bus_route_places = new ArrayList<Place>();
		country_places = new ArrayList<Place>();
	}
	
	public void LoadCountryPlaces(Path path) {
		country_places.clear();
		try(JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"))) {
		    Gson gson = new GsonBuilder().create();
		    jsonReader.beginArray();		    
		    while (jsonReader.hasNext()){
		    	Element type = gson.fromJson(jsonReader, Element.class);
		    	if(type.GetType().contentEquals("node")) {
			        Place place = gson.fromJson(jsonReader, Place.class);
			        country_places.add(place);	
		    	}
		    }
		}
		catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	
	public void LoadCountryBusRoutes(Path path) {	
		bus_routes.clear();
		bus_route_places.clear();
		try(JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"))) {
		    Gson gson = new GsonBuilder().create();		   
		    
		    jsonReader.beginArray();		    
		    while (jsonReader.hasNext()){
		    	Element type = gson.fromJson(jsonReader, Element.class);
		    	if(type.GetType().equals("relation")) {
			        BusRoute route = gson.fromJson(jsonReader, BusRoute.class);
			        bus_routes.add(route);

		    	} else if(type.GetType().contentEquals("node")) {
			        Place place = gson.fromJson(jsonReader, Place.class);
			        bus_route_places.add(place);	
		    	}
		    }
		}
		catch (UnsupportedEncodingException e) {
		    e.printStackTrace();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		boolean tt = true;
	}
	
	ArrayList<BusRoute> GetRoutesForPlace(Place p) {
		ArrayList<BusRoute> ret = new ArrayList<BusRoute>();
		double min = 0.0;
		long id;
		double mtolerance = 10000.0;
		Place nearest_place = null;

		if(!bus_route_places.isEmpty()) {
			double lat0 = bus_route_places.get(0).GetLat();
			double lon0 = bus_route_places.get(0).GetLon();
			min = HaversineDistance(lat0, p.GetLat(), lon0, p.GetLon(),0.0,0.0);
			nearest_place = bus_route_places.get(0);
		}
		
		for(Place place : bus_route_places) {
			double dist = HaversineDistance(place.GetLat(), p.GetLat(), place.GetLon(), p.GetLon(),0.0,0.0);
			if(dist <= min) {
				min = dist;
				nearest_place = place;
			}			
		}
		
		for(BusRoute route : bus_routes) {
			List<Member> members = route.GetMembers();
			for(Member member : members) {
				if(nearest_place != null) {
					if(member.GetRef() == nearest_place.GetId()) {
						ret.add(route);
					}
				}
				
			}
			
		}
		
		return ret;
		
	}
	
	
	
	public static double HaversineDistance(double lat1, double lat2, double lon1,
	        double lon2, double el1, double el2) {

	    final int R = 6371; // Radius of the earth

	    double latDistance = Math.toRadians(lat2 - lat1);
	    double lonDistance = Math.toRadians(lon2 - lon1);
	    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
	            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
	            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    double distance = R * c * 1000; // convert to meters

	    double height = el1 - el2;

	    distance = Math.pow(distance, 2) + Math.pow(height, 2);

	    return Math.sqrt(distance);
	}
	
	public ArrayList<Place> GetRoutePlaces() {
		return bus_route_places;
	}
	
	public ArrayList<Place> GetCountryPlaces() {
		return country_places;
	}
	
	private ArrayList<BusRoute> bus_routes;
	private ArrayList<Place> bus_route_places;
	
	
	private ArrayList<Place> country_places;

}
