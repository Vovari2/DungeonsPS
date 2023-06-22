package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.DPSDelayFunction;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import me.vovari2.dungeonps.utils.MenuUtils;
import me.vovari2.dungeonps.utils.SoundUtils;
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
        if (!sender.hasPermission("dungeonps.player"))
            return true;

        if (!(sender instanceof Player)) {
            sender.sendMessage(DPSLocale.getLocaleComponent("command.can_use_only_player"));
            return true;
        }
        Player player = (Player) sender;
        String playerName = player.getName();

        // Проверка, если нет параметров
        if (args.length == 0){
            DPSParty party = DPSParty.get(playerName);
            if (party == null){
                player.sendMessage(DPSLocale.getLocaleComponent("command.party_not_created"));
                return true;
            }
            DPSPlayer dpsPlayer = party.getPlayer(playerName);
            if (dpsPlayer == null){
                player.sendMessage(DPSLocale.getLocaleComponent("command.party_not_created"));
                return true;
            }
            if (dpsPlayer.isLeader())
                MenuUtils.openPartySettingsLeader(party, dpsPlayer);
            else MenuUtils.openPartySettingsPlayer(party, dpsPlayer);
            return true;
        }

        // Проверка, если параметров слишком много
        if (args.length > 2){
            sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly"));
            return true;
        }

        switch(args[0]) {
            // Команда /dps accept <Игрок>
            case "accept": {
                DPSParty party = DPSParty.get(args[1]);

                if (party == null){
                    player.sendMessage(DPSLocale.getLocaleComponent("command.party_of_player_not_created"));
                    return true;
                }
                if (party.getPlayers().size() == 4){
                    player.sendMessage(DPSLocale.getLocaleComponent("command.party_is_fill"));
                    return true;
                }

                if (party.getPlayer(playerName) != null){
                    player.sendMessage(DPSLocale.getLocaleComponent("command.party_already_created"));
                    return true;
                }

                DPSDelayFunction.add(playerName, "teleport_settings_party", "extinction");
                party.addPlayer(player, false);
                SoundUtils.play(player, "button_click");

                // Тут будет принятие приглашения в пати
            } return true;
            default: sender.sendMessage(DPSLocale.getLocaleComponent("command.command_incorrectly")); return true;
        }
    }

}
