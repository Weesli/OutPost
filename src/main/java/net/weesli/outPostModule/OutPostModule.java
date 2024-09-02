package net.weesli.outPostModule;

import lombok.Getter;
import net.weesli.outPostModule.configuration.Config;
import net.weesli.outPostModule.configuration.Messages;
import net.weesli.outPostModule.configuration.ModuleConfiguration;
import net.weesli.outPostModule.database.Database;
import net.weesli.outPostModule.database.MySQLDatabase;
import net.weesli.outPostModule.database.SQLiteDatabase;
import net.weesli.outPostModule.events.OutPostClaim;
import net.weesli.outPostModule.events.PlayerListeners;
import net.weesli.outPostModule.tasks.OutPostTask;
import net.weesli.outPostModule.ui.UIManager;
import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rClaim.management.modules.Module;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

@Getter
public final class OutPostModule implements Module {


    private Database database;
    private UIManager uiManager;
    @Getter  private static OutPostModule instance;
    private Map<String, OutPostTask> tasks = new HashMap<>();

    // files

    private ModuleConfiguration config;
    private ModuleConfiguration messages;

    @Override
    public void enable() {
        instance = this;
        uiManager = new UIManager();
        this.plugin.getServer().getPluginManager().registerEvents(new PlayerListeners(),plugin);
        this.plugin.getServer().getPluginManager().registerEvents(new OutPostClaim(),plugin);
        loadDatabase();
        loadFiles();
        startTimers();
    }

    private void loadFiles() {
        config = new Config(this,"OutPost");
        messages = new Messages(this,"messages");
        config.getFile().options().copyDefaults(true);
        messages.getFile().options().copyDefaults(true);
        config.save();
        messages.save();
    }

    private void startTimers() {
        for (Outpost outpost : getDatabase().getAllOutposts()){
            tasks.put(outpost.getId(), new OutPostTask(outpost.getId(),outpost.getTime()));
        }
    }

    private void loadDatabase() {
        String type = plugin.getConfig().getString("options.storage-type");
        switch (type){
            case "MySQL":
                database = new MySQLDatabase();
                break;
            case "SQLite":
                database = new SQLiteDatabase();
                break;
            default:
                Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("[OutPost] Invalid storage type!"));
                Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    @Override
    public String getAddonName() {
        return "OutPost";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }


    public String getMessage(String path){
        return ColorBuilder.convertColors(plugin.getConfig().getString("options.prefix") + getMessages().getFile().getString(path));
    }
}
