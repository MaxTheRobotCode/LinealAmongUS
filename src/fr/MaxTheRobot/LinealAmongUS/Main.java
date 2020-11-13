package fr.MaxTheRobot.LinealAmongUS;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.MaxTheRobot.LinealAmongUS.Object.Map;
import fr.MaxTheRobot.LinealAmongUS.ScoreBoard.Scoreboard;
import fr.MaxTheRobot.LinealAmongUS.Task.StartingTask;
import fr.MaxTheRobot.LinealAmongUS.Task.vottingTask;
import fr.MaxTheRobot.UtilsAPI.Item;
import fr.MaxTheRobot.saveAPI.SaveAPI;
import fr.MaxTheRobot.saveAPI.SaveFile;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

	public static List<Map> maps = new ArrayList<Map>();
	private File saveFolder;
	public SaveAPI saveAPI;
	private List<SaveFile> saveFiles;
	private List<String> saveFilesNames;
	private boolean b = true;
	private static Main instance;
	
	public static ItemStack report = Item.fromMat(Material.BONE).setName("§eReport").setEnchented().setLore("§cRight clic for report a body !").toItem();
	
	@Override
	public void onEnable() {
		System.out.println("[LinealAmongUS] Plugin ON !");
		Bukkit.getPluginManager().registerEvents(this, this);
		
		getDataFolder().mkdir();
		saveFolder = new File(getDataFolder() + "/save/");
		saveAPI = new SaveAPI(saveFolder);
		saveFiles = new ArrayList<>();
		saveFolder.mkdir();
		if(createFiles(Arrays.asList("map"))) {
			System.out.println("[LinealAmongUS] SaveFile creation complete !");
		} else System.out.println("[LinealAmongUS] SaveFile already exists or creation failed !");
		setup();
		
		getSaveFile("map").getContent().forEach(l -> maps.add(new Map(l.split(",")[0], Boolean.valueOf(l.split(",")[1]))));
		
		getCommand("vote").setExecutor(this);
		
		instance = this;
		new Scoreboard(this);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 1.5, 6, 28.5, 180, 0));
		e.getPlayer().getInventory().clear();
		e.getPlayer().showPlayer(instance, e.getPlayer());;
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() != null) {
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(e.getClickedBlock().getState() instanceof Sign) {
					Sign s = (Sign) e.getClickedBlock().getState();
					Map m = getMapByName(s.getLine(1));
					e.getPlayer().teleport(new Location(Bukkit.getWorld(s.getLine(1)), 8, 92, -10));
					m.getPlayers().put(e.getPlayer(), setRole(m));
					
					if(m.getPlayers().size() == 4) {
						m.setStatus(Status.STARTING);
						new StartingTask(m).runTaskTimer(this, 0, 20);
					}
				}
			}
		}
		if(getPlayerMap(e.getPlayer()) != null) {
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				if(e.getPlayer().getInventory().getItemInMainHand().equals(report)) {
					getPlayerMap(e.getPlayer()).setStatus(Status.VOTING);
					Bukkit.broadcastMessage("set voting");
					getPlayers(getPlayerMap(e.getPlayer())).forEach(p -> { p.sendTitle("§cBody report !", "", 1, 60, 1); p.sendMessage("§l§aChat enabled !");});
					new vottingTask(getPlayerMap(e.getPlayer())).runTaskTimer(this, 0, 20);
				}
			}
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(getPlayerMap(e.getPlayer()) == null) { e.setCancelled(true); return; }
		if(getPlayerMap(e.getPlayer()).getDeathPlayers().contains(e.getPlayer())) { e.getPlayer().sendMessage("§cYou are dead !"); e.setCancelled(true); return; }
		if(!getPlayerMap(e.getPlayer()).getStatus().equals(Status.VOTING)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cChat is disable !");
			return;
		}
		getPlayerMap(e.getPlayer()).getPlayers().keySet().forEach(p -> p.sendMessage("§7" + e.getPlayer().getName() + " > " + e.getMessage()));
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		try {
			if(e.getTo().getWorld().equals(Bukkit.getWorld("world"))) {
				getMapByName(e.getFrom().getWorld().getName()).getPlayers().remove(e.getPlayer());
				e.getPlayer().getInventory().clear();
				List<PotionEffectType> gs = new ArrayList<PotionEffectType>();
				for(PotionEffect g : e.getPlayer().getActivePotionEffects()) gs.add(g.getType());
				for(PotionEffectType g : gs) e.getPlayer().removePotionEffect(g);
			}
		} catch (Exception e1) {}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
			Player p = (Player) e.getDamager();
			Player v = (Player) e.getEntity();
			Map m = getPlayerMap(p);
			if(getImpostor(m).equals(p)) {
				e.setCancelled(true);
				kill(v);
				v.getWorld().spawnParticle(Particle.REDSTONE, v.getLocation(), 150, 0, 3, 0);
				Skeleton s = (Skeleton) v.getWorld().spawnEntity(v.getLocation(), EntityType.SKELETON);
				s.setAI(false);
				s.setCustomName(v.getName());
				v.setGameMode(GameMode.CREATIVE);
				v.getInventory().clear();
				getPlayers(m).forEach(a -> a.hidePlayer(this, v));
				getPlayerMap(p).getSkeletons().add(s);
				if(getPlayerMap(p).getPlayers().size() == 1) {
					Role r = Role.dead;
					for(Player g : getPlayerMap(p).getPlayers().keySet()) r = getPlayerRole(g);
					win(getPlayerMap(p), r);
				}
			} else e.setCancelled(true);
		}
		else e.setCancelled(true);
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		if(getPlayerMap(e.getPlayer()) == null) return;
		e.getPlayer().getInventory().clear();
		List<PotionEffectType> gs = new ArrayList<PotionEffectType>();
		for(PotionEffect g : e.getPlayer().getActivePotionEffects()) gs.add(g.getType());
		for(PotionEffectType g : gs) e.getPlayer().removePotionEffect(g);
		if(getImpostor(getPlayerMap(e.getPlayer())).equals(e.getPlayer())) {
			win(getPlayerMap(e.getPlayer()), Role.crewmate);
		} else {
			getPlayerMap(e.getPlayer()).getPlayers().remove(e.getPlayer());
			getPlayers(getPlayerMap(e.getPlayer())).forEach(p -> p.sendMessage("§a" + e.getPlayer().getName() + " a quitté la partie !"));
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return true;
		Player p = (Player) sender;
		Map m = getPlayerMap(p);
		if(m == null) return true;
		if(m.getStatus().equals(Status.VOTING) && !getPlayerRole(p).equals(Role.dead)) {
			if(args.length == 0) {
				p.sendMessage("§cspecify a player !");
			} else if(args.length == 1) {
				Player v = Bukkit.getPlayer(args[0]);
				if(v == null) { p.sendMessage("§cThis player is not online!"); return true; }
				Map vm = getPlayerMap(v);
				if(vm.equals(m)) {
					if(m.getDeathPlayers().contains(v)) {
						p.sendMessage("§cThis player is dead !");
						return true;
					}
					if(m.getVote().containsKey(p)) {
						p.sendMessage("§cYou have already vote !");
						return true;
					}
					p.sendMessage("§aYou have vote for " + v.getName());
					if(!m.getVote().containsKey(v)) m.getVote().put(v, 0);
					m.getVote().replace(v, m.getVote().get(v), m.getVote().get(v) + 1);
				} else p.sendMessage("§cThis player is not in the same game as you !");
			}
		} else p.sendMessage("§cYou can't vote now !");
		return true;
	}
	
	public static void kill(Player p) {
		getPlayerMap(p).getDeathPlayers().add(p);
		getPlayerMap(p).getPlayers().remove(p);
		p.setGameMode(GameMode.CREATIVE);
		p.getInventory().clear();
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, true));
		getPlayerMap(p).getPlayers().replace(p, getPlayerRole(p), Role.dead);
	}
	
	public static void win(Map m, Role r) {
		Bukkit.broadcastMessage("§cWINNER : " + r.name());
		m.getSkeletons().forEach(s -> ((Skeleton) s).setHealth(0));
		m.getDeathPlayers().forEach(p -> {
			p.teleport(new Location(Bukkit.getWorld("world"), 1.5, 6, 28.5, 180, 0));
		});
		m.getPlayers().keySet().forEach(p -> {
			p.teleport(new Location(Bukkit.getWorld("world"), 1.5, 6, 28.5, 180, 0));
		});
		m.getPlayers().clear();
		m.getDeathPlayers().clear();
		m.setStatus(Status.WAITING);
	}
	
	public static Map getPlayerMap(Player p) {
		for(Map m : maps) {
			if(m.getPlayers().containsKey(p)) return m;
			if(m.getDeathPlayers().contains(p)) return m;
		}
		return null;
	}
	
	public static Map getMapByName(String name) {
		for(Map m : maps) {
			if(m.getName().equals(name)) return m;
		}
		return null;
	}
	
	public boolean createFiles(List<String> names) {
		saveFilesNames = names;
		names.forEach(s -> { if(!saveAPI.createEmptyFile(s)) b = false; });
		return b;
	}
	public boolean setup() {
		System.out.println("[LinealAmongUS] Files to load : " + saveFilesNames.toString());
		saveFilesNames.forEach(f -> { saveFiles.add(saveAPI.get(f)); System.out.println("[LinealAmongUS] SaveFile " + f + " as loaded !"); });
		return true;
	}
	
	public boolean save() {
		saveFiles.forEach(f -> saveAPI.save(f));
		return true;
	}
	
	public SaveFile getSaveFile(String name) {
		for(SaveFile f : saveFiles) {
			if(f.getName().equals(name)) return f;
		}
		return null;
	}
	
	public void addMaps(String name) {
		getSaveFile("map").getContent().add(name);
	}
	
	public Role setRole(Map m) {
		Role r = Role.values()[new Random().nextInt(Role.values().length)];
		if(r == Role.dead) r = Role.crewmate;
		for(Entry<Player, Role> entry : m.getPlayers().entrySet()) if(entry.getValue().equals(Role.impostor)) r = Role.crewmate;
		int innocent = 0;
		for(Entry<Player, Role> entry : m.getPlayers().entrySet()) if(entry.getValue().equals(Role.crewmate)) innocent++;
		if(m.getPlayers().size() == 3 && innocent == 3) r = Role.impostor;
		return r;
	}
	
	public static Set<Player> getPlayers(Map m){
		return m.getPlayers().keySet();
	}
	
	public static Player getImpostor(Map m) {
		for(Entry<Player, Role> e : m.getPlayers().entrySet()) if(e.getValue().equals(Role.impostor)) return e.getKey();
		return null;
	}
	
	public static Role getPlayerRole(Player p) {
		for(Entry<Player, Role> e : getPlayerMap(p).getPlayers().entrySet()) if(e.getKey().equals(p)) return e.getValue();
		return null;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	public static int getPlayerVote(Player p) {
		return getPlayerMap(p).getVote().get(p);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Player getPlayerWithMaxVote(Map m) {
		Object[] a = m.getVote().entrySet().toArray();
		Arrays.sort(a, new Comparator() {
		    public int compare(final Object o1, final Object o2) {
		        return ((Entry<Player, Integer>) o2).getValue()
		                   .compareTo(((Entry<Player, Integer>) o1).getValue());
		    }
		});
		return ((Entry<Player, Integer>) a[0]).getKey();
	}
}
