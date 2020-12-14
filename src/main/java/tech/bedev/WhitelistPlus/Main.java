package tech.bedev.WhitelistPlus;

import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener, CommandExecutor, TabCompleter {

    List<String> arg0 = new ArrayList<String>();
    List<String> modearg = new ArrayList<>();

    private static Permission perms = null;

    public static Permission getVault() {
        return perms;
    }

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Could not find Vault! This plugin is required for WhitelistPlus to work!");
            this.setEnabled(false);
        }
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required for WhitelistPlus to work!");
            this.setEnabled(false);
        }
        if (!Bukkit.getOnlineMode()) {
            this.getLogger().warning(ChatColor.RED + "You must have your server on online mode for this plugin to work!");
            this.setEnabled(false);
        }
        if (getConfig().getString("Mode").equalsIgnoreCase("admin")) {
            //do nothing
        } else if (getConfig().getString("Mode").equalsIgnoreCase("staff")) {
            //do nothing
        } else if (getConfig().getString("Mode").equalsIgnoreCase("custom")) {
            //do nothing
        } else if (getConfig().getString("Mode").equalsIgnoreCase("all")) {
            //do nothing
        } else if (getConfig().getString("Mode").equalsIgnoreCase("beta")) {
            //do nothing
        } else {
            //not valid
            this.getLogger().warning(ChatColor.RED + "Invalid mode! Please enter a valid mode in config.yml");
        }
        setupVault();
        getCommand("whitelist").setExecutor(this);
        getCommand("whitelist").setTabCompleter(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    private boolean setupVault() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        String kickmsg = ChatColor.translateAlternateColorCodes('&', getConfig().getString("KickMessage"));
        kickmsg = PlaceholderAPI.setPlaceholders(player, kickmsg);
        if (this.getConfig().getString("Mode").equalsIgnoreCase("admin")) {
            if (!(getConfig().getStringList("Admin.Groups").contains(getVault().getPrimaryGroup(player))) || !(getConfig().getStringList("Admin.Users").contains(player.getUniqueId().toString()))) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', kickmsg));
            }
        } else if (this.getConfig().getString("Mode").equalsIgnoreCase("beta")) {
            if (!(getConfig().getStringList("Beta.Groups").contains(getVault().getPrimaryGroup(player))) || !(getConfig().getStringList("Beta.Users").contains(player.getUniqueId().toString()))) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', kickmsg));
            }
        } else if (this.getConfig().getString("Mode").equalsIgnoreCase("staff")) {
            if (!(getConfig().getStringList("Staff.Groups").contains(getVault().getPrimaryGroup(player))) || !(getConfig().getStringList("Staff.Users").contains(player.getUniqueId().toString()))) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', kickmsg));
            }
        } else if (this.getConfig().getString("Mode").equalsIgnoreCase("custom")) {
            if (!(getConfig().getStringList("Custom.Groups").contains(getVault().getPrimaryGroup(player))) || !(getConfig().getStringList("Custom.Users").contains(player.getUniqueId().toString()))) {
                e.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, ChatColor.translateAlternateColorCodes('&', kickmsg));
            }
        } else if (this.getConfig().getString("Mode").equalsIgnoreCase("all")) {
            e.allow();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("whitelist")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe whitelist mode is currently on &e" + getConfig().getString("Mode")));
            }
            else if (args[0].equalsIgnoreCase("mode")) {
                if (sender.hasPermission("wp.switchmode")) {
                    if (args.length == 1) {
                        sender.sendMessage(ChatColor.RED + "Please enter a mode!");
                    }
                    if (args.length == 2) {
                        if (args[1].equalsIgnoreCase("beta")) {
                            getConfig().set("Mode", "beta");
                            saveConfig();
                            reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Changed mode to beta");
                        } else if (args[1].equalsIgnoreCase("admin")) {
                            getConfig().set("Mode", "admin");
                            saveConfig();
                            reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Changed mode to admin");
                        } else if (args[1].equalsIgnoreCase("staff")) {
                            getConfig().set("Mode", "staff");
                            saveConfig();
                            reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Changed mode to staff");
                        } else if (args[1].equalsIgnoreCase("all")) {
                            getConfig().set("Mode", "all");
                            saveConfig();
                            reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Changed mode to all");
                        } else if (args[1].equalsIgnoreCase("custom")) {
                            getConfig().set("Mode", "custom");
                            saveConfig();
                            reloadConfig();
                            sender.sendMessage(ChatColor.GREEN + "Changed mode to custom");
                        } else {
                            sender.sendMessage(ChatColor.RED + "Not a valid mode!");
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "No Permission!");
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("wp.reload")) {
                    reloadConfig();
                    saveConfig();
                    sender.sendMessage(ChatColor.GREEN + "Reloaded config");
                } else {
                    sender.sendMessage(ChatColor.RED + "No Permission!");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Invalid argument!");
            }
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (arg0.isEmpty()) {
            if (sender.hasPermission("wp.switchmode")) {
                arg0.add("mode");
            }
            if (sender.hasPermission("wp.reload")) {
                arg0.add("reload");
            }
        }
        if (modearg.isEmpty()) {
            modearg.add("all");
            modearg.add("beta");
            modearg.add("staff");
            modearg.add("admin");
            modearg.add("custom");
        }

        List<String> result = new ArrayList<String>();
        if (args.length == 1) {
            for (String a : arg0) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase()))
                    result.add(a);
            }
            return result;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("mode")) {
                for (String a : modearg) {
                    if (a.toLowerCase().startsWith(args[1].toLowerCase()))
                        result.add(a);
                }
                return result;
            }
        }
        return null;
    }
}
