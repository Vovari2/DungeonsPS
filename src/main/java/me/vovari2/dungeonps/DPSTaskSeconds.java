package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.DPSDelayFunction;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import me.vovari2.dungeonps.utils.MenuUtils;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DPSTaskSeconds extends BukkitRunnable {

    public DPSTaskSeconds(){
        waitCooldownNotice = new HashMap<>();
        delayFunctions = new ArrayList<>();

        lockableInventory = new HashMap<>();
    }

    public HashMap<String, Integer> waitCooldownNotice;
    public List<DPSDelayFunction> delayFunctions;
    public int seconds;

    @Override
    public void run() {
        seconds++;
        if (seconds > 3599)
            seconds = 0;

        // Удаление элементов из списка ожидания кулдауна с уведомления
        for (Map.Entry<String, Integer> entry : waitCooldownNotice.entrySet()){
            DPSParty party = DPSParty.get(entry.getKey());
            if (party == null)
                continue;
            DPSPlayer dpsPlayer = party.getPlayer(entry.getKey());

            InventoryView inventoryView = dpsPlayer.getPlayer().getOpenInventory();
            String menuName = MenuUtils.getNameMenu(inventoryView);
            if (menuName == null || !menuName.equals("party_settings"))
                continue;
            MenuUtils.setWaitCooldownNotice(dpsPlayer, inventoryView.getTopInventory());
            if (entry.getValue() == seconds)
                waitCooldownNotice.remove(entry.getKey());
        }

        // Функции, которые должны выполниться по окончанию периода
        delayFunctions.removeIf(delayFunction -> {
            if (delayFunction.getTime() == seconds) {
                delayFunction.launchFunction();
                return true;
            }
            return false;
        });
    }
    public int getSecondAfterPeriod(int period){
        int time = seconds + period;
        return time > 3599 ? time - 3600 : time;
    } // Число, в какую секунду нужно вызвать событие из промежутка 0 - 3599
    public int getSecondsToTime(int time){
        return time < seconds ? 3600 - seconds + time : time - seconds;
    } // Количество секунд, оставшееся до вызова события


    private final HashMap<String, Boolean> lockableInventory;
    public static boolean containsLockableInventory(String playerName){
        return DPS.getTaskSeconds().lockableInventory.get(playerName);
    }
    public static void addLockableInventory(String playerName, boolean value){
        DPS.getTaskSeconds().lockableInventory.put(playerName, value);
    }
    public static void removeLockableInventory(String playerName){
        DPS.getTaskSeconds().lockableInventory.remove(playerName);
    }
}
