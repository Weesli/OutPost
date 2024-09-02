package net.weesli.outPostModule.events;

import net.weesli.outPostModule.OutPostModule;
import net.weesli.outPostModule.ui.VerifyAction;
import net.weesli.outPostModule.utils.OutPostManager;
import net.weesli.outPostModule.utils.Outpost;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.RClaimAPI;
import net.weesli.rozsLib.color.ColorBuilder;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerListeners implements Listener {


    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e){
        String message = e.getMessage().toLowerCase();
        if(message.equals("/claim outpost")){
            if (RClaimAPI.getInstance().getClaims().stream().filter(claim -> claim.isOwner(e.getPlayer().getUniqueId())).toList().isEmpty()){
                e.getPlayer().sendMessage(OutPostModule.getInstance().getMessage("no-claim"));
                return;
            }
            if (OutPostManager.getPlayerOutposts(e.getPlayer()).size() >= OutPostModule.getInstance().getConfig().getFile().getInt("options.max-outpost-limit-per-player")){
                e.getPlayer().sendMessage(OutPostModule.getInstance().getMessage("max-outpost"));
                return;
            }
            e.setCancelled(true);
            OutPostModule.getInstance().getUiManager().openOutpostMenu(e.getPlayer());
        }
        if (message.equals("/claim unoutpost")){
            Chunk chunk = e.getPlayer().getLocation().getChunk();
            Outpost outpost = OutPostManager.getPlayerOutposts(e.getPlayer()).stream().filter(target -> target.getX() == chunk.getX() *16 && target.getZ() == chunk.getZ() * 16).findAny().orElse(null);
            if (outpost == null){
                e.getPlayer().sendMessage(OutPostModule.getInstance().getMessage("not-claiming-outpost"));
                return;
            }
            OutPostModule.getInstance().getUiManager().openVerifyMenu(e.getPlayer(), VerifyAction.UNOUTPOST, outpost.getId());
        }
        if (message.equals("/claim outpost list")){
            OutPostModule.getInstance().getUiManager().openOutpostList(e.getPlayer());
            e.setCancelled(true);
        }
        if (message.equals("/adminclaim reload outpost")){
            if (e.getPlayer().isOp()){
                e.setCancelled(true);
                OutPostModule.getInstance().getConfig().reloadFile();
                OutPostModule.getInstance().getMessages().reloadFile();
                e.getPlayer().sendMessage(ColorBuilder.convertColors("&aOutpost files reloaded"));
            }
        }
    }

    @EventHandler
    public void onTab(TabCompleteEvent e){
        if (e.getBuffer().startsWith("/claim")){
            List<String> args = new ArrayList<>(e.getCompletions());
            args.add("outpost");
            args.add("unoutpost");
            e.setCompletions(args);
        }
        if (e.getBuffer().startsWith("/claim outpost")){
            e.setCompletions(List.of("list"));
        }
    }

}
