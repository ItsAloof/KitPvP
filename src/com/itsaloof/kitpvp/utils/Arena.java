package com.itsaloof.kitpvp.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.itsaloof.kitpvp.KitPvPPlugin;
import com.itsaloof.kitpvp.api.events.ArenaJoinEvent;

import net.md_5.bungee.api.ChatColor;

public class Arena {
	private final KitPvPPlugin plugin;
	private List<Location> spawns = new ArrayList<Location>();
	private List<Player> players = new ArrayList<Player>();
	private String name;
	private int maxPlayers;
	private boolean inUse;
	  
	public Arena(KitPvPPlugin plugin, List<Location> spawns, String name, int max)
	{
		this.plugin = plugin;
		this.spawns = spawns;
		this.name = name;
		this.maxPlayers = max;
		this.inUse = false;
	}
	
	public Arena(KitPvPPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	public List<Location> getSpawns()
	{
		return this.spawns;
	}
	
	public boolean isArenaFull()
	{
		if(players.isEmpty())
			return false;
		return (players.size() >= maxPlayers);
	}
	
	private boolean overFlow()
	{
		return (players.size() > maxPlayers);
	}
	
	
	public boolean playerInArena(Player player)
	{
		return (this.players.contains(player));
	}
	
	public String getArenaName()
	{
		return this.name;
	}
	
	public void setArenaName(String name)
	{
		this.name = name;
	}
	
	public List<Player> getPlayers()
	{
		return this.players;
	}
	
	 public boolean inUse()
	  {
	    return this.inUse;
	  }
	 
	 public void toggleInUse()
	 {
		 if(inUse)
		 {
			 inUse = false;
			 return;
		 }
		 else
			 inUse = true;
	 }
	
	public boolean addPlayer(Player player)
	{
		ArenaJoinEvent event = new ArenaJoinEvent(this, player);
		plugin.getServer().getPluginManager().callEvent(event);
			
		if(event.isCancelled())
			return false;
			
		players.add(player);
		if(isArenaFull())
		{
			toggleInUse();
			teleportPlayers();
		}
		return true;
	}
	
	public void clearArena()
	{
		this.players.clear();
	}
	
	public void setPlayers(List<Player> players)
	{
		for(Player p : players)
		{
			ArenaJoinEvent event = new ArenaJoinEvent(this, p);
			plugin.getServer().getPluginManager().callEvent(event);
			if(event.isCancelled())
			{
				continue;
			}else
			{
				this.players.add(p);
			}
		}
	}
	
	public int getMaxPlayers()
	{
		return maxPlayers;
	}
	
	
	public void teleportPlayers()
	  {
	    if (overFlow()) {
	      for (int i = this.players.size() - 1; i > this.maxPlayers; i--)
	      {
	        ((Player)this.players.get(i)).sendMessage(ChatColor.RED + "Sorry this match is currently full you have been re-added to the queue");
	        this.players.remove(i);
	      }
	    }
	    for (int i = 0; i < this.players.size(); i++) {
	    	Bukkit.createWorld(new WorldCreator(this.spawns.get(i).getWorld().getName()));
	      ((Player)this.players.get(i)).teleport((Location)this.spawns.get(i));
	      plugin.queue.remove(plugin.players.get(players.get(i)));
		}
		plugin.updateSigns();
	  }
	
	public void saveArena()
	{
		File f = plugin.getArenaFile();
		if(!f.exists())
		{
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		final String path = "Arenas." + getArenaName();
		fc.set(path + ".maxPlayers", maxPlayers);
		int spawn = 0;
		for(Location l : spawns)
		{
			final String sPath = path + ".spawns.spawn" + spawn;
			fc.set(sPath + ".world", l.getWorld().getName());
			fc.set(sPath + ".x", l.getBlockX());
			fc.set(sPath + ".y", l.getBlockY());
			fc.set(sPath + ".z", l.getBlockZ());
			fc.set(sPath + ".pitch", l.getPitch());
			fc.set(sPath + ".yaw", l.getYaw());
			spawn++;
		}
		
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public Arena loadArena(String name, FileConfiguration fc)
	{
		this.spawns = toLoc(fc, "Arenas." + name + ".spawns");
		this.maxPlayers = fc.getInt("Arenas." + name + ".maxPlayers");
		setArenaName(name);
		return this;
	}
	
	
	private List<Location> toLoc(FileConfiguration fc, String path)
	{
		List<Location> locs = new ArrayList<Location>();
		for(String s : fc.getConfigurationSection(path).getKeys(false))
		{
			double x, 
			y, 
			z;
			
			float pitch, 
			yaw;
			World world = Bukkit.getWorld(fc.getString(path + "." + s + ".world"));
			
			x = fc.getInt(path + "." + s + ".x");
			y = fc.getInt(path + "." + s + ".y");
			z = fc.getInt(path + "." + s + ".z");
			
			pitch = (float) fc.getDouble(path + "." + s + ".pitch");
			yaw = (float) fc.getDouble(path + "." + s + ".yaw");
			locs.add(new Location(world, x, y, z, yaw, pitch));
		}
		return locs;
	}
	
	

}
