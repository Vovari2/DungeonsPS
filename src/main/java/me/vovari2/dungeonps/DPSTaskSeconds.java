package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.DPSDelayFunction;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import me.vovari2.dungeonps.utils.MenuUtils;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DPSTaskSeconds extends BukkitRunnable {

    public DPSTaskSeconds(){
        waitCooldownNotice = new HashMap<>();
        delayFunctions = new ArrayList<>();
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
            MenuUtils.setWaitCooldownNotice(dpsPlayer, dpsPlayer.getMenuSettings());
            if (entry.getValue() == seconds)
                waitCooldownNotice.remove(entry.getKey());
        }

        // Функции, которые должны выполниться по окончанию периода
        delayFunctions.removeIf(delayFunction -> {
            if (delayFunction.equalsTime()) {
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
}
