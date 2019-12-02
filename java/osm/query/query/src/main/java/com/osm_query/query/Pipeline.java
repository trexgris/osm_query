package com.osm_query.query;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Pipeline {
	
	public Pipeline() {
		parser = new OsmParser();
		Path nica_places = Paths.get("D:/json_data/nica_places.json");
		Path nica_routes = Paths.get("D:/json_data/nica_routes.json");

	//	parser.LoadCountryBusRoutes(nica_routes);
		try {
			parser.LoadCountryPlaces(nica_places);
			parser.LoadCountryBusRoutes(nica_routes);

		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public void Test() {
		FindPath(parser.GetCountryPlaces().get(0), parser.GetCountryPlaces().get(1));
	}
	
	public void FindPath(Place id_city_A, Place id_city_B) {	
		HashSet<Long> idsa = parser.GetBusRoutesIdForDataPlace(id_city_A);
		HashSet<Long> idsb = parser.GetBusRoutesIdForDataPlace(id_city_B);
		HashSet<Long> res = new HashSet<Long>(idsa);
		res.retainAll(idsb);
		//ArrayList<BusRoute> routes_a = parser.GetRoutesForPlace(id_city_A);
		//ArrayList<BusRoute> routes_b = parser.GetRoutesForPlace(id_city_B);
	
	}
	
	Pair<Long, String> FindBusRouteForPlace(Place place) {
		Pair<Long, String> ret = new Pair<Long, String>((long)-1,"");
		List<BusRoute> routes = parser.GetRoutesForPlace(place);
		return ret;
	}
	
	private OsmParser parser;
	
	

}
