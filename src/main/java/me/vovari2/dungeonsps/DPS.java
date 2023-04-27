package me.vovari2.dungeonsps;

import com.alessiodp.parties.api.Parties;
import com.alessiodp.parties.api.interfaces.PartiesAPI;
import me.vovari2.dungeonsps.utils.ConfigUtils;
import me.vovari2.dungeonsps.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class DPS extends JavaPlugin {

    private static DPS plugin;
    private PartiesAPI partiesAPI;
    public DPSLocale locale;

    public HashMap<String, ItemStack> items;
    public HashMap<String, Location> points;

    private HashMap<String, DPSParty> parties;

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

        try{
            ConfigUtils.Initialization();
        } catch(Exception error){
            disablePlugin(error.getMessage());
            return;
        }

        getServer().getPluginManager().registerEvents(new DPSListener(), this);

        PluginCommand command = getCommand("dungeonsps");
        command.setExecutor(new DPSCommands(this));
        command.setTabCompleter(new DPSTabCompleter());

        TextUtils.sendInfoMessage("Plugin enabled for " + (System.currentTimeMillis() - loadingTime) + " ms");
    }

    @Override
    public void onDisable(){
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

    public static DPSParty getParty(String key){
        return plugin.parties.get(key);
    }
    public static DPSParty addParty(Player player){
        String playerName = player.getName();
        getPartiesAPI().createParty(playerName, getPartiesAPI().getPartyPlayer(player.getUniqueId()));

        DPSParty party = new DPSParty(getPartiesAPI().getParty(playerName), player);
        plugin.parties.put(playerName, party);

        return party;
    }
}
