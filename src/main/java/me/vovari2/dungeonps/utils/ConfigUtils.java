package me.vovari2.dungeonps.utils;

import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.DPSLocale;
import me.vovari2.dungeonps.objects.DPSDungeon;
import me.vovari2.dungeonps.objects.DPSItemPH;
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


        // Загрузка команд для работы с другими плагинами
        section = config.getConfigurationSection("commands");
        if (section == null)
            throw new Exception("Value \"commands\" must not be empty!");
        HashMap<String, String> commands = new HashMap<>();
        for (String path : section.getKeys(false))
            commands.put(path, checkString(config.getString("commands." + path), path));
        plugin.commands = commands;


        // Загрузка всех координат
        section = config.getConfigurationSection("dungeons");
        if (section == null)
            throw new Exception("Value \"dungeons\" must not be empty!");
        HashMap<String, DPSDungeon> dungeons = new HashMap<>();
        for (String path : section.getKeys(false)){
            dungeons.put(path, new DPSDungeon(
                    path,
                    checkString(config.getString("dungeons." + path + ".name"), "dungeons." + path + ".name"),
                    checkInt(config.getString("dungeons." + path + ".enter_in_distance"), "dungeons." + path + ".enter_in_distance"),
                    loadLocation(config.getString("dungeons." + path + ".enter_in_dungeon"), "dungeons." + path + ".enter_in_dungeon"),
                    loadLocation(config.getString("dungeons." + path + ".enter_out_dungeon"), "dungeons." + path + ".enter_out_dungeon"),
                    loadLocation(config.getString("dungeons." + path + ".party_start"), "dungeons." + path + ".party_start"),
                    loadLocation(config.getString("dungeons." + path + ".party_settings"), "dungeons." + path + ".party_settings")));
        }
        plugin.dungeons = dungeons;


        // Загрузка параметров предметов
        section = config.getConfigurationSection("items");
        if (section == null)
            throw new Exception("Value \"items\" must not be empty!");
        HashMap<String, ItemStack> items = new HashMap<>();
        for (String path : section.getKeys(false)){
            String fullPath = "items." + path;
            ItemStack item = new ItemStack(Material.valueOf(config.getString(fullPath + ".material")));
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(checkInt(config.getString(fullPath + ".custom_model_data"), fullPath + ".custom_model_data"));
            itemMeta.displayName(convertStringToComponent(config.getString(fullPath + ".name"), fullPath + ".name"));
            itemMeta.lore(convertStringListToComponent(config.getStringList(fullPath + ".lore"), fullPath + ".lore"));
            item.setItemMeta(itemMeta);
            items.put(path, item);
        }
        plugin.items = items;


        // Загрузка параметров предметов
        section = config.getConfigurationSection("items_placeholders");
        if (section == null)
            throw new Exception("Value \"items_placeholders\" must not be empty!");
        HashMap<String, DPSItemPH> itemsPH = new HashMap<>();
        for (String path : section.getKeys(false)){
            String fullPath = "items_placeholders." + path;

            itemsPH.put(path, new DPSItemPH(
                    Material.valueOf(config.getString(fullPath + ".material")),
                    checkString(config.getString(fullPath + ".name"), fullPath + ".name"),
                    checkListString(config.getStringList(fullPath + ".lore"), fullPath + ".lore"),
                    checkInt(config.getString(fullPath + ".custom_model_data"), fullPath + ".custom_model_data"),
                    checkArrayString(config.getStringList(fullPath + ".placeholders.name"), fullPath + ".placeholders.name"),
                    checkArrayString(config.getStringList(fullPath + ".placeholders.lore"), fullPath + ".placeholders.lore")));
        }
        plugin.itemsPH = itemsPH;

    }
    private static Location loadLocation(String str, String name) throws Exception{
        if (str == null || str.equals(""))
            throw new Exception("Failed to load point \"" + name + "\"!");

        try{
            String[] arrayStr = str.trim().split(" ");
            return new Location(Bukkit.getWorld(arrayStr[0]), Double.parseDouble(arrayStr[1]), Double.parseDouble(arrayStr[2]), Double.parseDouble(arrayStr[3]), Float.parseFloat(arrayStr[4]), Float.parseFloat(arrayStr[5]));
        }
        catch(Exception error){
            throw new Exception("Failed to load point \"points." + name + "\"! -> " + error.getMessage());
        }
    }



    // Загрузка локализации плагина
    private static void loadLocale(FileConfiguration configLocale) throws Exception{
        // Надписи, которые сразу можно конвертировать в Component
        String[] array = new String[] {
                "command.plugin_reload",
                "command.can_use_only_player",
                "command.dont_have_permission",
                "command.command_incorrectly",
                "command.party_not_created",
                "command.party_already_created",
                "command.party_is_fill",
                "menu.party_start.name"
        };
        HashMap<String, Object> localeTexts = new HashMap<>();
        for (String str : array)
            localeTexts.put(str, convertStringToComponent(configLocale.getString(str), str));

        // Надписи, которые нужно оставить в виде строк
        array = new String[] {
                "menu.party_settings.name_leader",
                "menu.party_settings.name_player",
                "menu.party_players.name",
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
                "placeholders.button_func.start",
                "placeholders.button_notice.use",
                "placeholders.button_notice.not_use",
                "command.party_all_notice"
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
                // В будущем тут может что-то будет
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
    } // Проверяет и переводит строку в число
    private static String checkString(String text, String path) throws Exception{
        if (text == null)
            throw new Exception("Text \"" + path + "\" not exists!");
        if (text.contains("&") || text.contains("§"))
            throw new Exception("Title \"" + path + " must not have char \"&\" or \"§\"!");
        return text;
    } // Проверяет строку на null и на символы & или §
    private static String[] checkArrayString(List<String> texts, String path) throws Exception{
        String[] newTexts = new String[texts.size()];
        for(int i = 0; i < texts.size(); i++)
            newTexts[i] = checkString(texts.get(i), path);
        return newTexts;
    } // Проверяет строки в массиве на null и на символы & или §
    private static List<String> checkListString(List<String> texts, String path) throws Exception{
        List<String> newTexts = new ArrayList<>();
        for (String text : texts)
            newTexts.add(checkString(text, path));
        return newTexts;
    } // Проверяет строки в списке на null и на символы & или §
    private static Component convertStringToComponent(String text, String path) throws Exception{
        return MiniMessage.miniMessage().deserialize(checkString(text, path));
    } // С проверкой конвертирует строку в Component
    private static List<Component> convertStringListToComponent(List<String> texts, String path)throws Exception{
        List<Component> listComponent = new ArrayList<>();
        for (String text : texts)
            listComponent.add(convertStringToComponent(text, path));
        return listComponent;
    } // С проверкой конвертирует список строк в список Component

}
