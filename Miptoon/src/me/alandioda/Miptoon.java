package me.alandioda;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;

public class Miptoon extends JavaPlugin implements Listener {
	
	Map<String, SplGame> games = new HashMap<String, SplGame>();
	Map<String, String> playersInGame = new HashMap<String, String>();
	
	public Permission adminPermissions = new Permission("Miptoon.admin.commands");
	Map<Integer, Integer> schID = new HashMap<Integer, Integer>();
	FileConfiguration config;
	
	String serverPrefex = ChatColor.GOLD.toString() + ChatColor.BOLD + "Mip" + ChatColor.YELLOW.toString() + ChatColor.BOLD  + "Craft";
	
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);		
		PluginManager pm = getServer().getPluginManager();	
		pm.addPermission(adminPermissions);
		config = getConfig();
		config.options().copyDefaults(true);
		config.addDefault("games", new ArrayList<String>());
		saveConfig();
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			int length = args.length;
			if(cmd.getName().equalsIgnoreCase("spl") || cmd.getName().equalsIgnoreCase("Splatoon") || cmd.getName().equalsIgnoreCase("Miptoon")) {
				if(length == 1) {
					if(args[0].equalsIgnoreCase("help")) {
						player.sendMessage(ChatColor.RED + "--------------Help--------------");
						player.sendMessage(ChatColor.RED + "/spl join" + ChatColor.GOLD + " join game");
						player.sendMessage(ChatColor.RED + "/spl leave" + ChatColor.GOLD + " leave game");
						if(!player.hasPermission(adminPermissions)) {
							return true;
						}
					} else if(args[0].equalsIgnoreCase("join")) { //join
						
						if(!player.hasPermission(adminPermissions)) {
							return true;
						}
					}
				}
				if(player.hasPermission(adminPermissions)) {
					if(length == 0) {
						player.sendMessage(ChatColor.RED + "MipToon");
						player.sendMessage(ChatColor.RED + "Version " + getDescription().getVersion());
						player.sendMessage(ChatColor.RED + "Craetaed by " + ChatColor.DARK_PURPLE + "MIP CRAFT TEAM");
						player.sendMessage(ChatColor.RED + "Type /spl help for help");
						return true;
					} else if(length == 1) {
						if(args[0].equalsIgnoreCase("help")) {
							player.sendMessage(ChatColor.RED + "/spl create (game name)" + ChatColor.GOLD + " create game");
							return true;
						} else if(args[0].equalsIgnoreCase("create")) {
							
							return true;
						}
					} else {
						player.sendMessage(serverPrefex + ChatColor.WHITE + "command not found.");
					}
				} else {
					player.sendMessage(serverPrefex + ChatColor.WHITE + " command not found.");
				}
				return true;
			}
		}
		return false;
	}
	
	void StartGame (SplGame game) {
		int a = GetUniqueID(schID);
		schID.put(a, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			int taskN = a;
			int n = 30;
			public void run() {
		 		if(n <= 0) {
		 			CreateGameBoard(game);
		 			for(Player p : Bukkit.getOnlinePlayers()) {
		 				sendTitle(p, ChatColor.RED + "GO!", 20, 40, 20);
	    				p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1);
	    				p.setScoreboard(game.gameBoard);
		 			}
		 			GameTime(game);
		 			ShutDownScheduler(taskN, schID);
		 		} else {
		 			for(Player p : Bukkit.getOnlinePlayers()) {
		 				sendTitle(p, ChatColor.GOLD + "Game starts in " + n, 20, 40, 20);
	    				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BELL, 1, 1);
		 			}
		 			n--;
		 		}
			}
		}, 0, 20));
	}
	
	void GameTime (SplGame game) {
		int a = GetUniqueID(schID);
		schID.put(a, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			int taskN = a;
			int n = 60*7; //change to 7
			public void run() {
		 		if(n <= 0) {
		 			for(Player p : Bukkit.getOnlinePlayers()) {
		 				sendTitle(p, ChatColor.RED + "End!", 20, 40, 20);
	    				p.playSound(p.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 1, 1);
		 			}
		 			Finish();
		 			ShutDownScheduler(taskN, schID);
		 		} else {
		 			UpdateGameBoard(game);
		 			n--;
		 		}
			}
		}, 0, 20));
	}
	
	void Finish () {
		for(Player p : Bukkit.getOnlinePlayers()) {
				sendTitle(p, ChatColor.GOLD + "Team Won", 20, 120, 20);
				p.setGameMode(GameMode.CREATIVE); //tp to loc... just enable fly and leave them in adventure or surv.
				p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		}
		int a = GetUniqueID(schID);
		schID.put(a, Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			int taskN = a;
			int n = 20;
			public void run() {
		 		if(n <= 0) {
		 			CloseGame();
		 			ShutDownScheduler(taskN, schID);
		 		} else {
		 			for(Player p : Bukkit.getOnlinePlayers()) {
		 				sendActionBar(p, ChatColor.WHITE + "ending in " + n, 20, 40, 20);
		 			}
		 			n--;
		 		}
			}
		}, 0, 20));
	}
	
	void CloseGame () {
		for(Player p : Bukkit.getOnlinePlayers()) { //reset players
			p.setGameMode(GameMode.ADVENTURE); //tp to loc...
		}
	}
	
	//board
	
	void CreateGameBoard(SplGame game) {
		game.gameBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		game.obj = game.gameBoard.registerNewObjective("GameNameBoard", "dummy"); //make for every game (name)
	    game.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
	    game.obj.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD + "Miptoon");
	    
	    game.timeDispl = game.gameBoard.registerNewTeam("timer");
	    game.timeDispl.addEntry(ChatColor.GREEN.toString());
	    game.timeDispl.setPrefix(ChatColor.GOLD + "Time: ");
	    game.timeDispl.setSuffix(ChatColor.GREEN + "0:0");
	    
	    game.playersDispl = game.gameBoard.registerNewTeam("players");
	    game.playersDispl.addEntry(ChatColor.RED.toString());
	    game.playersDispl.setPrefix("");
	    game.playersDispl.setSuffix(ChatColor.GOLD + "❤❤❤❤ ❤❤❤❤");
	    
	    game.obj.getScore(ChatColor.GREEN.toString()).setScore(4);
	    game.obj.getScore(ChatColor.RED.toString()).setScore(2);
	    
	    game.obj.getScore(ChatColor.WHITE.toString() + ChatColor.GREEN.toString()).setScore(5);
	    game.obj.getScore(ChatColor.WHITE.toString() + ChatColor.WHITE.toString()).setScore(3);
	    game.obj.getScore(ChatColor.WHITE.toString()).setScore(1);
	    
	    game.website = game.obj.getScore(ChatColor.BLUE + "mipcraft.eu");
	    game.website.setScore(0);
	}
	
	void UpdateGameBoard(SplGame game) {
		int time = game.gameTime;
	    int min = (int)(time / 60.0D);
	    int sec = time % 60;
	    game.gameBoard.getTeam("timer").setSuffix(ChatColor.GREEN.toString() + String.format("%02d", new Object[] { Integer.valueOf(min) }) + ":" + String.format("%02d", new Object[] { Integer.valueOf(sec) }));
	    String[] s = {ChatColor.BLACK + "❤", ChatColor.BLACK + "❤", ChatColor.BLACK + "❤", ChatColor.BLACK + "❤", ChatColor.BLACK + "❤", ChatColor.BLACK + "❤", ChatColor.BLACK + "❤", ChatColor.BLACK + "❤"};
	    int team1 = 0;
	    int team2 = 0;
	    for(Entry<String, PlayerData> playerData : game.players.entrySet()) {
	    	if(game.teamColor1 == playerData.getValue().teamColor) {
	    		s[team1] = ((playerData.getValue().isAlive)?(getColor(game.teamColor1) + "❤"):(ChatColor.GRAY + "❤"));
	    		team1++;
	    	} else {
	    		s[team2 + 4] = ((playerData.getValue().isAlive)?(getColor(game.teamColor2) + "❤"):(ChatColor.GRAY + "❤"));
	    		team2++;
	    	}
	    }
	    game.gameBoard.getTeam("players").setSuffix(s[0] + s[1] + s[2] + s[3] + " " + s[4] + s[5] + s[6] + s[7]);
	}
	
	String getColor (int id) {
		if(id == 0) {
			return ChatColor.YELLOW.toString();
		} else if(id == 1) {
			return ChatColor.GOLD.toString();
		} else if(id == 2) {
			return ChatColor.RED.toString();
		} else if(id == 3) {
			return ChatColor.LIGHT_PURPLE.toString();
		} else if(id == 4) {
			return ChatColor.DARK_PURPLE.toString();
		} else if(id == 5) {
			return ChatColor.DARK_BLUE.toString();
		} else if(id == 6) {
			return ChatColor.AQUA.toString();
		} else if(id == 7) {
			return ChatColor.BLUE.toString();
		} else if(id == 8) {
			return ChatColor.GREEN.toString();
		} else {
			return ChatColor.DARK_GREEN.toString();
		}
	}
	
	//saving and loading
	
	void SaveGameToConfig (String gameName) throws IOException {
		SplGame game = games.get(gameName);
		File saveFile = new File(this.getDataFolder() + "/games/" + game.gamePath);
		if(!(saveFile.exists() || saveFile.isDirectory())) {
			saveFile = new File(this.getDataFolder(), "games/" + game.gamePath);
		}
		FileConfiguration gameFile = YamlConfiguration.loadConfiguration(saveFile); // not testing if it's null!!!!!
		if(gameFile != null) {
			gameFile.set("Game_Name", gameName);
			gameFile.set("Bound1", LocationToString(game.Bound1));
			gameFile.set("Bound2", LocationToString(game.Bound2));
			gameFile.set("Lobby_Location", LocationToString(game.Lobby));
			gameFile.set("Team1", LocationToString(game.Team1));
			gameFile.set("Team2", LocationToString(game.Team2));
			gameFile.save(saveFile);
			List<String> games = config.getStringList("games");
			if(!games.contains(gameName)) {
				games.add(gameName);
				config.set("games", games);
				saveConfig();
			}
		}
	}
	
	void LoadGamesFromConfig () throws IOException {
		List<String> gamesL = config.getStringList("games");
		for(String game : gamesL) {
			File loadFile = new File(getDataFolder(), "games/" + game + ".yml");
			getLogger().info("Loading: " + game);
			if(loadFile.exists()) {
				FileConfiguration gameFile = YamlConfiguration.loadConfiguration(loadFile);
				SplGame g = new SplGame();
				g.Bound1 = StringToLocation(gameFile.getString("Bound1"));
				g.Bound2 = StringToLocation(gameFile.getString("Bound2"));
				g.Lobby = StringToLocation(gameFile.getString("Lobby_Location"));
				g.Team1 = StringToLocation(gameFile.getString("Team2"));
				g.Team2 = StringToLocation(gameFile.getString("Team2"));
				g.gamePath = gameFile.getString("Game_Name");
				games.put(g.gamePath, g);
			}
		}
	}
	
	String LocationToString (Location loc) {
		if(loc != null) {
			String location = loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch(); 
			return location;
		} else {
			return null;
		}
	}
	
	Location StringToLocation (String loc) {
		String[] sLoc = loc.split(";");
		if(!loc.equals(null) && sLoc.length > 5) {
			Location location = new Location(Bukkit.getServer().getWorld(sLoc[0]), Double.parseDouble(sLoc[1]), Double.parseDouble(sLoc[2]), Double.parseDouble(sLoc[3]), Float.parseFloat(sLoc[4]), Float.parseFloat(sLoc[5]));
			return location;
		} else {
			return null;
		}
	}
	
	public void sendTitle(Player player, String title, int fadeIn, int stay, int fadeOut) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
    }
	
	public void sendActionBar(Player player, String title, int fadeIn, int stay, int fadeOut) {
        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(EnumTitleAction.ACTIONBAR, ChatSerializer.a("{\"text\":\"" + title + "\"}"), fadeIn, stay, fadeOut);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
    }
	
	private Integer GetUniqueID(Map<Integer, Integer> map) {
		boolean foundID = false;		
	    int id = map.size();
	    while (!foundID) {
	    	if (!map.containsKey(Integer.valueOf(id))) {
	    		foundID = true;
	    		break;
	    	}
	    	id++;
	    }
	    return Integer.valueOf(id);
	}
	
	void ShutDownScheduler(int id, Map<Integer, Integer> map) {
		int schID = map.get(id);
		map.remove(id);
		Bukkit.getServer().getScheduler().cancelTask(schID);
	}
	
	//events
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent e) {
		String playerName = e.getPlayer().getName();
		if(playersInGame.containsKey(playerName)) {
			String gameName = playersInGame.get(playerName);
			games.get(gameName).players.remove(playerName);
		}
	}
	
	@EventHandler
	public void onPlayerKick (PlayerKickEvent e) {
		String playerName = e.getPlayer().getName();
		if(playersInGame.containsKey(playerName)) {
			String gameName = playersInGame.get(playerName);
			games.get(gameName).players.remove(playerName);
		}
	}
	
	public enum GameStage {
		DISABED, WAITING, PLAYING, ENDING
	}
}
