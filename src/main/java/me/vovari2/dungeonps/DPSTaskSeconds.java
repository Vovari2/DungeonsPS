package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.DPSDelayFunction;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DPSTaskSeconds extends BukkitRunnable {

    public DPSTaskSeconds(){
        delayFunctions = new HashMap<>();
    }

    public HashMap<String, DPSDelayFunction> delayFunctions;
    public int seconds;

    @Override
    public void run() {
        seconds++;
        if (seconds > 3599)
            seconds = 0;

        Iterator<Map.Entry<String, DPSDelayFunction>> iterator = delayFunctions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, DPSDelayFunction> entry = iterator.next();
            DPSDelayFunction delayFunction = entry.getValue();
            boolean equalsTime = delayFunction.equalsTime();
            if (delayFunction.isAfterPeriod()) {
                if (equalsTime) {
                    iterator.remove();
                    DPSDelayFunction.launchFunction(entry.getKey(), delayFunction.getFunctionName());
                }
            } else {
                if (equalsTime)
                    iterator.remove();
                DPSDelayFunction.launchFunction(entry.getKey(), delayFunction.getFunctionName());
            }
        }
    }
    public int getSecondAfterPeriod(int period){
        int time = seconds + period;
        return time > 3599 ? time - 3600 : time;
    } // Число, в какую секунду нужно вызвать событие из промежутка 0 - 3599
    public int getSecondsToTime(int time){
        return time < seconds ? 3600 - seconds + time : time - seconds;
    } // Количество секунд, оставшееся до вызова события
}
