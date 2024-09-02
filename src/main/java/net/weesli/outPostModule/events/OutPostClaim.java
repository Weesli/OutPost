package net.weesli.outPostModule.events;

import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.ClaimCreateEvent;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import net.weesli.rClaim.utils.ClaimPermission;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class OutPostClaim implements Listener {

    @EventHandler
    public void onClaimCreate(ClaimCreateEvent e){
        OutPostManager.getoutpostList().forEach(outpost -> {
            if (outpost.contains(e.getClaim().getCenter())){
                e.setCancelled(true);
                e.getSender().sendMessage(OutPostModule.getInstance().getMessage("no-create-a-claim-in-outpost"));
            }
        });
    }

    @EventHandler
    public void onClaimDelete(ClaimCreateEvent e){
        if (e.getClaim().isCenter()){
            UUID owner = e.getClaim().getOwner();
            OutPostManager.getoutpostList().forEach(outpost -> {
                if (outpost.getOwner().equals(owner)){
                    OutPostManager.remove(outpost.getId());
                }
            });
        }
    }


    @EventHandler
    public void onBreak(BlockBreakEvent e){
        OutPostManager.getoutpostList().forEach(outpost -> {
            if (outpost.contains(e.getBlock().getLocation())){
                Claim claim = ClaimManager.getPlayerData(outpost.getOwner()).getClaims().get(0);
                if (claim == null){
                    return;
                }
                if (!claim.isOwner(e.getPlayer().getUniqueId()) || claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_BREAK)){
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_BREAK"));
                }
            }
        });
    }

    @EventHandler
    public void onPlace(BlockBreakEvent e){
        OutPostManager.getoutpostList().forEach(outpost -> {
            if (outpost.contains(e.getBlock().getLocation())){
                Claim claim = ClaimManager.getPlayerData(outpost.getOwner()).getClaims().get(0);
                if (claim == null){
                    return;
                }
                if (!claim.isOwner(e.getPlayer().getUniqueId()) || claim.checkPermission(e.getPlayer().getUniqueId(), ClaimPermission.BLOCK_BREAK)){
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(RClaim.getInstance().getMessage("PERMISSION_BLOCK_PLACE"));
                }
            }
        });
    }


    @EventHandler
    public void onFarm(BlockBreakEvent e){
        boolean isOutPost = OutPostManager.isSuitable(e.getBlock().getX(), e.getBlock().getZ());
        if (!isOutPost){
            return;
        }
        int efficiency = OutPostManager.getFarmEfficiency(e.getBlock().getChunk());
        try {
            Ageable ageable = (Ageable) e.getBlock().getBlockData();
            if (ageable.getAge() != 7) {
                e.setDropItems(false);
            }
        }catch (ClassCastException ex){
            return;
        }
    }

    @EventHandler
    public void onDrop(BlockDropItemEvent e){
        boolean isOutPost = OutPostManager.isSuitable(e.getBlock().getChunk().getX() * 16, e.getBlock().getChunk().getZ() * 16);
        if (!isOutPost){
            return;
        }
        int efficiency = OutPostManager.getFarmEfficiency(e.getBlock().getChunk());
        List<ItemStack> itemStacks = e.getItems().stream().map(Item::getItemStack).toList();
        e.getItems().clear();
        for (ItemStack itemStack : itemStacks){
            ItemStack dropped = itemStack.clone();
            System.out.println(dropped.getAmount());
            dropped.setAmount(dropped.getAmount() + getBonus(efficiency));
            System.out.println(dropped.getAmount());
            e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), dropped);
        }
    }


    private int getBonus(int x) {
        Random random = new Random();

        if (x <= 15) {
            return 1;
        } else if (x <= 30) {
            return random.nextBoolean() ? 2 : 1;
        } else if (x <= 50) {
            return random.nextBoolean() ? 3 : 1;
        } else if (x <= 80) {
            return random.nextBoolean() ? 4 : 1;
        } else if (x <= 100) {
            return random.nextBoolean() ? 5 : 1;
        }
        return 1;
    }

}
