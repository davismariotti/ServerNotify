package me.gomeow.servernotify;

import java.util.ArrayList;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class servernotify extends JavaPlugin {

	//Define a whole bunch of variables.
	Boolean on = true;
	String noOnlineMessage;
	String noOpsOnlineMessage;
	String noAdminsOnlineMessage;
	ArrayList<String> messages = new ArrayList<String>();
	Integer maxplayers;
	
	/*
	 * Gets the messages needed to broadcast.
	 * @return Returns a List of messages to be broadcasted.
	 */
	public ArrayList<String> getMessages() {
		ArrayList<String> newmessages = new ArrayList<String>();
		//Get the online players.
		Player[] online = Bukkit.getServer().getOnlinePlayers();
		//Define more variables.
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> oplist = new ArrayList<String>();
		ArrayList<String> adminList = new ArrayList<String>();
		//Add their names to the all players list.
		for(Player p : online) {
			names.add(p.getDisplayName());
		}
		for(Player p : online) {
			//If they are op, add them to the op list.
			if(p.isOp()) {
				oplist.add(p.getDisplayName());
			}
			//If the are an admin (defined by a permission), add them to the admin list.
			if(p.hasPermission("servernotify.admin")) {
				adminList.add(p.getDisplayName());
			}
		}
		String players = new String();
		String ops = new String();
		String admins = new String();
		
		/*
		 * Put all the lists into strings.
		 */
		for(String name : names) {
			if(!(name == names.get(names.size() - 1))) { //If not the last iteration...
				//Put a comma
				players = players + name + ", ";
				
			}
			//Otherwise no comma.
			else players = players + name; 
		}
		//Repeat the above process.
		for(String name : oplist) {
			if(!(name == oplist.get(oplist.size() - 1))) {
				ops = ops + name + ", ";
				
			}
			else ops = ops + name;
		}
		for(String name : adminList) {
			if(!(name == adminList.get(adminList.size() - 1))) {
				admins = admins + admins + ", ";
				
			}
			else admins = admins + name;
		}
		
		//Check if there is nothing in the lists.
		//If so, replace the list with the No-Online messages.
		if(players.isEmpty()) players = noOnlineMessage;
		if(oplist.isEmpty()) ops = noOpsOnlineMessage;
		if(adminList.isEmpty()) admins = noAdminsOnlineMessage;
		Integer intOnline = 0;
		//Get the online amount
		for(int i = 0; i < online.length; i++) {
			intOnline++;
		}
		//Get the players/max-players string.
		String onlineAmount = intOnline.toString() + "/" + maxplayers.toString();
		for(String line : messages) {
			
			/*
			 * Finally time to change the strings.
			 * (?i) is regex for unknown case.
			 * It works.
			 */
			String changedString = line.replaceAll("(?i)%players%", players)
					.replaceAll("(?i)%onlineamount%", onlineAmount)
					.replaceAll("(?i)%ops%", ops)
					.replaceAll("(?i)%admins%", admins)
					.replaceAll("(?i)%t", "\t")
					.replaceAll("(?i)%quote%", "\'")
					.replaceAll("(?i)%doublequote%", "\"");
			changedString = ChatColor.translateAlternateColorCodes('&', changedString);
			//Add the changed string to a list of changed strings.
			newmessages.add(changedString);
		}
		return newmessages;
	}
	
	@Override
	public void onEnable() {
		//Get the messages and No Online Messages from the config.
		noOnlineMessage = this.getConfig().getString("No-Players-Online-Message");
		noOpsOnlineMessage = this.getConfig().getString("No-Ops-Online-Message");
		noAdminsOnlineMessage = this.getConfig().getString("No-Admins-Online-Message");
		saveDefaultConfig();
		final Set<String> messagekeys = this.getConfig().getConfigurationSection("Messages").getKeys(false);
		for(String messageline : messagekeys) {
			messages.add(this.getConfig().getString("Messages."+messageline));
		}
		
		//If the No-Online Messages are undefined, supply a default.
		if(noOnlineMessage == null) noOnlineMessage = "No Players Online";
		if(noOpsOnlineMessage == null) noOpsOnlineMessage = "No Ops Online";
		if(noAdminsOnlineMessage == null) noAdminsOnlineMessage = "No Admins Online";

		//Get the maximum amount of players.
		maxplayers = Bukkit.getServer().getMaxPlayers();
		//Get the interval at which the messages should be displayed, in seconds.
		Integer interval = this.getConfig().getInt("Interval");
		
		//Run the task
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for(String line : /*Get the messages*/ getMessages()) {
					//Broadcast the messages.
					Bukkit.broadcastMessage(line);
				}
			}
			
		}, 0L, interval * 20);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return false;
	}
}
