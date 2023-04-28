package me.vovari2.dungeonps;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DPSLocale {
    private final HashMap<String, Object> localeTexts;
    private final HashMap<String, List<Component>> localeListComponent;
    private final HashMap<String, List<String>> localeListString;

    public DPSLocale(HashMap<String, Object> localeTexts, HashMap<String, List<Component>> localeListComponent, HashMap<String, List<String>> localeListString){
        this.localeTexts = localeTexts;
        this.localeListComponent = localeListComponent;
        this.localeListString = localeListString;
    }

    public static Component getLocaleComponent(String key){
        return (Component) DPS.getInstance().locale.localeTexts.get(key);
    }
    public static List<Component> getLocaleListComponent(String key){
        return DPS.getInstance().locale.localeListComponent.get(key);
    }

    public static String getLocaleString(String key){
        return (String) DPS.getInstance().locale.localeTexts.get(key);
    }
    private static List<String> getLocaleListString(String key){
        return DPS.getInstance().locale.localeListString.get(key);
    }

    public static Component replacePlaceHolder(String key, String placeholder, String value){
        return MiniMessage.miniMessage().deserialize(getLocaleString(key).replaceAll(placeholder, value));
    }
    public static Component replacePlaceHolders(String key, String[] placeholders, String[] values){
        String str = getLocaleString(key);
        for (int i = 0; i < placeholders.length; i++)
            str = str.replaceAll(placeholders[i], values[i]);
        return MiniMessage.miniMessage().deserialize(str);
    }
    public static List<Component> replacePlaceHolderList(String key, String placeholder, String value){
        List<Component> listComponents = new ArrayList<>();
        Iterator<String> iterator = getLocaleListString(key).iterator();
        while(iterator.hasNext())
            listComponents.add(MiniMessage.miniMessage().deserialize(iterator.next().replaceAll(placeholder, value)));
        return listComponents;
    }
}
