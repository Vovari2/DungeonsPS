package me.vovari2.dungeonps;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.google.common.collect.ImmutableList;
import me.vovari2.dungeonps.listeners.DPSListener;
import me.vovari2.dungeonps.listeners.MenuClick;
import me.vovari2.dungeonps.objects.DPSDungeon;
import me.vovari2.dungeonps.objects.DPSItemPH;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import me.vovari2.dungeonps.utils.ConfigUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DPS extends JavaPlugin {

    private static DPS plugin;
    private PartiesAPI partiesAPI;
    public DPSLocale locale;

    public HashMap<String, DPSDungeon> dungeons;
    public HashMap<String, String> commands;
    public HashMap<String, Integer> periods;
    public HashMap<String, Sound> sounds;
    public HashMap<String, ItemStack> items;
    public HashMap<String, DPSItemPH> itemsPH;

    public HashMap<String, DPSParty> parties;
    private ImmutableList<String> nameMenus;

    private DPSTaskTicks taskTicks;

    @Override
    public void onEnable() {
        long loadingTime = System.currentTimeMillis();

        plugin = this;

        if (!plugin.getServer().getPluginManager().isPluginEnabled("Parties")){
            disablePlugin("Plugin \"Parties\" don't loading on the server!");
            return;
        }
        partiesAPI = Parties.getApi();
        parties = new HashMap<>();
        nameMenus = ImmutableList.of("party_settings", "party_start", "party_players");

        try{
            ConfigUtils.Initialization();
        } catch(Exception error){
            disablePlugin(error.getMessage());
            return;
        }

        getServer().getPluginManager().registerEvents(new MenuClick(), this);
        getServer().getPluginManager().registerEvents(new DPSListener(), this);

        PluginCommand command = getCommand("dungeonps");
        if (command != null) {
            command.setExecutor(new DPSCommands(this));
            command.setTabCompleter(new DPSTabCompleter());
        }

        taskTicks = new DPSTaskTicks();
        taskTicks.runTaskTimer(this, 1, 1);

        TextUtils.sendInfoMessage("Plugin enabled for " + (System.currentTimeMillis() - loadingTime) + " ms");
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
        if (taskTicks != null)
            taskTicks.cancel();

        for (DPSParty party : parties.values())
            for(DPSPlayer dpsPlayer : party.getPlayers())
                party.removePlayer(dpsPlayer, true);

        TextUtils.sendInfoMessage("Plugin disabled!");
    }

    public static void disablePlugin(String message){
        TextUtils.sendWarningMessage(message);
        plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    public static DPS getInstance(){
        return plugin;
    }
    public static ConsoleCommandSender getConsoleSender(){
        return plugin.getServer().getConsoleSender();
    }

    public static PartiesAPI getPartiesAPI(){
        return plugin.partiesAPI;
    }

    public static DPSDungeon getDungeon(String key) {
        return plugin.dungeons.get(key);
    }
    public static String getDPSCommand(String key) {
        return plugin.commands.get(key);
    }
    public static int getPeriod(String key){
        return plugin.periods.get(key);
    }
    public static Sound getSound(String key){
        return plugin.sounds.get(key);
    }
    public static ItemStack getItem(String key){
        return plugin.items.get(key);
    }
    public static DPSItemPH getItemPH(String key){
        return plugin.itemsPH.get(key);
    }

    public static HashMap<String, DPSParty> getParties(){
        return plugin.parties;
    }
    public static HashMap<String, DPSDungeon> getDungeons(){
        return plugin.dungeons;
    }
    public static ImmutableList<String> getNameMenus(){
        return plugin.nameMenus;
    }

    public static DPSTaskTicks getTaskTicks(){
        return plugin.taskTicks;
    }
}
