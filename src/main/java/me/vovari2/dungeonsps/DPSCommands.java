package me.vovari2.dungeonsps;

import me.vovari2.dungeonsps.utils.MenuUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DPSCommands implements CommandExecutor {
    private final DPS plugin;
    public DPSCommands(DPS plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(DPSLocale.getLocaleComponent("command.can_use_only_player"));
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("dungeonsps.player")){
            player.sendMessage(DPSLocale.getLocaleComponent("command.dont_have_permission"));
            return true;
        }

        if (args.length == 0){
            DPSParty party = DPS.getParty(player.getName());
            if (party == null){
                player.sendMessage(DPSLocale.getLocaleComponent("command.party_not_created"));
                return true;
            }
            player.openInventory(MenuUtils.formPartyLeader(DPS.getParty(player.getName())));
            return true;
        }

        if (args.length > 2){
            player.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
            return true;
        }

        switch(args[0]) {
            case "create_party": {
                DPS.addParty(player);
            } return true;
            case "join_party": {

            } return true;
            case "reload": {
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                plugin.getServer().getPluginManager().enablePlugin(plugin);
                player.sendMessage(DPSLocale.getLocaleComponent("command.plugin_reload"));
            } return true;
            default: player.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
        }
        return true;
    }
}
