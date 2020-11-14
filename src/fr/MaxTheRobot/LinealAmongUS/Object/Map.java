package fr.MaxTheRobot.LinealAmongUS.Object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fr.MaxTheRobot.LinealAmongUS.Role;
import fr.MaxTheRobot.LinealAmongUS.Status;

public class Map {

	HashMap<Player, Role> players;
	String name;
	boolean open;
	Status status;
	int startIn;
	int votetime;
	List<Player> deathPlayers;
	List<Entity> skeletons;
	HashMap<Player, Integer> vote;
	List<Location> taskButtons;
	Location EMLoc;
	
	public Map(String name, boolean open, Location EMLoc, List<Location> taskButtons) {
		this.players = new HashMap<>();
		this.name = name;
		this.open = open;
		this.status = Status.WAITING;
		this.startIn = 10;
		this.deathPlayers = new ArrayList<Player>();
		this.skeletons = new ArrayList<Entity>();
		this.vote = new HashMap<>();
		this.EMLoc = EMLoc;
		this.taskButtons = taskButtons;
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<Player, Role> getPlayers() {
		return players;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public int getStartIn() {
		return startIn;
	}
	
	public List<Player> getDeathPlayers() {
		return deathPlayers;
	}
	
	public List<Entity> getSkeletons() {
		return skeletons;
	}
	
	public HashMap<Player, Integer> getVote() {
		return vote;
	}
	
	public int getVotetime() {
		return votetime;
	}
	
	public Location getEMLoc() {
		return EMLoc;
	}
	
	public List<Location> getTaskButtons() {
		return taskButtons;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setStartIn(int startIn) {
		this.startIn = startIn;
	}
	
	public void setVotetime(int votetime) {
		this.votetime = votetime;
	}
	
	public void setEMLoc(Location eMLoc) {
		EMLoc = eMLoc;
	}
}
