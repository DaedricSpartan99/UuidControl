package atlas.numixe;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class UuidControl extends JavaPlugin implements Listener {
	
	HashMap<String, String> map = new HashMap<String, String>();
	public static final String LOGHEAD = "§2UuidControl> ";

	public void onEnable() {
		
		load();
	}
	
	public void load() {
		
		if (!getConfig().contains("players"))
			return;
		
		for (String name : getConfig().getConfigurationSection("players").getKeys(false))
			map.put(name, getConfig().getString("players." + name));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		
		if (uuid == null)
			return;
		
		if (!verify(p, uuid))	// se verify restituisce false
			p.kickPlayer(LOGHEAD + "Uuid non conforme a quello originale");
	}
	
	public boolean verify(Player p, UUID uuid) {
		
		return map.get(p.getName()).equals(uuid.toString());
	}
	
	public void write(Player p) {
		
		if (!getConfig().contains("players"))
			getConfig().createSection("players");
		
		if (map.containsKey(p.getName()) || getConfig().contains("players." + p.getName())) {
			
			p.sendMessage(LOGHEAD + "§aSei gia' un'utente protetto");
			return;
		}
		
		UUID uuid = p.getUniqueId();
		
		if (uuid == null)
			return;
		
		getConfig().createSection("players." + p.getName());
		getConfig().set("players." + p.getName(), uuid.toString());
		map.put(p.getName(), uuid.toString());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(LOGHEAD + "§aSolo un player puo' accedere a questo comando");
			return false;
		}
		
		// !! da impostare i permessi di admin !!
		
		Player p = (Player) sender;
		
		if (cmd.getName().equalsIgnoreCase("viewuuid)")) {
			
			if (map.containsKey(p.getName())) {
				
				p.sendMessage(LOGHEAD + "§7Utente admin protetto");
				p.sendMessage(LOGHEAD + "§7Username: §0" + p.getName());
				p.sendMessage(LOGHEAD + "§7UUID: §0" + map.get(p.getName()));
				
			} else {
				
				p.sendMessage(LOGHEAD + "§7Utente admin non protetto");
				p.sendMessage(LOGHEAD + "§7Current Username: §0" + p.getName());
				
				UUID uuid = p.getUniqueId();
				String uuids = "null";
				
				if (uuid != null)
					uuids = uuid.toString();
				
				p.sendMessage(LOGHEAD + "§7Current UUID: §0" + uuids);
			}
		}
		
		else if (cmd.getName().equalsIgnoreCase("setuuid"))
			write(p);
		
		return true;
	}
}
