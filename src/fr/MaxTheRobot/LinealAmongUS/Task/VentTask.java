package fr.MaxTheRobot.LinealAmongUS.Task;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.MaxTheRobot.LinealAmongUS.Main;
import fr.MaxTheRobot.LinealAmongUS.Object.Map;

public class VentTask extends BukkitRunnable {
	
	@Override
	public void run() {
		for(Map m : Main.getMaps()) {
			Player i = Main.getImpostor(m);
			if(i.isSneaking()) {
				if(i.getLocation().getBlock().getType().equals(Material.IRON_TRAPDOOR)) {
					//vent code here
				}
			}
		}
	}

}
