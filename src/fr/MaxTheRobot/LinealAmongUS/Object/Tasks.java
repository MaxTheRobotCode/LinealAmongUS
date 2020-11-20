package fr.MaxTheRobot.LinealAmongUS.Object;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class Tasks {
	private HashMap<Player, ArrayList<Tasks>> playerTask = new HashMap<Player, ArrayList<Tasks>>();
	private HashMap<TaskObject, Location> taskList = new HashMap<TaskObject, Location>();
	private ArrayList<Player> players = new ArrayList<Player>();
	
	public Tasks(HashMap<TaskObject, Location> t) {
		taskList = t;
	}
	
}
