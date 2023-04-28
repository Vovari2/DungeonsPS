package me.vovari2.dungeonsps.utils;

import me.vovari2.dungeonsps.DPS;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TextUtils {
    public static void sendInfoMessage(String message){
        DPS.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("[DungeonsPS] <green>" + message));
    }
    public static void sendWarningMessage(String message){
        DPS.getConsoleSender().sendMessage(MiniMessage.miniMessage().deserialize("<yellow>[DungeonsPS] " + message));
    }

    public static void launchCommand(String command){
        DPS.getInstance().getServer().dispatchCommand(DPS.getConsoleSender(), command);
    }
}
