package me.DevTec.ServerControlReloaded.Events;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.DevTec.ServerControlReloaded.Utils.setting;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.cooldownapi.CooldownAPI;
import me.devtec.theapi.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;


public class SecurityListenerCooldowns implements Listener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void CooldownChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (setting.cool_chat && !p.hasPermission("SCR.Other.Cooldown.Chat")
				&& StringUtils.timeFromString(Loader.config.getString("Options.Cooldowns.Chat.Time")) > 0) {
			CooldownAPI s = TheAPI.getCooldownAPI(p.getName());
			Bukkit.broadcastMessage("Ex: "+s.getCooldown("Cooldown.Msgs")+" ; "+s.getStart("Cooldown.Msgs")+ " ; "+s.getTimeToExpire("Cooldown.Msgs")+" ; "+s.expired("Cooldown.Msgs"));
			if (!s.expired("Cooldown.Msgs")) {
				Loader.sendMessages(p, "Cooldowns.Messages", Placeholder.c().add("%time%", StringUtils.setTimeToString(s.getTimeToExpire("Cooldown.Msgs")*20 )));
				e.setCancelled(true);
			} else {
				s.createCooldown("Cooldown.Msgs", StringUtils.getTimeFromString(Loader.config.getString("Options.Cooldowns.Chat.Time"))/20);
				Bukkit.broadcastMessage("Set: "+Loader.config.getString("Options.Cooldowns.Chat.Time"));
			}
		}
	}

	@EventHandler
	public void CooldownCommands(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if (!p.hasPermission("SCR.Other.Cooldown.Commands")) {
			long time = StringUtils.timeFromString(Loader.config.getString("Options.Cooldowns.Commands.Time"));
			boolean find = false;
			CooldownAPI as = TheAPI.getCooldownAPI(p.getName());
			if (setting.cool_percmd)
				for (String s : Loader.config.getStringList("Options.Cooldowns.Commands.PerCommand.List")) {
					if (!p.hasPermission("SCR.Other.Cooldown.PerCommand."+s)) {
						String[] c = s.split(":");
						if (e.getMessage().replaceFirst("/", "").toLowerCase().startsWith(c[0].toLowerCase())
								|| c[0].equalsIgnoreCase(e.getMessage().replaceFirst("/", ""))) {
							find = true;
							if (as.expired("Cooldown.Cmds." + c[0])) {
								as.createCooldown("Cooldown.Cmds." + c[0], StringUtils.timeFromString(c[1]));
							} else {
								e.setCancelled(true);
								Loader.sendMessages(p, "Cooldowns.Commands", Placeholder.c().add("%time%", StringUtils.setTimeToString(as.getTimeToExpire("Cooldown.Cmds." + c[0]))));
							}
							break;
						}
					}
				}
			if (!find && setting.cool_cmd && time > 0) {
				if (!as.expired("Cooldown.Cmdss")) {
					e.setCancelled(true);
					Loader.sendMessages(p, "Cooldowns.Commands", Placeholder.c().add("%time%", StringUtils.setTimeToString(as.getTimeToExpire("Cooldown.Cmdss"))));
				} else
					as.createCooldown("Cooldown.Cmdss", time);
			}
		}
	}
}