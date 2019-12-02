package com.osm_query.query;

import java.util.*;

import java.io.*;
import java.nio.file.Path;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;

public class OsmParser
{

    public OsmParser()
    {
        // bus_routes = new ArrayList<BusRoute>();
        bus_route_places = new ArrayList<Place>();
        bus_route_ways = new ArrayList<Way>();
        country_places = new ArrayList<Place>();
        builder = new GsonBuilder();
        br_map = new HashMap<Long, BusRoute>();
        builder.registerTypeAdapter(Element.class, new ElementDeserializer());

    }

    /* private BusRoute GetRouteForId(long id)
    {
        for (BusRoute route : bus_routes)
        {
            if (route.GetId() == id)
            {
                return route;
            }
        }
        return null;
    }*/

    public void GetFullRoute(Place A, Place B)
    {
        HashSet<Long> routes_ids_A = GetBusRoutesIdForDataPlace(A);
        HashSet<Long> routes_ids_B = GetBusRoutesIdForDataPlace(B);

        HashSet<Long> routes_to_skip = new HashSet<Long>();

        routes_to_skip.addAll(routes_ids_A);

        for (Long route_id : routes_ids_A)
        {
            BusRoute current_route = br_map.get(route_id);//GetRouteForId(route_id);
            //HERE
            for (BusRoute route_to_check : bus_routes)
            {

            }
        }

    }

    public void LoadCountryPlaces(Path path) throws UnsupportedEncodingException, FileNotFoundException
    {
        country_places.clear();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"));
        Gson gson = builder.create();
        Element[] result = gson.fromJson(jsonReader, Element[].class);

        for (Object element : result)
        {
            if (element instanceof Place)
            {
                country_places.add((Place) element);
            }
        }
    }

    public void LoadCountryBusRoutes(Path path) throws UnsupportedEncodingException, FileNotFoundException
    {
        // bus_routes.clear();
        bus_route_places.clear();
        bus_route_ways.clear();
        br_map.clear();

        JsonReader jsonReader = new JsonReader(new InputStreamReader(new FileInputStream(path.toString()), "UTF-8"));
        Gson gson = builder.create();
        Element[] result = gson.fromJson(jsonReader, Element[].class);

        for (Object element : result)
        {
            if (element instanceof Place)
            {
                bus_route_places.add((Place) element);
            }
            else if (element instanceof BusRoute)
            {
                BusRoute b = (BusRoute) element;
                //  bus_routes.add(b);
                br_map.put(b.GetId(), b);

            }
            else if (element instanceof Way)
            {
                bus_route_ways.add((Way) element);
            }
        }
    }

    private HashSet<Long> UnwrapBusRoute(BusRoute R)
    {
        HashSet<Long> ret = new HashSet<Long>();
        List<Member> ms = R.GetMembers();
        for (Member m : ms)
        {
            if (m.GetType().equals("node"))
            {
                ret.add(m.GetRef());
            }
            else if (m.GetType().equals("way"))
            {
                for (Way w : bus_route_ways)
                {
                    if (w.GetId() == m.GetRef())
                    {
                        ret.addAll(w.GetNodes());
                    }
                }
            }
        }
        return ret;
    }

    public HashSet<Long> GetNodesInCommon(BusRoute A, BusRoute B)
    {

        HashSet<Long> uA = UnwrapBusRoute(A);
        HashSet<Long> uB = UnwrapBusRoute(A);
        HashSet<Long> ret = new HashSet<Long>(uA);
        ret.retainAll(uB);
        return ret;
    }

    private HashSet<Long> GetPlacesInRadius(Place p, double radius)
    {
        HashSet<Long> ret = new HashSet<Long>();
        double plat = p.GetLat();
        double plon = p.GetLon();
        for (Place pp : bus_route_places)
        {
            double hdist = HaversineDistance(plat, pp.GetLat(), plon, pp.GetLon(), 0.0, 0.0);
            if (hdist <= radius)
            {
                ret.add(pp.GetId());
            }
        }
        return ret;
    }

    public HashSet<Long> GetBusRoutesIdForDataPlace(Place p)
    {

        //get country places in a radius 	

        HashSet<Long> places_rad = GetPlacesInRadius(p, 10000.0);

        // either a city or village or whatever
        HashSet<Long> ret = new HashSet<Long>();
        // go through all the bus routes of the country
        for (BusRoute R : br_map.values())
        {
            HashSet<Long> set = UnwrapBusRoute(R);
            //unwrap to get nodes only
            for (Long radius_place : places_rad)
            { // places in radius of Place p
                if (set.contains(radius_place))
                {
                    ret.add(R.GetId());
                }
            }

        }
        return ret;
    }

    ArrayList<BusRoute> GetRoutesForPlace(Place p)
    {
        ArrayList<BusRoute> ret = new ArrayList<BusRoute>();
        double min = 0.0;
        long id;
        double mtolerance = 10000.0;
        Place nearest_place = null;

        if (!bus_route_places.isEmpty())
        {
            double lat0 = bus_route_places.get(0).GetLat();
            double lon0 = bus_route_places.get(0).GetLon();
            min = HaversineDistance(lat0, p.GetLat(), lon0, p.GetLon(), 0.0, 0.0);
            nearest_place = bus_route_places.get(0);
        }

        for (Place place : bus_route_places)
        {
            double dist = HaversineDistance(place.GetLat(), p.GetLat(), place.GetLon(), p.GetLon(), 0.0, 0.0);
            if (dist <= min)
            {
                min = dist;
                nearest_place = place;
            }
        }

        long way_id = -1;
        boolean oops = false;
        for (Way way : bus_route_ways)
        {
            List<Long> nodes = way.GetNodes();
            for (Long node : nodes)
            {
                if (node == nearest_place.GetId())
                {
                    way_id = way.GetId();
                    if (oops)
                    {
                        System.out.println("\"Hello\"");
                    }
                    oops = true;
                }

            }
        }

        for (BusRoute route : br_map.values())
        {
            List<Member> members = route.GetMembers();
            for (Member member : members)
            {
                if (nearest_place != null)
                {
                    if ((member.GetRef() == nearest_place.GetId()) || (member.GetRef() == way_id))
                    {
                        ret.add(route);

                    }

                }
            }
        }

        return ret;

    }

    public static double HaversineDistance(double lat1, double lat2, double lon1, double lon2, double el1, double el2)
    {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    public ArrayList<Place> GetRoutePlaces()
    {
        return bus_route_places;
    }

    public ArrayList<Place> GetCountryPlaces()
    {
        return country_places;
    }

    private HashMap<Long, BusRoute> br_map;
    // private ArrayList<BusRoute> bus_routes;
    private ArrayList<Place> bus_route_places;

    private ArrayList<Way> bus_route_ways;
    private ArrayList<Place> country_places;

    private GsonBuilder builder;

}
