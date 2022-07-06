package net.momirealms.customcrops.timer;

import net.momirealms.customcrops.CustomCrops;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class CropTimer {

    private final int taskID;

    public CropTimer(CustomCrops plugin) {
        TimeCheck tc = new TimeCheck(plugin);
        BukkitTask task = tc.runTaskTimer(CustomCrops.instance, 1,1);
        this.taskID = task.getTaskId();
    }

    public void stopTimer(int ID) {
        Bukkit.getScheduler().cancelTask(ID);
        Bukkit.getServer().getScheduler().cancelTask(ID);
    }

    public int getTaskID() {
        return this.taskID;
    }
}