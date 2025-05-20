package me.syncwrld.interview.miniessentials.model.command;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data @Accessors(fluent = true)
public class CommandModel {
	private final boolean enabled;
	private final String permissionNode;
	private final List<String> aliases;
}
