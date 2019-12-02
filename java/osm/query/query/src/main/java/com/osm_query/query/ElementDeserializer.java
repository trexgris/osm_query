package com.osm_query.query;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ElementDeserializer implements JsonDeserializer<Element>{
	
	@Override
    public Element deserialize(JsonElement json, Type typeOfT,
                       JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = (JsonObject) json;
        JsonElement typeObj = jObject.get("type");
        JsonElement tags_obj = jObject.get("tags");

        if(typeObj!= null ){
            String typeVal = typeObj.getAsString();

            switch (typeVal){
                case "node": {
                	//if(tags_obj == null) {
                		return context.deserialize(json, Place.class);
                	//} else  {
                	//	return context.deserialize(json, Place.class);                		
                	//}
                }
                case "relation": {
                	return context.deserialize(json, BusRoute.class);
                }
                case "way": {
                	return context.deserialize(json, Way.class);
                }
            }
        }

        return null;
    }


}
