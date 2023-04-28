package me.vovari2.dungeonps;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import com.google.common.collect.ImmutableList;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.utils.ConfigUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import org.bukkit.Location;
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

    public HashMap<String, ItemStack> items;
    public HashMap<String, Location> points;
    public HashMap<String, String> commands;

    public HashMap<String, DPSParty> parties;
    private ImmutableList<String> nameMenus;

    private DPSTaskSeconds taskSeconds;

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
        nameMenus = ImmutableList.of("party_settings", "select_type", "select_player");

        try{
            ConfigUtils.Initialization();
        } catch(Exception error){
            disablePlugin(error.getMessage());
            return;
        }

        getServer().getPluginManager().registerEvents(new DPSListener(), this);

        PluginCommand command = getCommand("dungeonps");
        if (command != null) {
            command.setExecutor(new DPSCommands(this));
            command.setTabCompleter(new DPSTabCompleter());
        }

        taskSeconds = new DPSTaskSeconds();
        taskSeconds.runTaskTimer(this, 20, 20);

        TextUtils.sendInfoMessage("Plugin enabled for " + (System.currentTimeMillis() - loadingTime) + " ms");
    }

    @Override
    public void onDisable(){
        HandlerList.unregisterAll(this);
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

    public static ItemStack getItem(String key){
        return plugin.items.get(key);
    }
    public static Location getLocation(String key) {
        return plugin.points.get(key);
    }
    public static String getDPSCommand(String key) {
        return plugin.commands.get(key);
    }

    public static HashMap<String, DPSParty> getParties(){
        return plugin.parties;
    }
    public static ImmutableList<String> getNameMenus(){
        return plugin.nameMenus;
    }

    public static DPSTaskSeconds getTaskSeconds(){
        return plugin.taskSeconds;
    }
}
