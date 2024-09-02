package net.weesli.outPostModule.utils;

import de.tr7zw.nbtapi.NBTChunk;
import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.tasks.OutPostTask;
import net.weesli.rClaim.RClaim;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.weesli.rClaim.management.ClaimManager.IDCreator;

public class OutPostManager {

    public static void remove(String id){
        OutPostModule.getInstance().getDatabase().delete(id);
    }

    public static void buyOutpost(Player player, int x, int z){
        if (isSuitable(x,z)){
            player.sendMessage(OutPostModule.getInstance().getMessage("not-suitable"));
            return;
        }
        if (RClaim.getInstance().getEconomy().hasEnough(player, OutPostModule.getInstance().getConfig().getFile().getInt("options.outpost-cost"))){
            RClaim.getInstance().getEconomy().withdraw(player, OutPostModule.getInstance().getConfig().getFile().getInt("options.outpost-cost"));
            int days = OutPostModule.getInstance().getConfig().getFile().getInt("options.outpost-start-time");
            int seconds = days * 24 * 60 * 60;
            Outpost outpost = new Outpost(IDCreator(), player.getUniqueId(), x, z, seconds, getFarmEfficiency(player.getLocation().getChunk()));
            OutPostModule.getInstance().getTasks().put(outpost.getId(), new OutPostTask(outpost.getId(), seconds));
            add(outpost);
            player.sendMessage(OutPostModule.getInstance().getMessage("outpost-bought"));
        }else {
            player.sendMessage(OutPostModule.getInstance().getMessage("not-enough-money"));
        }
    }

    public static void add(Outpost outpost){
        OutPostModule.getInstance().getDatabase().insert(outpost);
    }

    public static Outpost getById(String id){
        return OutPostModule.getInstance().getDatabase().getOutpost(id);
    }

    public static List<Outpost> getoutpostList(){
        return OutPostModule.getInstance().getDatabase().getAllOutposts();
    }

    public static List<Outpost> getPlayerOutposts(Player player){
        return getoutpostList().stream().filter(outpost -> outpost.isOwner(player.getUniqueId())).toList();
    }

    public static boolean isSuitable(int x, int z){
        Optional<Outpost> founded =  getoutpostList().stream().filter(outpost -> outpost.getX() == x && outpost.getZ() == z).findAny();
        return founded.isPresent();
    }


    public static int getFarmEfficiency(Chunk chunk){
        NBTChunk target = new NBTChunk(chunk);
        if (target.getPersistentDataContainer().hasKey("Farmer_efficiency")){
            return target.getPersistentDataContainer().getInteger("Farmer_efficiency");
        }else {
            Random random = new Random();
            int efficiency;

            int chance = random.nextInt(100) + 1;

            if (chance <= 30) {
                efficiency = 10 + random.nextInt(16);
            } else if (chance <= 60) {
                efficiency = 25 + random.nextInt(26);
            } else if (chance <= 85) {
                efficiency = 50 + random.nextInt(26);
            } else {
                efficiency = 75 + random.nextInt(26);
            }

            target.getPersistentDataContainer().setInteger("Farmer_efficiency",efficiency);
            return target.getPersistentDataContainer().getInteger("Farmer_efficiency");
        }
    }

    public static String getTimeFormat(String id){
        OutPostTask task = OutPostModule.getInstance().getTasks().get(id);
        int totalSeconds = task.getTime();
        long secondsInWeek = 7 * 24 * 60 * 60;
        long secondsInDay = 24 * 60 * 60;
        long secondsInHour = 60 * 60;
        long secondsInMinute = 60;

        long weeks = totalSeconds / secondsInWeek;
        totalSeconds %= secondsInWeek;

        long days = totalSeconds / secondsInDay;
        totalSeconds %= secondsInDay;

        long hours = totalSeconds / secondsInHour;
        totalSeconds %= secondsInHour;

        long minutes = totalSeconds / secondsInMinute;
        long seconds = totalSeconds % secondsInMinute;
        return RClaim.getInstance().getConfig().getString("options.time-format").replaceAll("%week%", String.valueOf(weeks)).replaceAll("%day%", String.valueOf(days)).replaceAll("%hour%", String.valueOf(hours)).replaceAll("%minute%", String.valueOf(minutes)).replaceAll("%second%", String.valueOf(seconds));
    }
}
