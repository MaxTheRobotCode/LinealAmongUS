package fr.MaxTheRobot.LinealAmongUS.Task;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.MaxTheRobot.LinealAmongUS.Main;
import fr.MaxTheRobot.LinealAmongUS.Role;
import fr.MaxTheRobot.LinealAmongUS.Status;
import fr.MaxTheRobot.LinealAmongUS.Object.Map;

public class vottingTask extends BukkitRunnable {

	Map m;
	public vottingTask(Map m) {
		this.m = m;
	}
	
	int i = 60;
	@Override
	public void run() {
		if(i == 0) {
			Player p = Main.getPlayerWithMaxVote(m);
			p.setGameMode(GameMode.CREATIVE);
			p.getInventory().clear();
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, true));
			p.sendMessage("§cVous are ejected !");
			m.setStatus(Status.PLAYING);
			m.getPlayers().keySet().forEach(a -> { 
				a.sendMessage("§c" + p.getName() + " was ejected !");
				if(Main.getImpostor(m).equals(p)) { a.sendMessage("§a" + p.getName() + " is the impostor !"); } else { a.sendMessage("§c" + p.getName() + " is not the impostor !"); }
				a.sendMessage("§cChat Disable !"); });
			m.getDeathPlayers().add(p);
			m.getPlayers().remove(p);
			if(m.getPlayers().size() == 1) {
				Role r = Role.dead;
				m.getPlayers().keySet().forEach(g -> Main.getPlayerRole(p));
				Main.win(m, r);
			}
			this.cancel();
		}
		m.setVotetime(i);
		i--;
	}
}
