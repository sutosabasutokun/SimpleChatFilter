package com.yourname.chatfilter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final List<String> blockedWords = Arrays.asList("死ね", "バカ", "スパムサイトURL"); 
    private final int COOLDOWN_TIME = 3000; 

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SimpleChatFilter(26.2対応版)が有効になりました！");
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // NGワードチェック
        for (String word : blockedWords) {
            if (message.contains(word)) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "不適切な単語が含まれているため、送信できませんでした。");
                return;
            }
        }

        // 連投（スパム）防止チェック
        if (cooldowns.containsKey(player.getUniqueId())) {
            long timeElapsed = System.currentTimeMillis() - cooldowns.get(player.getUniqueId());
            
            if (timeElapsed < COOLDOWN_TIME) {
                event.setCancelled(true);
                long secondsLeft = (COOLDOWN_TIME - timeElapsed) / 1000 + 1;
                player.sendMessage(ChatColor.YELLOW + "チャットが早すぎます。あと " + secondsLeft + " 秒お待ちください。");
                return;
            }
        }

        cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
