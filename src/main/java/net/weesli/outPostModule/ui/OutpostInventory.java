package net.weesli.outPostModule.ui;

import net.weesli.outPostModule.OutPostModule;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface OutpostInventory {
    void openInventory(Player player);

    default ItemStack getItemStack(String path){
        ItemStack itemStack = new ItemStack(Material.getMaterial(OutPostModule.getInstance().getConfig().getFile().getString(path + ".material")));
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ColorBuilder.convertColors(OutPostModule.getInstance().getConfig().getFile().getString(path + ".title")));
        meta.setLore(OutPostModule.getInstance().getConfig().getFile().getStringList(path + ".lore").stream().map(ColorBuilder::convertColors).collect(java.util.stream.Collectors.toList()));
        if (OutPostModule.getInstance().getConfig().getFile().getInt(path + ".custom-model-data") != 0){
            meta.setCustomModelData(OutPostModule.getInstance().getConfig().getFile().getInt(path + ".custom-model-data"));
        }
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
