package me.vovari2.dungeonsps.utils;

import me.vovari2.dungeonsps.DPS;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtils {
    public static void sendInfoMessage(String message){
        DPS.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("[DungeonPS] <green>" + message));
    }
    public static void sendWarningMessage(String message){
        DPS.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<yellow>[DungeonPS] " + message));
    }
}
