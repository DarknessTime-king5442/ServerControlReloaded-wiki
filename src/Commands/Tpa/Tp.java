package Commands.Tpa;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import ServerControl.API;
import ServerControl.Loader;
import me.Straiker123.TheAPI;

public class Tp implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender s, Command arg1, String arg2, String[] args) {
		if(API.hasPerm(s, "ServerControl.Tp")) {
			if(args.length==0) {
				Loader.Help(s, "/Tp <player|player x y z>", "TpaSystem.Tp");
			}
			if(args.length==1) {
				if(s instanceof Player) {
			Player target = TheAPI.getPlayer(args[0]);
			if(target==null) {
				if(TheAPI.getStringUtils().isInt(args[0])) {
					Loader.Help(s, "/Tp <x> <y> <z>", "TpaSystem.Tp");
					return true;
				}else {
				Loader.msg(Loader.PlayerNotOnline(args[0]),s);
				return true;
				}}else {
			if(!Loader.me.getBoolean("Players."+target.getName()+".TpBlock."+s.getName())&&!Loader.me.getBoolean("Players."+target.getName()+".TpBlock-Global")) {
			Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.Teleported").replace("%player%",target.getName()).replace("%playername%", target.getDisplayName()), s);
			API.setBack(((Player) s));
			((Player) s).teleport(target);
			return true;
			}else {
				if(s.hasPermission("ServerControl.Tp.Blocked")) {
					Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.Teleported").replace("%player%",target.getName()).replace("%playername%", target.getDisplayName()), s);
					API.setBack(((Player) s));
					((Player) s).teleport(target);
					return true;
				}else {
					Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.TpBlocked")
					.replace("%playername%", target.getDisplayName())
					.replace("%player%", target.getName()), s);
					return true;
				}
				
				}
			
			}
				}
				if(TheAPI.getPlayer(args[0])==null) {
					if(TheAPI.getStringUtils().isInt(args[0])) {
					Loader.Help(s, "/Tp <player> <x> <y> <z>", "TpaSystem.Tp");
					return true;
				}else
					if(s.hasPermission("ServerControl.Tp.Location"))
					Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
					else
						Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
				return true;
				}else {
					if(s.hasPermission("ServerControl.Tp.Location"))
					Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
					else
						Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
					return true;
				}
		}
		if(args.length==2) {
			Player p0 = TheAPI.getPlayer(args[0]);
			Player p1 = TheAPI.getPlayer(args[1]);
			if(p1==null) {
				if(s instanceof Player) {
			if(p0 != null && TheAPI.getStringUtils().isInt(args[1])) {
				if(s.hasPermission("ServerControl.Tp.Location"))
				Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
				else
					Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
				return true;
			}else 
				if(p0==null && TheAPI.getStringUtils().isInt(args[1])) {
					if(s.hasPermission("ServerControl.Tp.Location"))
					Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
					else
						Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
					return true;
					
			}else {
			Loader.msg(Loader.PlayerNotOnline(args[1]),s);
			return true;
			}}
				if(s.hasPermission("ServerControl.Tp.Location"))
				Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
				else
					Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
			return true;
			}else {
				String player = args[0];
				if(p0!=null)player=p0.getName();
				String playername = args[0];
				if(p0!=null)playername=p0.getDisplayName();
				String player1 = args[1];
				if(p1!=null)player1=p1.getName();
				String playername1 = args[1];
				if(p1!=null)playername1=p1.getDisplayName();
				Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.TpPlayerToPlayer")
				.replace("%firstplayer%", player)
				.replace("%firstplayername%", playername)
				.replace("%lastplayer%",player1)
				.replace("%lastplayername%",playername1), s);
				API.setBack(p0);
				p0.teleport(p1);
				return true;
				
			}
		}
		if(API.hasPerm(s,"ServerControl.Tp.Location")) {
		if(args.length==3) {
			Player p = TheAPI.getPlayer(args[0]);
			if(p==null) {
				if(TheAPI.getStringUtils().isInt(args[0])&&TheAPI.getStringUtils().isInt(args[1])&&TheAPI.getStringUtils().isInt(args[2])) {
					if(s instanceof Player) {
					Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.TpLocation")
					.replace("%playername%", ((Player) s).getDisplayName())
					.replace("%player%", ((Player) s).getName())
					.replace("%world%", ((Player) s).getWorld().getName())
					.replace("%x%", args[0])
					.replace("%y%", args[1])
					.replace("%z%", args[2])
					, s);
					Location loc = new Location(((Player) s).getWorld(),TheAPI.getStringUtils().getDouble(args[0]),TheAPI.getStringUtils().getDouble(args[1]),TheAPI.getStringUtils().getDouble(args[2]));

					API.setBack(((Player) s));
					((Player) s).teleport(loc);
					return true;
					}else {
						if(s.hasPermission("ServerControl.Tp.Location"))
							Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
							else
								Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
					return true;
					}
				}else
					if(s.hasPermission("ServerControl.Tp.Location"))
					Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
					else
						Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
				return true;
			}else
				if(s.hasPermission("ServerControl.Tp.Location"))
				Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
				else
					Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
			return true;
		}
		if(args.length==4) {
			Player p = TheAPI.getPlayer(args[0]);
			if(TheAPI.getStringUtils().isInt(args[0])&&TheAPI.getStringUtils().isInt(args[1])&&TheAPI.getStringUtils().isInt(args[2])) {
				if(s instanceof Player) {
				Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.TpLocation")
				.replace("%world%", ((Player) s).getWorld().getName())
				.replace("%playername%", ((Player) s).getDisplayName())
				.replace("%player%", ((Player) s).getName())
				.replace("%x%", args[0])
				.replace("%y%", args[1])
				.replace("%z%", args[2])
				, s);
				Location loc = new Location(p.getWorld(),TheAPI.getStringUtils().getDouble(args[0]),TheAPI.getStringUtils().getDouble(args[1]),TheAPI.getStringUtils().getDouble(args[2]));
				API.setBack(((Player) s));
				((Player) s).teleport(loc);
				return true;
				}else {
					if(s.hasPermission("ServerControl.Tp.Location"))
					Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
					else
						Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
					return true;
				}
			}else
				if(p!=null &&TheAPI.getStringUtils().isInt(args[0])&&TheAPI.getStringUtils().isInt(args[1])&&TheAPI.getStringUtils().isInt(args[2])) {
					Loader.msg(Loader.s("Prefix")+Loader.s("TpaSystem.TpLocationPlayer")
					.replace("%world%", p.getWorld().getName())
					.replace("%playername%", p.getDisplayName())
					.replace("%player%", p.getName())
					.replace("%x%", args[1])
					.replace("%y%", args[2])
					.replace("%z%", args[3])
					, s);
					Location loc = new Location(p.getWorld(),TheAPI.getStringUtils().getDouble(args[0]),TheAPI.getStringUtils().getDouble(args[1]),TheAPI.getStringUtils().getDouble(args[2]));
					API.setBack(p);
					p.teleport(loc);
					return true;
				}else {
					if(p!=null) {
						if(s.hasPermission("ServerControl.Tp.Location"))
							Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
							else
								Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
						return true;
					}else
						if(s.hasPermission("ServerControl.Tp.Location"))
							Loader.Help(s, "/Tp <player player|player x y z>", "TpaSystem.Tp");
							else
								Loader.Help(s, "/Tp <player> <player>", "TpaSystem.Tp");
					return true;
					}}
		}return true;
		
		}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender s, Command arg1, String arg2, String[] args) {
		List<String> c = new ArrayList<>();
			if(args.length==1||args.length==2)
			return null;
		return c;
	}

}

