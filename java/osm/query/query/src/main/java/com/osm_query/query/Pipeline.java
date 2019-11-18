package com.osm_query.query;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Pipeline {
	
	public Pipeline() {
		parser = new OsmParser();
		Path nica_places = Paths.get("D:/json_data/nica_places.json");
		Path nica_routes = Paths.get("D:/json_data/nica_routes.json");

		parser.LoadCountryBusRoutes(nica_routes);
		parser.LoadCountryPlaces(nica_places);		
	}
	
	public void Test() {
		FindPath(parser.GetCountryPlaces().get(0), parser.GetCountryPlaces().get(1));
	}
	
	public void FindPath(Place id_city_A, Place id_city_B) {	
		
		ArrayList<BusRoute> routes_a = parser.GetRoutesForPlace(id_city_A);
		ArrayList<BusRoute> routes_b = parser.GetRoutesForPlace(id_city_B);
		
		if(routes_a.size() == 1) {
			boolean t = true;
		} else
		if(routes_b.size() == 1) {
			boolean t = true;
		}
		else {
			boolean t = true;
		}
		
		
	/*	Pair bus_route_id_A = FindBusRouteForPlace(Place id_city_A);
		Pair bus_route_id_B = FindBusRouteForPlace(Place id_city_A);
		if(bus_route_id_A.getFirst() == bus_route_id_B.getFirst()) {			
			boolean nice = true;
		}
		*/
	}
	
	Pair<Long, String> FindBusRouteForPlace(Place place) {
		Pair<Long, String> ret = new Pair<Long, String>((long)-1,"");
		List<BusRoute> routes = parser.GetRoutesForPlace(place);
		return ret;
	}
	
	private OsmParser parser;
	
	

}
