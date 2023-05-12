package me.vovari2.dungeonps.utils;

import me.vovari2.dungeonps.DPS;
import org.bukkit.entity.Player;

public class SoundUtils {

    public static void play(Player player, String key){
        player.playSound(DPS.getSound(key));
    }
}
