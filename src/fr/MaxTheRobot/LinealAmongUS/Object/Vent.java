package fr.MaxTheRobot.LinealAmongUS.Object;

import java.util.List;

import org.bukkit.Location;

public class Vent {

	Map map;
	String name;
	Location location;
	List<Location> Vents;
	
	public Vent(Map map, String name, Location location, List<Location> vents) {
		this.map = map;
		this.name = name;
		this.location = location;
		Vents = vents;
	}

	public Map getMap() {
		return map;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public List<Location> getVents() {
		return Vents;
	}
	
	public String getName() {
		return name;
	}
	
	public void setMap(Map map) {
		this.map = map;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
