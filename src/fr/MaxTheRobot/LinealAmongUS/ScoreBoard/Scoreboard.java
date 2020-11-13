package fr.MaxTheRobot.LinealAmongUS.ScoreBoard;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.MaxTheRobot.LinealAmongUS.Main;

public class Scoreboard implements Listener {

	private static ScoreboardManager scoreboardManager;
    private static ScheduledExecutorService executorMonoThread;
    private static ScheduledExecutorService scheduledExecutorService;
    private static Main main;
    
    public Scoreboard(Main main) {
    	Scoreboard.main = main;
    	Bukkit.getPluginManager().registerEvents(this, getMain());
    	scheduledExecutorService = Executors.newScheduledThreadPool(16);
        executorMonoThread = Executors.newScheduledThreadPool(1);
        scoreboardManager = new ScoreboardManager(Scoreboard.main);
    }
    
    public static ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }
 
    public static ScheduledExecutorService getExecutorMonoThread() {
        return executorMonoThread;
    }
 
    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }
    
    public static Main getMain() {
		return main;
	}
    
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	Scoreboard.getScoreboardManager().onLogin(e.getPlayer());
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	Scoreboard.getScoreboardManager().onLogout(e.getPlayer());
    }
}
