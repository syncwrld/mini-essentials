package me.syncwrld.interview.miniessentials.command;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.model.command.CommandModel;
import me.syncwrld.interview.miniessentials.util.config.YAML;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
@Accessors(fluent = true)
public class Commands {
	
	private final MiniEssentialsPlugin plugin;
	private final YAML commandsConfiguration;
	private final Map<String, CommandModel> modelsById = new HashMap<>();
	
	public Commands(MiniEssentialsPlugin plugin) {
		this.plugin = plugin;
		this.commandsConfiguration = new YAML("commands", plugin);
	}
	
	public void load() {
		this.commandsConfiguration.create();
		this.modelsById.clear();
		
		final ConfigurationSection configurationSection = commandsConfiguration.getConfigurationSection("commands");
		if (configurationSection == null) {
			plugin.pluginLogger().error(
			  "Error while loading commands! Can't find 'commands' section, maybe typing error or corrupted file? " +
			  "Try deleting current commands configuration to avid errors."
			);
			return;
		}
		
		configurationSection.getKeys(false).forEach(commandKey -> {
			final boolean enabled = configurationSection.getBoolean(commandKey + ".enabled");
			final String permissionNode = configurationSection.getString(commandKey + ".permission");
			final List<String> aliases = configurationSection.getStringList(commandKey + ".aliases");
			
			plugin.pluginLogger().debug(
			  "Registering command " + commandKey + " with" +
			  (Strings.isNullOrEmpty(permissionNode) ? "out permission " : " permission " + permissionNode) +
			  "and aliases: " + String.join(", ", aliases)
			);
			this.modelsById.put(commandKey, new CommandModel(enabled, permissionNode, aliases));
		});
		
		plugin.pluginLogger().info("Loaded " + this.modelsById.size() + " commands");
	}
	
	@SneakyThrows
	public void registerCommands(AbstractCommand... commands) {
		for (AbstractCommand command : commands) {
			registerCommand(command);
		}
	}
	
	private void registerCommand(AbstractCommand command) {
		final String commandKey = command.commandKey();
		
		final Optional<CommandModel> commandModelOptional = find(commandKey);
		if (commandModelOptional.isEmpty()) {
			this.plugin.pluginLogger().error(
			  "Unrecognized command '" + commandKey + "', ignoring!"
			);
			return;
		}
		
		final CommandModel commandModel = commandModelOptional.get();
		
		final PluginCommand pluginCommand = plugin.getCommand(commandKey);
		pluginCommand.setExecutor(command);
		pluginCommand.setAliases(commandModel.aliases());
		command.permissionNode(commandModel.permissionNode());
		command.enabled(commandModel.enabled());
	}
	
	public Optional<CommandModel> find(String commandKey) {
		return Optional.ofNullable(this.modelsById.get(commandKey));
	}
	
}
