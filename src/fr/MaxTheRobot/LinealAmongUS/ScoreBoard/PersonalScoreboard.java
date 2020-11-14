package fr.MaxTheRobot.LinealAmongUS.ScoreBoard;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.MaxTheRobot.LinealAmongUS.Main;
import fr.MaxTheRobot.LinealAmongUS.Object.Map;

public class PersonalScoreboard {
    private final UUID uuid;
    private final ObjectiveSign objectiveSign;
    private final Player player;
    
    PersonalScoreboard(Player player, Main main){
        this.player = player;
        uuid = player.getUniqueId();
        objectiveSign = new ObjectiveSign("sidebar", "linealmc");
        reloadData();
        objectiveSign.addReceiver(player);
    }
 
    public void reloadData(){}
 
    public void setLines(String ip){
        objectiveSign.setDisplayName("§eLineal§aAmongUS");
 
        objectiveSign.setLine(0, "§1");
        if(Main.getPlayerMap(player) == null) {
        	objectiveSign.clearScores();
            objectiveSign.setLine(1, "§7Maps :");
	        int i = 0;
	        for(Map m : Main.maps) {
	        	String color = "§3";
	        	if(!m.isOpen()) color = "§c";
	        	objectiveSign.setLine((i+2), "§7-> " + color + m.getName() + " : §6" + m.getPlayers().size() + "§7/§610");
	        	i++;
	        }
	        objectiveSign.setLine(Main.maps.size() + 2, "§b");
	        objectiveSign.setLine(Main.maps.size() + 3, "§elineal§amc.fr");
        } else {
        	objectiveSign.clearScores();
	        switch (Main.getPlayerMap(player).getStatus()) {
			case WAITING:
				objectiveSign.clearScores();
				objectiveSign.setLine(1, "§7Joueurs : " + Main.getPlayerMap(player).getPlayers().size() + "/10");
				objectiveSign.setLine(2, "§7");
				objectiveSign.setLine(3, "§elineal§amc§e.fr");
				break;
			case STARTING:
				objectiveSign.clearScores();
				objectiveSign.setLine(1, "§7Start in : " + Main.getPlayerMap(player).getStartIn());
				objectiveSign.setLine(2, "§7");
				objectiveSign.setLine(3, "§elineal§amc§e.fr");
				break;
			case PLAYING:
				objectiveSign.clearScores();
				objectiveSign.setLine(1, "§7Role : " + Main.getPlayerRole(player).toString());
				objectiveSign.setLine(2, "§7");
				objectiveSign.setLine(3, "§elineal§amc§e.fr");
			case VOTING:
				objectiveSign.clearScores();
				objectiveSign.setLine(1, "§7Role : " + Main.getPlayerRole(player).toString());
				objectiveSign.setLine(2, "§7");
				objectiveSign.setLine(3, "§7Number of vote for you : " + Main.getPlayerVote(player));
				objectiveSign.setLine(5, "§a");
				objectiveSign.setLine(6, "§7Vote time left : " + Main.getPlayerMap(player).getVotetime());
				objectiveSign.setLine(7, "§b");
				objectiveSign.setLine(8, "§elineal§amc§e.fr");
				break;
			default:
				break;
			}
        }
        objectiveSign.updateLines();
    }
 
    public void onLogout(){
        objectiveSign.removeReceiver(Bukkit.getServer().getOfflinePlayer(uuid));
    }
}