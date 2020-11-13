package fr.MaxTheRobot.LinealAmongUS.ScoreBoard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.MaxTheRobot.LinealAmongUS.Main;

@SuppressWarnings("all")
public class ScoreboardManager {
    private final Map<UUID, PersonalScoreboard> scoreboards;
	private final ScheduledFuture glowingTask;
    private final ScheduledFuture reloadingTask;
    private int ipCharIndex;
    private int cooldown;
    private Main main;
    
    public ScoreboardManager(Main main) {
    	this.main = main;
        this.scoreboards = new HashMap<>();
        ipCharIndex = 0;
        cooldown = 0;
 
        glowingTask = Scoreboard.getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            String ip = colorIpAt();
            for (PersonalScoreboard scoreboard : this.scoreboards.values())
            	Scoreboard.getExecutorMonoThread().execute(() -> scoreboard.setLines(ip));
        }, 80, 80, TimeUnit.MILLISECONDS);
 
        reloadingTask = Scoreboard.getScheduledExecutorService().scheduleAtFixedRate(() ->
        {
            for (PersonalScoreboard scoreboard : this.scoreboards.values())
            	Scoreboard.getExecutorMonoThread().execute(scoreboard::reloadData);
        }, 1, 1, TimeUnit.SECONDS);
    }
 
    public void onDisable() {
        this.scoreboards.values().forEach(PersonalScoreboard::onLogout);
    }
 
    public void onLogin(Player player) {
        if (this.scoreboards.containsKey(player.getUniqueId())) {
            return;
        }
        this.scoreboards.put(player.getUniqueId(), new PersonalScoreboard(player, this.main));
    }
 
    public void onLogout(Player player) {
        if (this.scoreboards.containsKey(player.getUniqueId())) {
        	this.scoreboards.get(player.getUniqueId()).onLogout();
        	this.scoreboards.remove(player.getUniqueId());
        }
    }
 
    public void update(Player player) {
        if (this.scoreboards.containsKey(player.getUniqueId())) {
        	this.scoreboards.get(player.getUniqueId()).reloadData();
        }
    }
 
	private String colorIpAt() {
        String ip = "www.linealmc.fr";
 
        if (cooldown > 0) {
            cooldown--;
            return ChatColor.YELLOW + ip;
        }
 
        StringBuilder formattedIp = new StringBuilder();
 
        if (ipCharIndex > 0) {
            formattedIp.append(ip.substring(0, ipCharIndex - 1));
            formattedIp.append(ChatColor.GOLD).append(ip.substring(ipCharIndex - 1, ipCharIndex));
        } else {
            formattedIp.append(ip.substring(0, ipCharIndex));
        }
 
        formattedIp.append(ChatColor.RED).append(ip.charAt(ipCharIndex));
 
        if (ipCharIndex + 1 < ip.length()) {
            formattedIp.append(ChatColor.GOLD).append(ip.charAt(ipCharIndex + 1));
 
            if (ipCharIndex + 2 < ip.length())
                formattedIp.append(ChatColor.YELLOW).append(ip.substring(ipCharIndex + 2));
 
            ipCharIndex++;
        } else {
            ipCharIndex = 0;
            cooldown = 50;
        }
 
        return ChatColor.YELLOW + formattedIp.toString();
    }
 
}
