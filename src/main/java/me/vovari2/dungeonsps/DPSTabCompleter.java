package me.vovari2.dungeonsps;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DPSTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player))
            return new ArrayList<>();

        if (args.length == 1){
            List<String> subcommands = new ArrayList<>();
            if (sender.hasPermission("dungeonsps.player")){
                subcommands.add("accept");
                subcommands.add("decline");
            }
            if (sender.hasPermission("dungeonsps.admin")){
                subcommands.add("create_party");
                subcommands.add("reload");
            }
            return subcommands;
        }
        if (args.length == 2){
            switch(args[0]){
                case "accept":
                    case "decline":
                        case "create_party": return getPlayerStringList();
            }
        }
        return new ArrayList<>();
    }

    private List<String> getPlayerStringList(){
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers())
            players.add(player.getName());
        return players;
    }
}
