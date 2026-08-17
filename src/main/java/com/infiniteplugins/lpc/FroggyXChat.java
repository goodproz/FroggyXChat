package com.infiniteplugins.lpc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;

import java.util.regex.Pattern;
public final class FroggyXChat extends JavaPlugin implements Listener {

    private LuckPerms luckPerms;

    @Override
    public void onEnable() {
        // Double check to ensure LuckPerms is running on the server
        if (getServer().getPluginManager().getPlugin("LuckPerms") == null) {
            getLogger().severe("LuckPerms was not found! Disabling FroggyXChat.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize the LuckPerms developer bridge
        this.luckPerms = LuckPermsProvider.get();
        
        // Register this class to manage chat events
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("FroggyXChat has successfully loaded! Ribbit.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        
        // Loop through all online players to check if someone is being tagged
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            String tagTarget = "@" + onlinePlayer.getName();
            
            // If the message contains their @name (not case sensitive)
            if (message.toLowerCase().contains(tagTarget.toLowerCase())) {
                
                // BRILLIANT VISUAL: Highlight the tagged username in Gold and return back to white text
                String highlightedTag = ChatColor.GOLD + "" + ChatColor.BOLD + tagTarget + ChatColor.RESET + ChatColor.WHITE;
                
                // Update the message string with the new golden highlight
                message = message.replaceAll("(?i)" + Pattern.quote(tagTarget), highlightedTag);
                
                // BRILLIANT AUDIO: Play a crisp notification ring directly to the player who was tagged
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.2f);
            }
        }
        
        // Apply the updated message back into the chat pool
        event.setMessage(message);
        
        // Basic chat structure (Prefix + Name + Message)
        Player sender = event.getPlayer();
        User user = luckPerms.getUserManager().getUser(sender.getUniqueId());
        if (user != null) {
            String prefix = user.getCachedData().getMetaData().getPrefix();
            if (prefix == null) prefix = "";
            
            // Format the final visual message for the server chat stream
            String formattedChat = ChatColor.translateAlternateColorCodes('&', prefix) + sender.getName() + " §7» §f" + event.getMessage();
            event.setFormat(formattedChat.replace("%", "%%")); 
        }
    }
}