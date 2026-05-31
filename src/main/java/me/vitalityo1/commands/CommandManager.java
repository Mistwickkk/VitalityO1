package me.vitalityo1.commands;

import me.vitalityo1.VitalityO1;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;

public class CommandManager {

    private final VitalityO1 plugin;

    public CommandManager(VitalityO1 plugin) {
        this.plugin = plugin;
    }

    public void registerAll() {
        register("spawn", new SpawnCommand(plugin));
        register("setspawn", new SetSpawnCommand(plugin));
        register("home", new HomeCommand(plugin));
        register("sethome", new SetHomeCommand(plugin));
        register("delhome", new DelHomeCommand(plugin));
        register("warp", new WarpCommand(plugin));
        register("setwarp", new SetWarpCommand(plugin));
        register("delwarp", new DelWarpCommand(plugin));
        register("tpa", new TpaCommand(plugin));
        register("tpaccept", new TpAcceptCommand(plugin));
        register("tpdeny", new TpDenyCommand(plugin));
        register("back", new BackCommand(plugin));
        register("balance", new BalanceCommand(plugin));
        register("pay", new PayCommand(plugin));
        register("eco", new EcoCommand(plugin));
        register("kick", new KickCommand(plugin));
        register("ban", new BanCommand(plugin));
        register("mute", new MuteCommand(plugin));
        register("unmute", new UnmuteCommand(plugin));
        register("warn", new WarnCommand(plugin));
        register("sc", new StaffChatCommand(plugin));
        register("fly", new FlyCommand(plugin));
        register("god", new GodCommand(plugin));
        register("heal", new HealCommand(plugin));
        register("feed", new FeedCommand(plugin));
        register("vanish", new VanishCommand(plugin));
        register("repair", new RepairCommand(plugin));
        register("msg", new MsgCommand(plugin));
        register("reply", new ReplyCommand(plugin));
        register("seen", new SeenCommand(plugin));
        register("list", new ListCommand(plugin));
    }

    private void register(String name, TabExecutor executor) {
        PluginCommand cmd = plugin.getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
            cmd.setTabCompleter(executor);
        } else {
            plugin.getLogger().warning("Command '" + name + "' not found in plugin.yml!");
        }
    }
}
