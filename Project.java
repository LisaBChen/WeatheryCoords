/*
 * This project uses 2 api's: current weather api and an api of my choosing.
 * 
 * The 2nd api should be a geocoding api. 
 * 
 * An irc bot will be implemented and interacted with on freenode.
 * 
 * This file should be used with WeatherData.java and IRCBot.java. 
 * 
 * This file should be run and then after going to freenode, you should connect
 * to the server: irc.freenode.net and the channel: #cs2336Bao to set up the channel
 * that users will interact with the bot
 * 
 * Remember to use the /server irc.freenode.net, click the connect button
 * and use /join #cs2336Bao. 
 * 
 * Afterwards the user should open up another tab to actually join the channel 
 * to interact with the bot 
 * 
 * This project doesn't require a password when joining on freenode
 * and any nickname is fine. 
 * 
 * Project.java should connect with the bot and the ide should be closed after testing
 * to avoid spending more money on the api than you have already. 
 * 
 * (Using bot.disconnect(); should be avoided if you're doing long term testing)
 */
package api_ircbot;

public class Project 
{
	public static void main(String[] args) throws Exception
	{
		String server = "https://webchat.freenode.net/"; //server name
		String channel = "#cs2336Bao"; //channel name
			
	    IRCBot bot = new IRCBot(); //bot object
	        
	    bot.setVerbose(true); //turns on debugging output
	    
	    //runs the bot while utilizing error catching
	    try {
	    	bot.connect(server); //connects to the irc server
	        } catch (Exception e)
	        {
	        	System.out.println("Failed to connect to the server"); //error message
	        }finally {
		        bot.joinChannel(channel); //joins to the channel
		        bot.sendMessage(channel, "cs2336Bao");	//used for IRCBot.java for the info
	        }
	}
}

