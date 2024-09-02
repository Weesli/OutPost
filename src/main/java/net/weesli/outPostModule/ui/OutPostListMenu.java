package net.weesli.outPostModule.ui;

import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

public class OutPostListMenu implements OutpostInventory{


    @Override
    public void openInventory(Player player) {
        InventoryBuilder builder = new InventoryBuilder(OutPostModule.plugin, ColorBuilder.convertColors(OutPostModule.getInstance().getConfig().getFile().getString("menus.outposts.title")),  OutPostModule.getInstance().getConfig().getFile().getInt("menus.outposts.size"));
        int x = 1;
        for (Outpost outpost : OutPostManager.getPlayerOutposts(player)){
            ClickableItemStack itemStack = new ClickableItemStack(OutPostModule.plugin, getItemStack("menus.outposts.item-settings"), builder.build())
                   .setEvent(event -> {
                       OutPostModule.getInstance().getUiManager().openOutpostSettingsMenu(player,outpost);
                   });
            itemStack.setCancelled(true);
            ItemMeta meta = itemStack.getItemStack().getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replaceAll("<count>", String.valueOf(x)));
            meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("%farm_efficiency%", String.valueOf(outpost.getFarmer_efficiency())).replaceAll("%x%", String.valueOf(outpost.getX())).replaceAll("%z%", String.valueOf(outpost.getZ())).replaceAll("%owner%", player.getName())).collect(Collectors.toList()));
            itemStack.getItemStack().setItemMeta(meta);
            builder.addItem(itemStack);
            x++;
        }
        player.openInventory(builder.build());
    }
}
