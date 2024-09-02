package net.weesli.outPostModule.tasks;

import lombok.Getter;
import lombok.Setter;
import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.outPostModule.utils.Outpost;
import org.bukkit.scheduler.BukkitRunnable;

@Getter@Setter
public class OutPostTask extends BukkitRunnable {

    private String id;
    private int time;

    public OutPostTask(String id, int time){
        this.id = id;
        this.time = time;
        runTaskTimerAsynchronously(OutPostModule.plugin, 0, 20);
    }


    @Override
    public void run() {
        if(time <= 0){
            OutPostManager.remove(id);
            cancel();
        }
        for (int time = 0; time <= (30 * 24 * 60 * 60); time += 250) {
            Outpost outpost = OutPostManager.getById(id);
            if (outpost != null){
                OutPostModule.getInstance().getDatabase().updateTime(outpost);
            }

        }
        time--;
    }
}
