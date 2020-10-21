/*
 * Used alongside with Project.java and WeatherData.java
 * 
 * Used to request the api and get the needed information to display the needed fino
 * in addition to parsing the json in addition to calling handling the api requests. 
 */
package api_ircbot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.jibble.pircbot.*;
import org.jibble.pircbot.Colors;
import org.jibble.pircbot.PircBot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser; 

public class IRCBot extends PircBot 
{
	static final String defaultLocation = "75080"; //default location if zip code isn't valid
    static final Pattern regex = Pattern.compile("(\\d{5})"); // used to find the zip code
    
    //the ircbot
	public IRCBot()
	{
		this.setName("LisaBot"); //the ircbot's name
	}
	
	//used if disconnection of the bot happens
	public void onDisconnect() 
	{
		while(!isConnected()) 
		{
			try 
			{
				reconnect(); //reconnecting with the server if needed
			}
			catch(Exception e) {}
		}
	}
	
	//used to handle the messages from the user to the bot and respond based on response
	public void onMessage(String channel, String sender, String login, String hostname, String message) 
	{
		//runs output for weather report from weather api
		if(message.toLowerCase().contains("weather")) 
		{
			String location = defaultLocation; //default location of 75080
			String [] words = message.split(" "); //splits the input into 2 parts
			
			//looks for the zip code and sends it to be processed as the location
            if (words.length == 2) //used to theres 2 words
            {
                /*
                 * If user input is "weather {location}" then location is assigned as 
                 * the second word otherwise the location is assigned to the 1st word 
                 * if the 1st word is the zip code 
                 */
                if (words[0].equals("weather")) 
                {
                    location = words[1]; //location assigned as the 2nd word
                } else 
                {
                    location = words[0]; //location assigned as the 1st word
                }
            }
            else //used to look for zip code if there isn't 2 words as input
            {
			    Matcher matcher = regex.matcher(message); //used to find zipcode
			    
                // looks for zipcode
                if (matcher.find()) 
                {
                 location = matcher.group(1); //has the location be the zip code 
                } 
                else 
                {
                 //Prints message saying it's using default location since it can't find the location the user entered
                 sendMessage(channel, "I don't know what that location is so I'm going to assume Richardson.");
                }
            }
            
            WeatherData data = getWeather(location); //gets the weather report after data is retrieved
            
            //Used to print error message and use default location if request to get weather report failed
            if (data == null) 
            {
                //Gets weather for the default location if the entered location isn't possible
                sendMessage(channel, "Can't get the weather data for " + location + ". Trying " + defaultLocation + " instead.");
                
                data = getWeather(defaultLocation); //gets weather for default location
                
                //Prints error message if the data request fails a 2nd time 
                if (data == null) 
                {
                    sendMessage(channel, "Sorry, there's a issue with the weather API on their side.");
                }
            }
            
            String weather = data.toString(); // assigns output to to be printed
            sendMessage(channel, weather);   // Output weather message
			
		}
		else if(!message.contains("weather"))
		{			
			String[] coordinates = startGoogleMapsRequest(message); //calls for geocode api

			//runs output for geocode api of coordinates
		    sendMessage(channel, Colors.BLUE + "The coordinates of " + Colors.BOLD + message + Colors.NORMAL + Colors.BLUE + " is " + Colors.BOLD + coordinates[0] + " , " + coordinates[1] + ".");
		}
	}
	
	//Welcome message from the bot that is printed on freenode
	public void onJoin(String channel, String sender, String login, String hostname) 
	{
			sendMessage(channel, Colors.BLUE + "Welcome, " + sender + ", I'm a chatbot that can look up" + " coordinates and the weather.");
	}
	
	//parses json to get the latitude part of the coordinates for the geocode api
	static double parseLatJson(String json) 
	{
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray resultsArray = jobject.getAsJsonArray("results");
		
		JsonObject resultsContent = resultsArray.get(0).getAsJsonObject();
		JsonObject geometry = resultsContent.getAsJsonObject("geometry");
		JsonObject location = geometry.getAsJsonObject("location");
		double lat = location.get("lat").getAsDouble();
		
		return lat; //returns latittude
	}
	
	//parses the josn for the longitude for the geocode api
	static double parseLngJson(String json) 
	{
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray resultsArray = jobject.getAsJsonArray("results");
		
		JsonObject resultsContent = resultsArray.get(0).getAsJsonObject();
		JsonObject geometry = resultsContent.getAsJsonObject("geometry");
		JsonObject location = geometry.getAsJsonObject("location");
		double lng = location.get("lng").getAsDouble();
		
		return lng; //returns longitude
	}
	
	//does the geocode api request for data to get the coordinates
	static String[] startGoogleMapsRequest(String address) 
	{
		String key = "AIzaSyBIY6l6PnlSN66vqs4cemz4ipAVKfWNqFM"; //api key
		String mapsURL = "https://maps.googleapis.com/maps/api/geocode/" + "json?address=" + address + "&key=" + key; //used for api call
		mapsURL = mapsURL.replaceAll(" ", "%20"); //replaces specified spot with specified info
		StringBuilder result = new StringBuilder(); //result of data object
		
		//does the geocode data getting and parsing of json while doing error checking
		try 
		{
			URL url = new URL(mapsURL); //used for api call
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); //calls api
			conn.setRequestMethod("GET"); //sets data to be gathered
			BufferedReader read = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8"))); //takes in data
			String line; //used for data
			
			//returns info while it's still not null
			while ((line = read.readLine()) != null) 
			{
				result.append(line); //returns the data
			}
			read.close(); //stops trying to read the info
			
			double lat = parseLatJson(result.toString()); //parses json for latitude
			double lng = parseLngJson(result.toString()); //parses json for longitude
			
			String coordinates[] = {Double.toString(lat), Double.toString(lng)}; //sets the coordinates for later printing
			
			return coordinates; //returns coordinates
		}
		catch (Exception e) //error catching for the data
		{ 
			String[] error = {"Error! Exception: " + e};
			return error;
		}
	}
	
	/*
	 * Calls the weather api and gets the needed weather report using the data provided
	 * from input. Parsining the json is included. 
	 */
	 public static WeatherData getWeather(String location) 
	 {	 
		 String key = "a41907f50ae8ce54e86eb00fd2a67b12"; //api key
		 final String endpoint = "http://api.openweathermap.org/data/2.5/weather?q=%s,us&APPID=%s";  //used for api call
		 
		 //parses json and calls weather to print out the needed data and prints error message if it doens't work
		 try 
			{
			 	//	weatherURL = weatherURL.replaceAll(" ", "%20"); 
				URL url = new URL(String.format(endpoint, location, key)); //api object
		        HttpURLConnection http = (HttpURLConnection) url.openConnection();
		        http.setRequestMethod("GET");
		            
		         // Reads the data returned from the API
		        BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
		        StringBuilder result = new StringBuilder();

		        String line; //used for info
		        //reads in the info while it's not null
		        while ((line = reader.readLine()) != null) 
		        {
		        	result.append(line); //appends the info
		        }
		            reader.close(); //stops reading the info
		            
		        return parseDescription(result.toString());  // Return the parsed weather api data
			} 
		 catch (Exception e) 
		 {
			 System.out.println("Failed to get weather data"); //prints out error message if can't get weather data
		 }
		     return null; // Return null if failure to work
      }
	 
	 	/*
	 	 * This method parses the JSON data returned by the weather API and returns 
	 	 * the needed data
	 	 */
	    private static WeatherData parseDescription(String json) 
	    {
	        // Parses entire JSON string and convert to object
	        JsonObject object = new JsonParser().parse(json).getAsJsonObject();

	        // Get the object under the "main" key
	        JsonObject main = object.getAsJsonObject("main");

	        // Get the temperatures from "main" object
	        double temp = main.get("temp").getAsDouble(); //gets weather's temperature
	        double high = main.get("temp_max").getAsDouble(); //gets the highest temperature
	        double low = main.get("temp_min").getAsDouble(); //gets the lowest temperature

	        // Get weather from the main object of first element of "weather" array in the "main" object
	        String weather = object.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

	        // Get the location name from the root object
	        String location = object.get("name").getAsString();

	        // Return fetched data as a WeatherData object
	        return new WeatherData(temp, high, low, weather, location);
	    }
	    
}

