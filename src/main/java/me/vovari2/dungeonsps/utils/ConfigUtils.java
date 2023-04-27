package me.vovari2.dungeonsps.utils;

import me.vovari2.dungeonsps.DPS;
import me.vovari2.dungeonsps.DPSLocale;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigUtils {
    private static FileConfiguration loadConfiguration(File file) {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void Initialization() throws Exception {
        File dataFolder = DPS.getInstance().getDataFolder();

        // Загрузка основного файла конфигурации
        if (dataFolder.mkdir())
            TextUtils.sendInfoMessage("Folder \"DungeonsPS\" in \"plugins\" was created!");

        File file = new File(dataFolder, "text.yml");
        if (!file.exists())
            DPS.getInstance().saveResource("text.yml", false);
        FileConfiguration config = loadConfiguration(file);

        loadLocale(config);

        file = new File(dataFolder, "config.yml");
        if (!file.exists())
            DPS.getInstance().saveResource("config.yml", false);
        config = loadConfiguration(file);

        loadSettings(config);
    }
    // Загрузка параметров основного конфига
    private static void loadSettings(FileConfiguration config) throws Exception {
        DPS plugin = DPS.getInstance();
        ConfigurationSection section;

        // Загрузка параметров предметов
        section = config.getConfigurationSection("items");
        if (section == null)
            throw new Exception("Value \"items\" must not be empty!");
        HashMap<String, ItemStack> items = new HashMap<>();
        for (String path : section.getKeys(false)){
            String fullPath = "items." + path;
            ItemStack item = new ItemStack(Material.valueOf(config.getString("items." + path + ".material")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(checkInt(config.getString(fullPath + ".custom_model_data"), fullPath + ".custom_model_data"));
            itemMeta.displayName(convertStringToComponent(config.getString(fullPath + ".name"), fullPath + ".name"));
            itemMeta.lore(convertStringListToComponent(config.getStringList(fullPath + ".lore"), fullPath + ".lore"));
            item.setItemMeta(itemMeta);
            items.put(path, item);
        }
        DPS.getInstance().items = items;


        // Загрузка всех координат
        section = config.getConfigurationSection("points");
        if (section == null)
            throw new Exception("Value \"points\" must not be empty!");
        HashMap<String, Location> points = new HashMap<>();
        for (String path : section.getKeys(false))
            points.put("path", loadLocation(config.getString("points." + path), path));
        DPS.getInstance().points = points;
    }
    private static Location loadLocation(String str, String name) throws Exception{
        if (str == null || str.equals(""))
            throw new Exception("Failed to load point \"points." + name + "\"!");

        try{
            String[] arrayStr = str.trim().split(" ");
            return new Location(Bukkit.getWorld(arrayStr[0]), Double.parseDouble(arrayStr[1]), Double.parseDouble(arrayStr[2]), Double.parseDouble(arrayStr[3]), Float.parseFloat(arrayStr[4]), Float.parseFloat(arrayStr[5]));
        }
        catch(Exception error){
            throw new Exception("Failed to load point \"points." + name + "\"! -> " + error.getMessage());
        }
    }

    // Загрузка локализации плагина
    private static void loadLocale(FileConfiguration configLocale){
        File dataFolder = DPS.getInstance().getDataFolder();

        // Надписи, которые сразу можно конвертировать в Component
        String[] array = new String[] {
                "command.plugin_reload",
                "command.can_use_only_player",
                "command.dont_have_permission",
                "command.command_incorrectly",
                "command.party_is_fill",
                "menu.select_type.name"
        };
        HashMap<String, Object> localeTexts = new HashMap<>();
        for (String str : array)
            localeTexts.put(str, convertStringToComponent(configLocale.getString(str), str));

        // Надписи, которые нужно оставить в виде строк
        array = new String[] {
                "menu.party_settings.name_leader",
                "menu.party_settings.name_player",
                "menu.select_player.name",
                "button.party_player_for_leader",
                "button.party_player_for_player",
                "placeholders.button_ready_1.ready",
                "placeholders.button_ready_1.not_ready",
                "placeholders.button_ready_2.ready",
                "placeholders.button_ready_2.not_ready",
                "placeholders.button_ready_3.ready",
                "placeholders.button_ready_3.not_ready",
                "placeholders.button_ready_4.ready",
                "placeholders.button_ready_4.not_ready",
                "placeholders.button_chat.use",
                "placeholders.button_chat.not_use",
                "placeholders.button_func.ready",
                "placeholders.button_func.cancel",
                "placeholders.button_func.start"
        };
        for (String str : array)
            localeTexts.put(str, checkString(configLocale.getString(str), str));

        // Списки надписей, которые можно сразу конвертировать в списки Component
        array = new String[] {
                // В будущем тут может что-то будет
        };
        HashMap<String, List<Component>> localeListComponent = new HashMap<>();
        for (String str : array)
            localeListComponent.put(str, convertStringListToComponent(configLocale.getStringList(str), str));

        // Списки надписей, которые нужно оставить в виде строк
        array = new String[] {
                "button.party_player_for_leader_lore"
        };
        HashMap<String, List<String>> localeListString = new HashMap<>();
        for (String str : array){
            List<String> listStrings = new ArrayList<>();
            for (String str2 : configLocale.getStringList(str))
                listStrings.add(checkString(str2, str));
            localeListString.put(str, listStrings);
        }

        // Запись всех списков в основные переменные
        DPS.getInstance().locale = new DPSLocale(localeTexts, localeListComponent, localeListString);
    }
    private static int checkInt(String text, String path) throws Exception{
        if (text == null)
            throw new Exception("Title \"" + path + "\" in \"config.yml\" not exists!");
        return Integer.parseInt(text);
    }
    private static String checkString(String text, String path){
        if (text == null){
            TextUtils.sendWarningMessage("Title \"" + path + "\" not exists! An empty string will be given");
            return "";
        }
        if (text.contains("&") || text.contains("§")){
            TextUtils.sendWarningMessage("Title \"" + path + " must not have char \"&\" or \"§\"! An empty string will be given");
            return "";
        }
        return text;
    } // Проверяет строку на null и на символы & или §
    private static Component convertStringToComponent(String text, String path){
        return MiniMessage.miniMessage().deserialize(checkString(text, path));
    } // С проверкой конвертирует строку в Component
    private static List<Component> convertStringListToComponent(List<String> texts, String path){
        List<Component> listComponent = new ArrayList<>();
        for (String text : texts)
            listComponent.add(convertStringToComponent(text, path));
        return listComponent;
    } // С проверкой конвертирует список строк в список Component

}
