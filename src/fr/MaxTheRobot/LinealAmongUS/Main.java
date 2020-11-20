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
import org.bukkit.SkullType;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.MaxTheRobot.LinealAmongUS.Object.Map;
import fr.MaxTheRobot.LinealAmongUS.Object.Vent;
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
	private static List<SaveFile> saveFiles;
	private List<String> saveFilesNames;
	private boolean b = true;
	private static Main instance;
	
	public static ItemStack report = Item.fromMat(Material.BONE).setName("§eReport").setEnchented().setLore("§cClic droit pour report un corps").toItem();
	public static ItemStack sabotage = Item.fromMat(Material.REDSTONE_COMPARATOR).setName("§cSabotage").setLore("§cClic droit pour saboter !").toItem();
	
	public static Inventory ventInventory;
	
	@Override
	public void onEnable() {
		System.out.println("[LinealAmongUS] Plugin ON !");
		Bukkit.getPluginManager().registerEvents(this, this);
		
		getDataFolder().mkdir();
		saveFolder = new File(getDataFolder() + "/save/");
		saveAPI = new SaveAPI(saveFolder);
		saveFiles = new ArrayList<>();
		saveFilesNames = new ArrayList<String>();
		saveFolder.mkdir();
		if(createFiles(Arrays.asList("map"))) {
			System.out.println("[LinealAmongUS] SaveFile creation complete !");
		} else System.out.println("[LinealAmongUS] SaveFile already exists or creation failed !");
		setup();
		
		List<String> filesNames = new ArrayList<String>();
		getSaveFile("map").getContent().forEach(l -> filesNames.add(l.split(",")[0]));
		if(createFiles(filesNames)) {
			System.out.println("[LinealAmongUS] SaveFile creation complete !");
		} else System.out.println("[LinealAmongUS] SaveFile already exists or creation failed !");
		setup();
		
		maps = Config.getMap(getSaveFile("map"));
		
		getCommand("vote").setExecutor(this);
		getCommand("admin").setExecutor(this);
		
		instance = this;
		new Scoreboard(this);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().teleport(new Location(Main.getWorld("world"), 1.5, 6, 28.5, 180, 0));
		e.getPlayer().getInventory().clear();
		e.getPlayer().showPlayer(this, e.getPlayer());;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(e.getClickedBlock() != null) {
			if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(e.getClickedBlock().getState() instanceof Sign) {
					Sign s = (Sign) e.getClickedBlock().getState();
					String mapName = s.getLine(1).substring(4);
					Map m = getMapByName(mapName);
					e.getPlayer().sendMessage(m.getSpawn().toString());
					try {
						e.getPlayer().teleport(m.getSpawn());
					} catch (Exception e2) {
						Location l = m.getSpawn();
						l.setWorld(getWorld(mapName));
						e.getPlayer().teleport(l);
					}
					Role r = setRole(m);
					if(r.equals(Role.impostor)) m.setImpostor(e.getPlayer());
					m.getPlayers().put(e.getPlayer(), r);
					
					if(m.getPlayers().size() == 2) {
						m.setStatus(Status.STARTING);
						new StartingTask(m).runTaskTimer(this, 0, 20);
					}
				} else if(e.getClickedBlock().getState() instanceof Skull) {
					Skull s = (Skull) e.getClickedBlock();
					if(s.getSkullType().equals(SkullType.PLAYER)) {
						if(s.getOwner().equals("Push_red_button")) {
							getPlayerMap(e.getPlayer()).setStatus(Status.VOTING);
							getPlayers(getPlayerMap(e.getPlayer())).forEach(p -> { p.sendTitle("§cEmergency meating !", "", 1, 60, 1); p.sendMessage("§l§aLe chat a été activé !");});
							new vottingTask(getPlayerMap(e.getPlayer())).runTaskTimer(this, 0, 20);
						}
					}
				} else if(e.getClickedBlock().getState() instanceof Button) {
					//task detetection here
				}
			}
		}
		if(getPlayerMap(e.getPlayer()) != null) {
			if(e.getAction().equals(Action.RIGHT_CLICK_AIR)) {
				if(e.getPlayer().getInventory().getItemInMainHand().equals(report)) {
					getPlayerMap(e.getPlayer()).setStatus(Status.VOTING);
					getPlayers(getPlayerMap(e.getPlayer())).forEach(p -> { p.sendTitle("§cUn corps a été trouvé !", "", 1, 60, 1); p.sendMessage("§l§aLe chat a été activé !");});
					new vottingTask(getPlayerMap(e.getPlayer())).runTaskTimer(this, 0, 20);
				} else if(e.getPlayer().getInventory().getItemInMainHand().equals(sabotage)) {
					e.getPlayer().sendMessage("§cSoon....");
				}
			} 
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		if(getPlayerMap(e.getPlayer()) == null) { e.setCancelled(true); return; }
		if(getPlayerMap(e.getPlayer()).getDeathPlayers().contains(e.getPlayer())) { e.getPlayer().sendMessage("§cVous ête mort. Vous ne pouvez pas parler"); e.setCancelled(true); return; }
		if(!getPlayerMap(e.getPlayer()).getStatus().equals(Status.VOTING)) {
			e.setCancelled(true);
			e.getPlayer().sendMessage("§cLe chat est désactivé !");
			return;
		}
		getPlayerMap(e.getPlayer()).getPlayers().keySet().forEach(p -> p.sendMessage("§7" + e.getPlayer().getDisplayName() + " > " + e.getMessage()));
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent e) {
		Player p = e.getPlayer();
		if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.IRON_TRAPDOOR) && getPlayerMap(p).getImpostor().equals(p)) {
			p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255));
			for(Vent v : getPlayerMap(p).getVents()) {
				if(v.getLocation().getWorld() == null) {
					v.getLocation().setWorld(getWorld(getPlayerMap(p).getName()));
				}
				if(isLocSimilary(p.getLocation(), v.getLocation())) {
					Inventory inv = Bukkit.createInventory(null, 9, "§cVent - " + v.getName());
					for(int i = 0; i < inv.getSize(); i++) inv.setItem(i, Item.create(Material.STAINED_GLASS_PANE, 15).setName("§r").toItem());
					for(String s : v.getVents()) inv.addItem(Item.create(Material.WOOD, 14).setName("§e" + s).setEnchented().setLore("§cClic pour se téléporter").toItem());
					p.openInventory(inv);
					p.setSneaking(false);
				}
			}
		}
	}
	
	@EventHandler
	public void onClic(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory().getName().startsWith("§cVent - ")) {
			if(!e.getCurrentItem().getType().equals(Material.WOOL)) return;
			ItemStack it = e.getCurrentItem();
			for(String v : getPlayerMap(p).getVent(e.getInventory().getName().substring(9)).getVents()) {
				if(v.equals(it.getItemMeta().getDisplayName().substring(2))) {
					Location loc = getPlayerMap(p).getVent(v).getLocation().add(0, 1, 0);
					loc.setWorld(getWorld(getPlayerMap(p).getName()));
					p.teleport(loc);
					p.removePotionEffect(PotionEffectType.INVISIBILITY);
					p.closeInventory();
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if(e.getInventory().getName().startsWith("§cVent - ")) {
			e.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		try {
			if(e.getTo().getWorld().equals(Main.getWorld("world"))) {
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
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		if(e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;
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
				kill(v, p);
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
		if(command.getName().equals("admin")) {
			if(!p.isOp()) return true;
			if(args.length == 0) { p.sendMessage("§cSpecify a argument !"); return true; }
			if(args.length == 2) {
				if(args[0].equals("sign")) {
					Bukkit.dispatchCommand(p, 
							"give " + p.getName() + " minecraft:sign 1 0 {BlockEntityTag:{Text1:\"{\\\"text\\\":\\\"Map :\\\",\\\"bold\\\":true}\",Text2:\"{\\\"text\\\":\\\"MAPNAME\\\",\\\"bold\\\":true,\\\"color\\\":\\\"yellow\\\"}\",Text3:\"{\\\"text\\\":\\\"\\\"}\",Text4:\"{\\\"text\\\":\\\"[clic to join]\\\",\\\"bold\\\":true,\\\"color\\\":\\\"dark_red\\\"}\"},display:{Name:\"MAPNAME sign\"}}"
							.replace("MAPNAME", args[1]));
				} else if(args[0].equals("tp")) {
					p.teleport(getMapByName(args[1]).getSpawn());
				}
			} else if(args.length == 1) {
				if(args[0].equals("block")) {
					p.sendMessage("Le block que vous regarder est en " + stringfromloc(p.getEyeLocation()));
				}
			}
		} else if(command.getName().equals("vote")) {
			Map m = getPlayerMap(p);
			if(m == null) return true;
			if(m.getStatus().equals(Status.VOTING) && !getPlayerRole(p).equals(Role.dead)) {
				if(args.length == 0) {
					p.sendMessage("§cSpecify a player !");
				} else if(args.length == 1) {
					Player v = Bukkit.getPlayer(args[0]);
					if(v == null) { p.sendMessage("§cCe joueur n'est pas en ligne !"); return true; }
					Map vm = getPlayerMap(v);
					if(vm.equals(m)) {
						if(m.getDeathPlayers().contains(v)) {
							p.sendMessage("§cCe joueur est mort !");
							return true;
						}
						if(m.getVote().containsKey(v)) {
							p.sendMessage("§cVous avez déjà voté !");
							return true;
						}
						p.sendMessage("§aVous avez voté pour " + v.getName());
						if(!m.getVote().containsKey(v)) m.getVote().put(v, 0);
						m.getVote().replace(v, m.getVote().get(v), m.getVote().get(v) + 1);
					} else p.sendMessage("§cCe joueur n'est pas dans la meme partie que vous !");
				}
			} else p.sendMessage("§cVous ne pouvez pas voter maintenant !");
		} else if(command.getName().equals("leave")) {
			if(getPlayerMap(p) == null) {
				p.sendMessage("§cVous n'etes pas dans une partie !");
				return true;
			}
			p.getInventory().clear();
			List<PotionEffectType> gs = new ArrayList<PotionEffectType>();
			for(PotionEffect g : p.getActivePotionEffects()) gs.add(g.getType());
			for(PotionEffectType g : gs) p.removePotionEffect(g);
			if(getImpostor(getPlayerMap(p)).equals(p)) {
				win(getPlayerMap(p), Role.crewmate);
			} else {
				getPlayerMap(p).getPlayers().remove(p);
				getPlayers(getPlayerMap(p)).forEach(a -> a.sendMessage("§a" + p.getName() + " a quitté la partie !"));
			}
		}
		return true;
	}
	
	public static void kill(Player p, Player killer) {
		getPlayerMap(p).getDeathPlayers().add(p);
		getPlayerMap(p).getPlayers().remove(p);
		p.setGameMode(GameMode.CREATIVE);
		p.sendTitle("§cVous êtes mort !", "Vous avez été tué par " + killer.getName(), 1, 1, 1);
		p.getInventory().clear();
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, true));
		getPlayerMap(p).getPlayers().replace(p, getPlayerRole(p), Role.dead);
	}
	
	public static void win(Map m, Role r) {
		Bukkit.broadcastMessage("§cGagnant : " + r.name());
		m.getSkeletons().forEach(s -> ((Skeleton) s).setHealth(0));
		getPlayers(m).forEach(a -> a.showPlayer(instance, a));
		for(Player p : getPlayers(m)) for(Player v : getPlayers(m)) p.showPlayer(instance, v);
		m.getPlayers().clear();
		m.getDeathPlayers().clear();
		m.setStatus(Status.WAITING);
		
		new BukkitRunnable() {
			
			int i = 7;
			@Override
			public void run() {
				if(i == 0) {
					m.getDeathPlayers().forEach(p -> {
						p.teleport(new Location(Main.getWorld("world"), 1.5, 6, 28.5, 180, 0));
					});
					m.getPlayers().keySet().forEach(p -> {
						p.teleport(new Location(Main.getWorld("world"), 1.5, 6, 28.5, 180, 0));
					});
					this.cancel();
				}
				getPlayers(m).forEach(p -> p.sendMessage("§cTéléportation dans " + i));
				i--;
			}
		};
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
		names.forEach(n -> saveFilesNames.add(n));
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
	
	public static SaveFile getSaveFile(String name) {
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
		if(m.getPlayers().size() == 1 && innocent == 1) r = Role.impostor;
		return r;
	}
	
	public static Set<Player> getPlayers(Map m){
		return m.getPlayers().keySet();
	}
	
	public static Player getImpostor(Map m) {
		return m.getImpostor();
	}
	
	public static Role getPlayerRole(Player p) {
		for(Entry<Player, Role> e : getPlayerMap(p).getPlayers().entrySet()) if(e.getKey().equals(p)) { return e.getValue(); };
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
	
	public static Location locfromstring(String s, String w) {
		return new Location(Main.getWorld(w), Integer.parseInt(s.split(",")[0]), Integer.parseInt(s.split(",")[1]), Integer.parseInt(s.split(",")[2]));
	}
	
	public String stringfromloc(Location l) {
		return l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ();
	}
	
	public boolean isLocSimilary(Location loc1, Location loc2) {
		return loc1.getBlock().equals(loc2.getBlock());
	}
	
	public static World getWorld(String name) {
		for(World w : Bukkit.getWorlds()) {
			if(w.getName().equals(name)) {
				return w;
			}
		}
		return null;
	}
	
	public static List<Map> getMaps() {
		return maps;
	}
}
