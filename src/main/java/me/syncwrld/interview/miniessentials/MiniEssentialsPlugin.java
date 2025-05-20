package me.syncwrld.interview.miniessentials;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.syncwrld.interview.miniessentials.command.Commands;
import me.syncwrld.interview.miniessentials.command.impl.*;
import me.syncwrld.interview.miniessentials.lang.Languages;
import me.syncwrld.interview.miniessentials.listener.PlayerDamageListener;
import me.syncwrld.interview.miniessentials.service.GodService;
import me.syncwrld.interview.miniessentials.service.TpaService;
import me.syncwrld.interview.miniessentials.util.config.YAML;
import me.syncwrld.interview.miniessentials.util.logging.PluginLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

@Getter(AccessLevel.PUBLIC)
@Accessors(fluent = true)
public class MiniEssentialsPlugin extends JavaPlugin {
	
	// Utils
	private final PluginLogger pluginLogger = new PluginLogger(this);
	
	// Managers
	private final Commands commands = new Commands(this);
	private final Languages languages = new Languages(this);
	
	// Services
	private final GodService godService = new GodService();
	private final TpaService tpaService = new TpaService(this);
	
	// Configs
	private YAML configuration;
	
	@Override
	public void onLoad() {
		pluginLogger.info("Loading configuration and message files...");
		
		this.configuration = new YAML("configuration", this);
		this.languages.registerDefaults("messages", "placeholders").load();
		this.tpaService.expiration(configuration().getInt("tpa-system.expiration-seconds", 10));
		
		pluginLogger.info("Loaded configuration.");
		pluginLogger.info("Loading and registering commands...");
		this.commands.load();
	}
	
	@Override
	public void onEnable() {
		tpaService.load();
		
		commands.registerCommands(
		  new EnderchestCommand(this), new FixToolCommand(this), new GamemodeCommand(this),
		  new GodCommand(this), new OpenInventoryCommand(this), new TrashCommand(this),
		  new TpRequestCommand(this, tpaService), new TpAcceptCommand(this, tpaService), new TpDenyCommand(this, tpaService)
		);
		registerListeners(new PlayerDamageListener(godService));
	}
	
	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			Bukkit.getPluginManager().registerEvents(listener, this);
		}
	}
	
}
