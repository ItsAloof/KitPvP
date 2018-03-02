package com.itsaloof.kitpvp.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.itsaloof.kitpvp.utils.Arena;
import com.sun.xml.internal.ws.api.Cancelable;


public class ArenaJoinEvent extends Event implements Cancelable{

	private final Arena arena;
	private boolean cancelled = false;
	private final Player player;
	
	public ArenaJoinEvent(Arena arena, Player player) {
		this.arena = arena;
		this.player = player;
	}
	
	public Arena getArena()
	{
		return this.arena;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	@Override
	public void cancel(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public boolean isCancelled()
	{
		return cancelled;
	}
	
	public void setCancelled(boolean cancelled)
	{
		this.cancelled = cancelled;
	}
	
	@Override
	public HandlerList getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

}