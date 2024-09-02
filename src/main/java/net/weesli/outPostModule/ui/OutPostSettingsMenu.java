package net.weesli.outPostModule.ui;

import lombok.Getter;
import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rozsLib.color.ColorBuilder;
import net.weesli.rozsLib.inventory.ClickableItemStack;
import net.weesli.rozsLib.inventory.InventoryBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

public class OutPostSettingsMenu implements OutpostInventory{

    @Getter private final Outpost outpost;

    public OutPostSettingsMenu(Outpost outpost){
        this.outpost = outpost;
    }

    @Override
    public void openInventory(Player player) {
        InventoryBuilder builder = new InventoryBuilder(OutPostModule.plugin, ColorBuilder.convertColors(OutPostModule.getInstance().getConfig().getFile().getString("menus.settings.title")), OutPostModule.getInstance().getConfig().getFile().getInt("menus.settings.size"));
        ClickableItemStack info = new ClickableItemStack(OutPostModule.plugin, getItemStack("menus.settings.items.info"), builder.build());
        info.setEvent(event -> {});
        info.setCancelled(true);
        ItemMeta infoMeta = info.getItemStack().getItemMeta();
        infoMeta.setLore(infoMeta.getLore().stream().map(line -> line.replaceAll("%owner%", Bukkit.getOfflinePlayer(getOutpost().getOwner()).getName()).replaceAll("%countdown%", OutPostManager.getTimeFormat(outpost.getId())).replaceAll("%farm_efficiency%", String.valueOf(outpost.getFarmer_efficiency()))).toList());
        info.getItemStack().setItemMeta(infoMeta);
        builder.setItem(OutPostModule.getInstance().getConfig().getFile().getInt("menus.settings.items.info.slot"), info);

        ClickableItemStack upgrade = new ClickableItemStack(OutPostModule.plugin, getItemStack("menus.settings.items.upgrade-time"), builder.build());
        upgrade.setEvent(event -> {
            OutPostModule.getInstance().getUiManager().openVerifyMenu(player, VerifyAction.UPGRADE_OUTPOST, outpost.getId());
        });
        upgrade.setCancelled(true);
        ItemMeta upgradeMeta = upgrade.getItemStack().getItemMeta();
        upgradeMeta.setLore(upgradeMeta.getLore().stream().map(line -> line.replaceAll("%cost%", OutPostModule.getInstance().getConfig().getFile().getString("options.outpost-cost"))).toList());
        upgrade.getItemStack().setItemMeta(upgradeMeta);
        builder.setItem(OutPostModule.getInstance().getConfig().getFile().getInt("menus.settings.items.upgrade-time.slot"), upgrade);

        player.openInventory(builder.build());
    }
}
