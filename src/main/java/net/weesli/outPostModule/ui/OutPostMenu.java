package net.weesli.outPostModule.ui;

import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class OutPostMenu implements OutpostInventory{

    @Override
    public void openInventory(Player player) {
        InventoryBuilder builder = new InventoryBuilder(OutPostModule.plugin, ColorBuilder.convertColors(OutPostModule.getInstance().getConfig().getFile().getString("menus.main.title")), OutPostModule.getInstance().getConfig().getFile().getInt("menus.main.size"));
        Chunk chunk = player.getLocation().getChunk();
        ClickableItemStack itemStack = new ClickableItemStack(OutPostModule.plugin, getItemStack("menus.main.item-settings"), builder.build());
        itemStack.setEvent(event -> {
            OutPostManager.buyOutpost(player, chunk.getX() * 16, chunk.getZ() * 16);
            player.closeInventory();
        });
        itemStack.setCancelled(true);
        ItemMeta meta = itemStack.getItemStack().getItemMeta();
        meta.setLore(meta.getLore().stream().map(line -> line.replaceAll("%farm_count%", String.valueOf(OutPostManager.getFarmEfficiency(chunk))).replace("%cost%", OutPostModule.getInstance().getConfig().getFile().getString("options.outpost-cost")).replaceAll("%x%", String.valueOf(chunk.getX() * 16)).replaceAll("%z%", String.valueOf(chunk.getZ() * 16))).toList());
        itemStack.getItemStack().setItemMeta(meta);
        builder.setItem(OutPostModule.getInstance().getConfig().getFile().getInt("menus.main.item-settings.slot"), itemStack);
        player.openInventory(builder.build());
    }
}
