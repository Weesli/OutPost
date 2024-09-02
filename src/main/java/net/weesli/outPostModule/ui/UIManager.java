package net.weesli.outPostModule.ui;

import lombok.Getter;
import net.weesli.outPostModule.utils.Outpost;
import org.bukkit.entity.Player;

@Getter
public class UIManager {

    private final OutPostMenu main;
    private final OutPostListMenu list;
    private OutPostSettingsMenu settingsMenu;
    private VerifyMenu verifyMenu;

    public UIManager() {
        main = new OutPostMenu();
        list = new OutPostListMenu();
    }

    public void openOutpostMenu(Player player) {
        main.openInventory(player);
    }

    public void openOutpostList(Player player) {
        list.openInventory(player);
    }

    public void openOutpostSettingsMenu(Player player, Outpost outpost) {
        settingsMenu = new OutPostSettingsMenu(outpost);
        settingsMenu.openInventory(player);
    }

    public void openVerifyMenu(Player player, VerifyAction action, String object) {
        verifyMenu = new VerifyMenu(action,object);
        verifyMenu.openInventory(player);
    }

}



