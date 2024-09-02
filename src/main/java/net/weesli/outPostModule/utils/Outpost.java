package net.weesli.outPostModule.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@Setter
public class Outpost {

    private String id;
    private UUID owner;
    private int x,z;
    private int time;
    private int farmer_efficiency;

    public Outpost(String id, UUID owner, int x, int z, int time, int farmer_efficiency) {
        this.id = id;
        this.owner = owner;
        this.x = x;
        this.z = z;
        this.time = time;
        this.farmer_efficiency = farmer_efficiency;
    }

    public boolean isOwner(UUID player) {
        return owner.equals(player);
    }

    public boolean contains(Location location) {
        return location.getX() >= x && location.getX() < x + 16 && location.getZ() >= z && location.getZ() < z + 16;
    }

}
