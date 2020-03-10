package Commands;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ServerControl.API;
import ServerControl.Loader;
import me.Straiker123.TheAPI;

public class GamemodeA implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(API.hasPerm(s, "ServerControl.Gamemode.Adventure")) {
			if(args.length==0) {
			if(s instanceof Player) {
				Player p = (Player)s;
				p.setGameMode(GameMode.ADVENTURE);
				Loader.msg(Loader.s("Prefix")+Loader.s("Gamemode.Changed")
				.replace("%gamemode%", "Adventure"),s);
				return true;
			}
			Loader.Help(s,"/Gma <player>","Gamemode");
			return true;
			}
			if(args.length==1) {
				Player p = TheAPI.getPlayer(args[0]);
				if(p!=null) {
				p.setGameMode(GameMode.ADVENTURE);
				Loader.msg(Loader.s("Prefix")+Loader.s("Gamemode.ChangedOther")
				.replace("%gamemode%", "Adventure")
				.replace("%player%", p.getName()).replace("%playername%", p.getDisplayName()),s);
				return true;
			}
				Loader.msg(Loader.PlayerNotOnline(args[0]),s);
				return true;
			}}
		return true;
	}}