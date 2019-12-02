package com.osm_query.query;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	HttpClientBasic  h = new HttpClientBasic();
    	try {
    	
    		/*
    		String qid = args[0];
    		String q = args[1];
    		Path out_file = Paths.get("D:/json_data/"+qid+".json");
			h.SendOsmQuery(out_file, q);
			System.out.print("Q GENERATED\n");
			System.out.print(Files.size(out_file)/1024);
			System.out.print("\n");
			*/

    		
   		Pipeline pipe = new Pipeline();
   		pipe.Test();

		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
