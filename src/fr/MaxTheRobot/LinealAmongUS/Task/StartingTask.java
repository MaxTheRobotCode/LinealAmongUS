package fr.MaxTheRobot.LinealAmongUS.Task;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.MaxTheRobot.LinealAmongUS.Main;
import fr.MaxTheRobot.LinealAmongUS.Status;
import fr.MaxTheRobot.LinealAmongUS.Object.Map;

public class StartingTask extends BukkitRunnable {

	Map m;
	public StartingTask(Map m) {
		this.m = m;
	}
	
	int i = 13;
	@Override
	public void run() {
		if(i > 10) { i--; return; }
		m.setStartIn(i);
		if(i == 10) { Main.getPlayers(m).forEach(p -> p.sendTitle("�cStart in : 10s", "", 1, 20, 1)); i--; return; }
		Main.getPlayers(m).forEach(p -> p.sendTitle("�c" + i + "s", "", 1, 20, 1));
		
		if(i == 0) {
			for(Player p : Main.getPlayers(m)) p.sendTitle("�cTu es :", "�e" + m.getPlayers().get(p).toString(), 1, 60, 1);
			m.setStatus(Status.PLAYING);
			Main.getPlayers(m).forEach(p -> p.getInventory().setItem(8, Main.report));
			this.cancel();
		}
		i--;
	}
}
