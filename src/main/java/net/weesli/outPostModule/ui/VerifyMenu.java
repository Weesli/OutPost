package net.weesli.outPostModule.ui;

import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.rClaim.RClaim;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class VerifyMenu implements OutpostInventory{

    private VerifyAction action;
    private String object;

    public VerifyMenu(VerifyAction action, String object) {
        this.action = action;
        this.object = object;
    }

    @Override
    public void openInventory(Player player) {
        InventoryBuilder builder = new InventoryBuilder(RClaim.getInstance(), ColorBuilder.convertColors(OutPostModule.getInstance().getConfig().getFile().getString("menus.verify-menu.title")), OutPostModule.getInstance().getConfig().getFile().getInt("menus.verify-menu.size"));
        builder.setItem(OutPostModule.getInstance().getConfig().getFile().getInt("menus.verify-menu.children.confirm.slot"), new ClickableItemStack(RClaim.getInstance(), getItemStack("menus.verify-menu.children.confirm"), builder.build())
                .setEvent(event -> {
                    switch (action){
                        case UPGRADE_OUTPOST -> {
                            if (RClaim.getInstance().getEconomy().hasEnough(player, OutPostModule.getInstance().getConfig().getFile().getInt("options.outpost-cost"))){
                                RClaim.getInstance().getEconomy().withdraw(player, OutPostModule.getInstance().getConfig().getFile().getInt("options.outpost-cost"));
                                int days = OutPostModule.getInstance().getConfig().getFile().getInt("options.outpost-start-time");
                                int seconds = days * 24 * 60 * 60;
                                OutPostModule.getInstance().getTasks().get(object).setTime(OutPostModule.getInstance().getTasks().get(object).getTime() + seconds);
                                player.sendMessage(OutPostModule.getInstance().getMessage("time-upgraded"));
                                player.closeInventory();
                            }else {
                                player.sendMessage(OutPostModule.getInstance().getMessage("not-enough-money"));
                            }
                        }
                        case UNOUTPOST -> {
                            OutPostManager.remove(object);
                            OutPostModule.getInstance().getTasks().get(object).cancel();
                            player.sendMessage(OutPostModule.getInstance().getMessage("unclaimed-outpost"));
                            player.closeInventory();
                        }
                    }
                })
                .setCancelled(true)
                .setSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP));

        builder.setItem(OutPostModule.getInstance().getConfig().getFile().getInt("menus.verify-menu.children.deny.slot"), new ClickableItemStack(RClaim.getInstance(), getItemStack("menus.verify-menu.children.deny"), builder.build())
                .setEvent(event->{
                    OutPostModule.getInstance().getUiManager().openOutpostList(player);
                })
                .setCancelled(true));
        player.openInventory(builder.build());
    }
}
