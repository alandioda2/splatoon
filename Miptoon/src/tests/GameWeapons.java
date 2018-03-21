package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.md_5.bungee.api.ChatColor;

public class GameWeapons {
	
	//not implemented
	
	Plugin plugin;
	
	int lounchMultiplex = 1;
	Map<Integer, Integer> schID = new HashMap<Integer, Integer>();
	Map<String, Integer> playerTeam = new HashMap<String, Integer>();
	List<String> squidPlayers = new ArrayList<String>();
	Map<String, SplMenu> playerMenu = new HashMap<String, SplMenu>();
	
	@EventHandler
	public void onPlayerInteract (PlayerInteractEvent e){
		Player player = e.getPlayer();
		ItemStack is = player.getInventory().getItemInMainHand();
		if(!is.getType().toString().equalsIgnoreCase("AIR")) {
			if(is.getType().equals(Material.INK_SACK)) {
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					spawnBlock(player);
					e.setCancelled(true);
				}
			} else if(is.getType().equals(Material.CLAY_BALL)) {
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					TransformSquid(player);
					e.setCancelled(true);
				}
			} else if(is.getType().equals(Material.GOLD_RECORD)) {
				if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
					ShootColor(player, 1.2f, 40, 50);
					e.setCancelled(true);
				}
			}
		}
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
			if(playerMenu.containsKey(player.getName())) {
				e.setCancelled(true);
				ExitMenu(player);
			}
		}
	}
	
	@EventHandler
	void onPlayerInteractAtEntityEvent (PlayerInteractAtEntityEvent e) {
		Player player = e.getPlayer();
		if(e.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
			if(playerMenu.containsKey(player.getName())) {
				e.setCancelled(true);
				ExitMenu(player);
			}
		}
	}
	
	private void TransformSquid(Player player) {
		if(!squidPlayers.contains(player.getName())) {
			squidPlayers.add(player.getName());
			player.sendMessage(ChatColor.DARK_PURPLE + "You're now a squid!");
			player.setWalkSpeed(0.4f);
			MobDisguise squid = new MobDisguise(DisguiseType.SQUID);
			squid.setReplaceSounds(true);
			squid.setViewSelfDisguise(true);
			DisguiseAPI.disguiseEntity(player, squid);
		} else {
			squidPlayers.remove(player.getName());
			player.setWalkSpeed(0.2f);
			player.sendMessage(ChatColor.DARK_PURPLE + "You're not a squid anymore!");
			DisguiseAPI.undisguiseToAll(player);
		}
	}
	
	void EnterMenu (Player player, String name) {
		if(!playerMenu.containsKey(player.getName())) {
			SplMenu menu = new SplMenu();
			menu.menuName = name;
			playerMenu.put(player.getName(), menu);
			player.teleport(new Location(player.getWorld(), player.getLocation().getBlock().getX(), player.getLocation().getBlock().getY(), player.getLocation().getBlock().getZ(), 0f, 0f));
			menu.playerLocation = player.getLocation();
			menu.gamemode = player.getGameMode();
			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(ChatColor.GOLD + "Select an item!");
			if(name.equals("Team")) {
				OpenMenu(menu, CreatListOfTeamItems());
			}
		}
	}
	
	private List<ItemStack> CreatListOfTeamItems() {
		List<ItemStack> l = new ArrayList<ItemStack>();
		for(int i = 1; i < 15; i++) {
			l.add(this.CreateItem(Material.CONCRETE, i));
		}
		return l;
	}

	private void OpenMenu(SplMenu menu, List<ItemStack> items) {
		int s = items.size();
		float size = 1.6f;
		int j = 0;
		for(int i = 0; i < 360; i += (360/s)) {
			double x = Math.sin(Math.toRadians(i)) * size;
			double z = Math.cos(Math.toRadians(i)) * size;
			Location loc = new Location(menu.playerLocation.getWorld(), x + menu.playerLocation.getX(), menu.playerLocation.getY() + (-0.1f), z + menu.playerLocation.getZ(), -(float)i, 0f);
			ArmorStand en = (ArmorStand)menu.playerLocation.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
			en.setVisible(false);
			en.setSmall(true);
			en.setGravity(false);
			en.setHelmet(items.get(j));
			en.setCustomName(ChatColor.GREEN + "Weapon " + j);
			en.setCustomNameVisible(false);
			j++;
			menu.holders.add(en);
		}
		UpdateMenu(menu, 0f);
	}

	void ExitMenu (Player player) {
		if(playerMenu.containsKey(player.getName())) {
			SplMenu menu = playerMenu.get(player.getName());
			player.setGameMode(menu.gamemode);
			String name = menu.menuName;
			for(ArmorStand as : menu.holders) {
				as.remove();
			}
			if(name.equals("Team")) {
				int i = getIndex(player);
				@SuppressWarnings("deprecation")
				int number = (int)menu.holders.get(i).getHelmet().getData().getData();
				ChangeTeam(number, player);
			}
			playerMenu.remove(player.getName());
		}
	}
	
	private void ChangeTeam (int number, Player player) {
		if(playerTeam.containsKey(player.getName())) {
			playerTeam.remove(player.getName());
		}
		playerTeam.put(player.getName(), number);
	}
	
	private void UpdateMenu(Player player) {
		SplMenu menu = playerMenu.get(player.getName());
		float playerRotation = -player.getLocation().getYaw();
		UpdateMenu(menu, playerRotation);
	}
	
	private void UpdateMenu(SplMenu menu, float playerRotation) {
		float difference = (360 / menu.holders.size());
		int number = Math.abs(Math.round(((playerRotation < 0)?(360 + playerRotation):(playerRotation)) / difference));
		number = ((number >= menu.holders.size())?(0):(number));
		for(int i = 0; i < menu.holders.size(); i++) {
			if(i == number) {
				menu.holders.get(i).setCustomNameVisible(true);
			} else {
				menu.holders.get(i).setCustomNameVisible(false);
			}
		}
		int n = 0;
		for(int i = 0; i < 360; i += difference) {
			n++;
			float size = ((n == number)?(1.2f):(1.6f));
			float y = ((n == number)?(0.1f):(-0.1f));
			Bukkit.broadcastMessage(n + ", " + number);
			double x = Math.sin(Math.toRadians(i)) * size;
			double z = Math.cos(Math.toRadians(i)) * size;
			Location loc = new Location(menu.playerLocation.getWorld(), x + menu.playerLocation.getX(), menu.playerLocation.getY() + y, z + menu.playerLocation.getZ(), -(float)i, 0f);
			menu.holders.get(n).teleport(loc);
		}
	}
	
	int getIndex (Player player) {
		float playerRotation = -player.getLocation().getYaw();
		SplMenu menu = playerMenu.get(player.getName());
		float difference = (360 / menu.holders.size());
		int number = Math.abs(Math.round(((playerRotation < 0)?(360 + playerRotation):(playerRotation)) / difference));
		return ((number >= menu.holders.size())?(0):(number));
	}

	@EventHandler
	public void onEntityChnage (EntityChangeBlockEvent e){
		if(e.getEntityType().equals(EntityType.FALLING_BLOCK) && e.getTo().equals(Material.CONCRETE)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onItemMerge (ItemMergeEvent e){
		if(e.getEntityType().equals(EntityType.DROPPED_ITEM)) {
			Item i = (Item)e.getEntity();
			if(i.getItemStack().getType().equals(Material.CONCRETE)) {
				e.setCancelled(true);
			}
		}
	}
	
	void ShootColor (Player player, float range, int amount, int veriation) {
		if(!playerTeam.containsKey(player.getName())) {
			playerTeam.put(player.getName(), 0);
		}
		float p = player.getLocation().getPitch();
		float y = player.getLocation().getYaw();
		float yF = (float) (Math.sin(Math.toRadians((-p))) * range);
		float fF = (float) (Math.cos(Math.toRadians((-p))) * range);
		float xF = (float) (Math.sin(Math.toRadians((-y))) * fF);
		float zF = (float) (Math.cos(Math.toRadians((y))) * fF);
		ItemStack item = CreateItem(Material.CONCRETE, playerTeam.get(player.getName()));
		Random rx = new Random();
		Random ry = new Random();
		Random rz = new Random();
		for(int i = 0; i < amount; i++) {
			Item it = player.getWorld().dropItem(player.getEyeLocation(), item);
			int plx = rx.nextInt(veriation) - (veriation/2);
			int ply = ry.nextInt(veriation) - (veriation/2);
			int plz = rz.nextInt(veriation) - (veriation/2);
			it.setVelocity(new Vector(xF + plx/100f, yF + ply/100f, zF + plz/100f));
			Timer2(it);
		}
	}

	void spawnBlock (Player player) {
		if(!playerTeam.containsKey(player.getName())) {
			playerTeam.put(player.getName(), 0);
		}
		int id = playerTeam.get(player.getName());
		Material m = Material.CONCRETE;
		@SuppressWarnings("deprecation")
		FallingBlock block = player.getWorld().spawnFallingBlock(player.getLocation().clone().add(0, 1, 0), m, (byte) id);
		float p = player.getLocation().getPitch();
		float y = player.getLocation().getYaw();
		float yF = (float) (Math.sin(Math.toRadians((-p))) * lounchMultiplex);
		float fF = (float) (Math.cos(Math.toRadians((-p))) * lounchMultiplex);
		float xF = (float) (Math.sin(Math.toRadians((-y))) * fF);
		float zF = (float) (Math.cos(Math.toRadians((y))) * fF);
		block.setVelocity(new Vector(xF, yF, zF));
		Timer(block, id, m);
	}
	
	void PaintRadius (int radius, Location impact, int id, Material m) {
		Location startLocation = impact.clone().add(-radius, -radius, -radius);
		for(int x = 0; x < radius*2+1; x++) {
			for(int y = 0; y < radius*2+1; y++) {
				for(int z = 0; z < radius*2+1; z++) {
					ChangeBlock(impact, startLocation.clone().add(x, y, z), radius, id, m);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	void ChangeBlock (Location start, Location main, int r, int id, Material m) {
		Block b = main.getBlock();
		if(isBlockSeen(b.getLocation()) && !b.getType().equals(Material.AIR) && !(b.getType().equals(Material.CONCRETE) && b.getData() == (byte) id) && get3DRadius(b.getLocation(), start) <= r) {
			b.setType(m);
			b.setData((byte) id);
		}
	}
	
	@SuppressWarnings("deprecation")
	void ChangeBlock (Location loc, int id, Material m) {
		Block b = loc.getBlock();
		b.setType(m);
		b.setData((byte) id);
	}
	
	private void Timer2(Item i) {
		int a = GetUniqueID(schID);
		schID.put(a, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int taskN = a;
			Item item = i;
			int n = 20*20;
		 	@SuppressWarnings("deprecation")
			public void run() {
		 		if(n <= 0 || item == null) {
		 			item.remove();
		 			ShutDownScheduler(taskN, schID);
		 		} else {
		 			if(isBlockNear(item.getLocation().clone())) {
		 				PaintSides(item.getLocation().getBlock().getLocation(), item.getItemStack().getType(), (int)item.getItemStack().getData().getData());
		 				item.remove();
		 				ShutDownScheduler(taskN, schID);
		 			}
		 			n--;
		 		}
			}
		}, 0, 1));
	}
	
	private void Timer(FallingBlock fb, int id, Material m) {
		int a = GetUniqueID(schID);
		schID.put(a, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			int taskN = a;
			FallingBlock b = fb;
			int n = 20*20;
			int ids = id;
			Material ms = m;
		 	public void run() {
		 		if(n <= 0 || b == null) {
		 			ShutDownScheduler(taskN, schID);
		 		} else {
		 			if(isBlockNear(b.getLocation().clone())) {
		 				PaintRadius(5, b.getLocation().clone().getBlock().getLocation(), ids, ms);
		 				b.remove();
		 				ShutDownScheduler(taskN, schID);
		 			}
		 			n--;
		 		}
			}
		}, 0, 1));
	}
	
	Map<String, ArrayList<Block>> vineMap = new HashMap<String, ArrayList<Block>>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (squidPlayers.contains(player.getName())) {
			BlockFace bf = yawToFace(player.getLocation().getYaw());
			Block block = player.getLocation().getBlock().getRelative(bf);
			if (block.getType() != Material.AIR) {
				for (int i = 0; i < 300; i++) {
					Block opp = player.getLocation().add(0.0D, i, 0.0D).getBlock();
					Block aboveOpp = opp.getLocation().add(0.0D, 1.0D, 0.0D).getBlock();
					if ((opp.getType() != Material.AIR) && (opp.getType() != Material.LONG_GRASS) && (opp.getType() != Material.YELLOW_FLOWER) && (opp.getType() != Material.RED_ROSE)) {
						break;
					}
					if (aboveOpp.getType() == Material.AIR) {
						player.sendBlockChange(opp.getLocation(), Material.VINE, (byte)0);
						addVines(player, opp);
					}
					player.setFallDistance(0.0F);
				}
			} else {
				for (int i = 0; i < getVines(player).size(); i++) {
					player.sendBlockChange(((Block)getVines(player).get(i)).getLocation(), Material.AIR, (byte)0);
				}
				getVines(player).clear();
			}
		}
		
		if(playerMenu.containsKey(player.getName())) {
			UpdateMenu(player);
			if(IsChanedPosition(event.getFrom(), event.getTo())) {
				event.setCancelled(true);
			}
		}
		
	}

	public BlockFace yawToFace(float yaw) {
		BlockFace[] axis = { BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST };
		return axis[(Math.round(yaw / 90.0F) & 0x3)];
	}
	
	public void addVines(Player player, Block vine) {
		ArrayList<Block> updated = new ArrayList<Block>();
	    updated = getVines(player);
	    updated.add(vine);
	    setVines(player, updated);
	}
	  
	public ArrayList<Block> getVines(Player player) {
		if (this.vineMap.containsKey(player.getName())) {
			return (ArrayList<Block>)this.vineMap.get(player.getName());
		}
		ArrayList<Block> temp = new ArrayList<Block>();
	    return temp;
	}
	
	public void setVines(Player player, ArrayList<Block> vines) {
	    this.vineMap.put(player.getName(), vines);
	}
	
	//functions
	void ShutDownScheduler(int id, Map<Integer, Integer> map) {
		int schID = map.get(id);
		map.remove(id);
		Bukkit.getServer().getScheduler().cancelTask(schID);
	}
	
	@SuppressWarnings("deprecation")
	void PaintSides(Location loc, Material m, int id) {
		for(int i = 0; i < 6; i++) {
			Block b = getBlockBside(loc, i).getBlock();
			if(!b.getType().equals(Material.AIR) && !(b.getType().equals(Material.CONCRETE) && b.getData() == (byte)id)) {
				ChangeBlock(b.getLocation(), id, m);
			}
		}
	}
	
	private ItemStack CreateItem(Material material, int id) {
		return new ItemStack(material, 1, (byte)id);
	}
	
	boolean IsChanedPosition (Location from, Location to) {
		if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()) {
			return true;
		} else {
			return false;
		}
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
	
	private boolean isBlockSeen (Location bL) {
		for(int a = 0; a < 6; a++) {
			Block b = getBlockBside(bL, a).getBlock();
			if(b.getType().equals(Material.AIR)) {
				return true;
			}
		}
		return false;
	}
	
	private double get3DRadius (Location loc1, Location loc2) {
		double x = Math.abs(loc1.getX() - loc2.getX());
		double y = Math.abs(loc1.getY() - loc2.getY());
		double z = Math.abs(loc1.getZ() - loc2.getZ());

		double q1 = Math.sqrt(x*x + y*y);
		double q2 = Math.sqrt(q1*q1 + z*z);
		
		return q2;
	}
	
	private boolean isBlockNear (Location targetPos) {
		for(int a = 0; a < 6; a++) {
			Block b = getBlockBside(targetPos.getBlock().getLocation(), a).getBlock();
			if(!b.getType().equals(Material.AIR)) {
				return true;
			}
		}
		return false;
	}
	
	private Location getBlockBside (Location l, int n) {
		if(n == 0) {
			return l.clone().add(1, 0, 0);
		} else if(n == 1) {
			return l.clone().add(0, 1, 0);
		} else if(n == 2) {
			return l.clone().add(0, 0, 1);
		} else if(n == 3) {
			return l.clone().add(-1, 0, 0);
		} else if(n == 4) {
			return l.clone().add(0, -1, 0);
		} else if(n == 5) {
			return l.clone().add(0, 0, -1);
		} else {
			return l.clone();
		}
	}
}
