package me.vovari2.dungeonps.objects;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class DPSItemPH {
    private final ItemStack item;
    private final String name;
    private final List<String> lore;
    private final String[] namePlaceholders;
    private final String[] lorePlaceholders;

    public DPSItemPH(Material material, String name, List<String> lore, int customModelData, String[] namePlaceholders, String[] lorePlaceholders){
        item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setCustomModelData(customModelData);
        item.setItemMeta(itemMeta);

        this.name = name;
        this.lore = lore;

        this.namePlaceholders = namePlaceholders;
        this.lorePlaceholders = lorePlaceholders;
    }

    // Получение предмета с плейсхолдерами
    public ItemStack getItem(String[] nameValues, String[] loreValues){
        ItemStack newItem = item.clone();
        ItemMeta itemMeta = item.getItemMeta().clone();
        itemMeta.displayName(replacePlaceHolders(name, namePlaceholders, nameValues));
        itemMeta.lore(replacePlaceHoldersInList(lore, lorePlaceholders, loreValues));
        newItem.setItemMeta(itemMeta);
        return newItem;
    }

    // Получение предмета с плейсхолдерами головы игрока
    public ItemStack getItem(Player player, String[] nameValues, String[] loreValues){
        ItemStack newItem = item.clone();
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta().clone();
        itemMeta.setOwningPlayer(player);
        itemMeta.displayName(replacePlaceHolders(name, namePlaceholders, nameValues));
        itemMeta.lore(replacePlaceHoldersInList(lore, lorePlaceholders, loreValues));
        newItem.setItemMeta(itemMeta);
        return newItem;
    }


    private Component replacePlaceHolders(String name, String[] placeholders, String[] values){
        for (int i = 0; i < placeholders.length; i++)
            name = name.replaceAll(placeholders[i], values[i]);
        return MiniMessage.miniMessage().deserialize(name);
    }
    private List<Component> replacePlaceHoldersInList(List<String> lore, String[] placeholders, String[] values){
        List<Component> newLore = new ArrayList<>();
        for (String strLore : lore)
            newLore.add(replacePlaceHolders(strLore, placeholders, values));
        return newLore;
    }
}
