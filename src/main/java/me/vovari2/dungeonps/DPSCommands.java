package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.utils.MenuUtils;
import org.bukkit.Bukkit;
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

        // Проверка, если нет параметров
        if (args.length == 0){
            if (!(sender instanceof Player)) {
                sender.sendMessage(DPSLocale.getLocaleComponent("command.can_use_only_player"));
                return true;
            }
            Player player = (Player) sender;
            DPSParty party = DPSParty.get(player.getName());
            if (party == null){
                player.sendMessage(DPSLocale.getLocaleComponent("command.party_not_created"));
                return true;
            }
            player.openInventory(MenuUtils.formPartyLeader(party));
            return true;
        }

        // Проверка, если параметров слишком много
        if (args.length > 2){
            sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
            return true;
        }

        switch(args[0]) {
            // Команда /dps create_party <Игрок>
            case "create_party": {
                if (!sender.hasPermission("dungeonps.admin"))
                    return true;
                if (args.length != 2) {
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
                    return true;
                }
                Player targetPlayer = Bukkit.getPlayer(args[1]);
                if (targetPlayer == null)
                    return true;
                DPSParty party = DPSParty.add(targetPlayer);
                if (party == null){
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.party_already_created"));
                    return true;
                }

                targetPlayer.openInventory(MenuUtils.formPartyLeader(party));
            } return true;

            // Команда /dps reload
            case "reload": {
                if (!sender.hasPermission("dungeonps.admin"))
                    return true;
                if (args.length != 1) {
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
                    return true;
                }

                plugin.onDisable();
                plugin.onEnable();
                sender.sendMessage(DPSLocale.getLocaleComponent("command.plugin_reload"));
            } return true;

            // Команда /dps accept <Игрок>
            case "accept": {
                if (!sender.hasPermission("dungeonps.admin") && !sender.hasPermission("dungeonps.admin"))
                    return true;
                if (args.length != 2) {
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.can_use_only_player"));
                    return true;
                }
                Player player = (Player) sender;

                // Тут будет принятие приглашения в пати
            } return true;

            // Команда /dps decline <Игрок>
            case "decline":{
                if (!sender.hasPermission("dungeonps.admin") && !sender.hasPermission("dungeonps.admin"))
                    return true;
                if (args.length != 2) {
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
                    return true;
                }
                if (!(sender instanceof Player)) {
                    sender.sendMessage(DPSLocale.getLocaleComponent("command.can_use_only_player"));
                    return true;
                }
                Player player = (Player) sender;

                // Тут будет отклонение приглашения в пати
            } return true;
            default: sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly")); return true;
        }
    }

}
