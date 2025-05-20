package me.syncwrld.interview.miniessentials.lang;

import lombok.Getter;
import me.syncwrld.interview.miniessentials.MiniEssentialsPlugin;
import me.syncwrld.interview.miniessentials.util.config.YAML;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.logging.Level;


@Getter
public class Languages {
	
	private final MiniEssentialsPlugin plugin;
	private final List<String> defaultFiles = new ArrayList<>();
	
	/*
	| file name | <message id, message object>
	 */
	private final Map<String, Map<String, LanguageMessage>> messages;
	private final HashMap<String, String> placeholders = new HashMap<>();
	
	public Languages(MiniEssentialsPlugin plugin) {
		this.plugin = plugin;
		this.messages = new HashMap<>();
	}
	
	public Languages registerDefaults(String... files) {
		this.defaultFiles.addAll(Arrays.asList(files));
		return this;
	}
	
	public void load() {
		File languageFolder = new File(plugin.getDataFolder(), "lang");
		messages.clear();
		placeholders.clear();
		
		if (!languageFolder.exists()) {
			languageFolder.mkdirs();
		}
		
		defaultFiles.forEach(defaultFile -> {
			final String fileName = defaultFile.endsWith(".yml") ? defaultFile : defaultFile + ".yml";
			plugin.saveResource("lang/" + fileName, false);
		});
		
		collectPlaceholders();
		
		File[] languageFiles = languageFolder.listFiles((dir, name) -> name.endsWith(".yml"));
		if (languageFiles == null || languageFiles.length == 0) {
			plugin.pluginLogger().debug("No language files found. Please check 'lang' folder or restart the server.");
			return;
		}
		
		for (File languageFile : languageFiles) {
			loadLanguageFile(languageFile);
			plugin.pluginLogger().debug("Loaded message file: " + languageFile.getName());
		}
		
		plugin.pluginLogger().debug(String.format("Loaded %d message files.", messages.size()));
	}
	
	private void loadLanguageFile(File file) {
		try {
			String languageName = file.getName().replace(".yml", "");
			YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
			Map<String, LanguageMessage> languageMessages = new HashMap<>();
			
			loadMessagesFromSection(config, "", languageMessages);
			messages.put(languageName, languageMessages);
		} catch (Exception e) {
			plugin.getLogger().log(Level.SEVERE, "Error while loading lang file: " + file.getName(), e);
		}
	}
	
	private void loadMessagesFromSection(ConfigurationSection section, String path, Map<String, LanguageMessage> messages) {
		for (String key : section.getKeys(false)) {
			String fullPath = path.isEmpty() ? key : path + "." + key;
			Object value = section.get(key);
			
			if (value instanceof ConfigurationSection) {
				loadMessagesFromSection((ConfigurationSection) value, fullPath, messages);
				continue;
			}
			
			messages.put(key, new LanguageMessage(plugin, key, value));
//			plugin.pluginLogger().debug("Registered message: " + fullPath);
			
			if (value instanceof String) {
				applyPlaceholders((String) value);
				continue;
			}
			
			if (value instanceof List<?>) {
				((List<?>) value).forEach(line -> {
					if (line instanceof String) {
						applyPlaceholders((String) line);
					}
				});
			}
		}
	}
	
	private void collectPlaceholders() {
		YAML placeholdersConfiguration = new YAML("lang/placeholders", plugin);
		placeholdersConfiguration.getKeys(false).forEach(sectionKey -> {
			final ConfigurationSection configSection = placeholdersConfiguration.getConfigurationSection(sectionKey);
			if (configSection == null) return;
			
			configSection.getKeys(false).forEach(itemKey -> {
				final Object value = configSection.get(itemKey);
				
				if (value instanceof String) {
					this.placeholders.put(sectionKey + "." + itemKey, value.toString());
				}
			});
		});
	}
	
	public String applyPlaceholders(String text) {
		if (placeholders.isEmpty() || text == null || text.isEmpty()) {
			return text;
		}
		
		final String[] processedText = {text};
		placeholders.forEach((key, value) -> {
			processedText[0] = processedText[0].replace("$" + key, value);
		});
		
		plugin.pluginLogger().debug("Before = " + text + ", after = " + String.join(" ", processedText[0]));
		return processedText[0];
	}
	
	public LanguageMessage find(String namespace, String key) {
		return Optional.ofNullable(messages.get(namespace))
		  .map(messageMap -> messageMap.get(key))
		  .orElseGet(() -> {
			  plugin.getLogger().warning(String.format("Mensagem não encontrada: %s.%s", namespace, key));
			  return new LanguageMessage(plugin, key, null);
		  });
	}
	
}