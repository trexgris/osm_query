package com.osm_query.query;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.omg.CORBA.Environment;
import org.omg.CORBA.portable.InputStream;

public class HttpClientBasic {
	
	private final CloseableHttpClient http_client_ = HttpClients.createDefault();
	
	public void Close() throws IOException {
		http_client_.close();
    }
	
	public void SendOsmQuery(Path qid, String q) throws Exception {
		String qencoded = overpass_api_url_ + URLEncoder.encode(q, "UTF-8");
    	HttpGet request = new HttpGet(qencoded);
        try (CloseableHttpResponse response = http_client_.execute(request)) {
        	File json_response = new File(qid.toString());
            FileUtils.copyInputStreamToFile(response.getEntity().getContent(), json_response);
   		 	response.getEntity().getContent().close();
        }
        catch(Exception e) { 
			e.printStackTrace();        
        }
        
	 }
	
	private String overpass_api_url_ = "https://www.overpass-api.de/api/interpreter?data=";
	
}
