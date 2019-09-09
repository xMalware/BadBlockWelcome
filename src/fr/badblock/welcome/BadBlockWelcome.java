package fr.badblock.welcome;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class BadBlockWelcome extends JavaPlugin implements Listener
{

	private long welcome = 0L;
	private UUID uuid;
	private String address;
	private List<UUID> p = new ArrayList<>();

	@Override
	public void onEnable()
	{
		this.getServer().getPluginManager().registerEvents(this, this);
		this.reloadConfig();
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onLogin(PlayerLoginEvent event)
	{
		if (event.getResult() != null && !event.getResult().equals(Result.ALLOWED))
		{
			return;
		}
		
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		address = event.getAddress().getHostAddress();

		File file = new File("plugins/Essentials/userdata/" + uuid.toString() + ".yml");

		if (!file.exists())
		{
			welcome = System.currentTimeMillis() + 30000L;
			this.uuid = uuid;
			this.p.clear();
			System.out.println("[/] " + player.getName() + " is a new player.");
			
			Bukkit.getOnlinePlayers().stream().filter(p -> !player.getUniqueId().equals(p.getUniqueId())).forEach(p -> p.sendMessage("§dBienvenue à " + player.getName() + ". N'hésite pas à lui souhaiter la bienvenue !"));
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event)
	{
		if (welcome < System.currentTimeMillis())
		{
			return;
		}

		String message = event.getMessage();

		if (message == null || (!message.toLowerCase().contains("bienvenue") && !message.toLowerCase().contains("bvn")))
		{
			return;
		}

		Player player = event.getPlayer();

		if (player.getUniqueId().equals(uuid))
		{
			return;
		}

		if (p.contains(player.getUniqueId()))
		{
			return;
		}
		
		if (player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(address))
		{
			return;
		}

		p.add(player.getUniqueId());

		String info = ChatColor.translateAlternateColorCodes('&', getConfig().getString("info"));
		String command = getConfig().getString("command").replace("{player}", player.getName());

		player.sendMessage(info);

		Bukkit.getScheduler().runTask(this, new Runnable()
		{
			@Override
			public void run()
			{
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		});
	}

}
