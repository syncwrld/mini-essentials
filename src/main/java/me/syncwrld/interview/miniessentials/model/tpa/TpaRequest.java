package me.syncwrld.interview.miniessentials.model.tpa;

import lombok.Data;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;

import java.time.Instant;

@Data @Accessors(fluent = true)
public class TpaRequest {
    private final Player sender;
    private final Player target;
    private final Instant createdAt;

    public TpaRequest(Player sender, Player target) {
        this.sender = sender;
        this.target = target;
        this.createdAt = Instant.now();
    }

    public boolean isExpired(long expirationSeconds) {
        return Instant.now().isAfter(createdAt.plusSeconds(expirationSeconds));
    }
}
