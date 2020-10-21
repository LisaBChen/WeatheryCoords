/*
 * Contains the setters, getters, objects, and methods needed for
 * IRCBot.java. Data returned from this will be used to display the needed infomration.
 * 
 * This file should be used alongside Project.java and IRCBot.java. 
 */
package api_ircbot;

public class WeatherData {

    private double temp;
    private double high;
    private double low;
    private String weather;
    private String location;

    //calls needed methods for the info on the weather thats needed that will be used
    public WeatherData(double temp, double high, double low, String weather, String location) {
        setTemp(temp);
        setHigh(high);
        setLow(low);
        setWeather(weather);
        setLocation(location);
    }

    // Returns the info in a formmatted manner
    public String toString() {
        return String.format("The weather in %s is %s. The temperature is %.1f°F, with a high of %.1f°F and a low of %.1f°F.", location, weather, temp, high, low);
    }

    // Convert from Kelvin to Fahrenheit and returns the amount to get needed temp
    private double kToF(double kelvin) {
        return (kelvin - 273.15) * 1.8 + 32;
    }

    //getter for highest temp
    public double getHigh() {
        return high;
    }

    //setter for highest temp
    public void setHigh(double high) {
        this.high = kToF(high);
    }

    //getter for lowest temp
    public double getLow() {
        return low;
    }

    //setter for lowest temp
    public void setLow(double low) {
        this.low = kToF(low);
    }

    //getter for weather
    public String getWeather() {
        return weather;
    }

    //setter for location 
    public void setWeather(String weather) {
        this.weather = weather.toLowerCase();
    }

    //getter for temperature of location
    public double getTemp() {
        return temp;
    }

    //setter for temperature of location
    public void setTemp(double temp) {
        this.temp = kToF(temp);
    }

    //gets needed location
    public String getLocation() {
        return location;
    }

    //sets requested location
    public void setLocation(String location) {
        this.location = location;
    }
}