package ServerControl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Commands.BanSystem.BanSystem;
import Commands.BanSystem.BanSystem.BanType;
import me.Straiker123.TheAPI;
import me.Straiker123.TheAPI.SudoType;
import me.Straiker123.User;

public class BanSystemAPI {
	 public boolean hasJail(String p) {
		 return BanSystem.isArrested(p);
	 }
	 public boolean hasJail(Player p) {
		 return BanSystem.isArrested(p.getName());
	 }
	 
	 public boolean hasTempJail(String p) {
		if(p==null)return false;
		if(getTempJailStart(p)==-0)return false;
		long time = getTempJailStart(p)/1000 - System.currentTimeMillis()/1000 + getTempJailTime(p);
		return time > 0;
	 }
	 public boolean hasTempJail(Player p) {
		 if(p==null)return false;
			if(getTempJailStart(p.getName())==-0)return false;
			long time = getTempJailStart(p.getName())/1000 - System.currentTimeMillis()/1000 + getTempJailTime(p.getName());
			return time > 0;
	 }
	 public long getTempJailStart(String player) {
			if(player==null)return 0;
			return TheAPI.getUser(player).getLong("TempJail.Start");
	 }
	 public long getTempJailTime(String player) {
			if(player==null)return 0;
			return  TheAPI.getUser(player).getLong("TempJail.Time");
	}

	 public void setJail(CommandSender sender,String player, String jail, String reason) {
		 BanSystem.setPlayer(BanSystem.BanType.JAIL, player, reason, 0, System.currentTimeMillis(), sender);
		 TheAPI.getUser(player).set("Jail.Location", jail);
		BanSystem.kickPlayer(sender,player,BanType.JAIL);
	 }
	 public void setJail(CommandSender sender,Player player, String jail, String reason) {
		 BanSystem.setPlayer(BanSystem.BanType.JAIL, player.getName(), reason, 0, System.currentTimeMillis(), sender);
		 TheAPI.getUser(player).set("Jail.Location", jail);
		BanSystem.kickPlayer(sender,player.getName(),BanType.JAIL);
	 }
	 
	 public void setTempJail(CommandSender sender,String player, String jail, String reason, long time) {
		 BanSystem.setPlayer(BanSystem.BanType.TEMPJAIL, player, reason, time, System.currentTimeMillis(), sender);
		 TheAPI.getUser(player).set("TempJail.Location", jail);
		 BanSystem.kickPlayer(sender,player,BanType.TEMPJAIL);
	 }
	 public void setTempJail(CommandSender sender,Player player, String jail, String reason, long time) {
		 BanSystem.setPlayer(BanSystem.BanType.TEMPJAIL, player.getName(), reason, time, System.currentTimeMillis(), sender);
		 TheAPI.getUser(player).set("TempJail.Location", jail);
		 BanSystem.kickPlayer(sender,player.getName(),BanType.TEMPJAIL);
	 }
	 public void setKick(CommandSender sender,String player, String reason) {
		 BanSystem.setPlayer(BanSystem.BanType.KICK, player, reason, 0, System.currentTimeMillis(), sender);
			BanSystem.kickPlayer(sender,player,BanType.KICK);
	 }
	 public void setKick(CommandSender sender,Player player, String reason) {
		 BanSystem.setPlayer(BanSystem.BanType.KICK, player.getName(), reason, 0, System.currentTimeMillis(), sender);
			BanSystem.kickPlayer(sender,player.getName(),BanType.KICK);
	 }
	 
	public void processBanSystem(String n) {
			Commands.BanSystem.BanSystem.KickMaxWarns(n);
			if (Loader.config.getBoolean("AutoKickLimit.Kick.Use")==true) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(Loader.getInstance, new Runnable() {
				public void run() {
					User s = TheAPI.getUser(n);
	if(s.getInt("Kicks") >= Loader.config.getInt("AutoKickLimit.Kick.Number")) {
		s.set("Kicks" ,s.getInt("Kicks") - Loader.config.getInt("AutoKickLimit.Kick.Number"));
		if(Loader.config.getBoolean("AutoKickLimit.Kick.Message.Use")) {
			if(TheAPI.getPlayer(n)!=null)
			    	for(String cmds: Loader.config.getStringList("AutoKickLimit.Kick.Message.List")) {
			    		Loader.msg(cmds.replace("%player%", n).replace("%number%", ""+Loader.config.getInt("AutoKickLimit.Kick.Number")),TheAPI.getPlayer(n));
	    	}}
	         if(Loader.config.getBoolean("AutoKickLimit.Spam.Commands.Use")==true) {
	    	for(String cmds: Loader.config.getStringList("AutoKickLimit.Spam.Commands.List")) {
			TheAPI.sudoConsole(SudoType.COMMAND, TheAPI.colorize(cmds.replace("%player%", n).replace("%number%", ""+Loader.config.getInt("AutoKickLimit.Kick.Number"))));
		  }}}}}, 20);}
			 if(TheAPI.getPlayer(n)!=null)
			if(BanSystem.getLaterWarn(n)!=null && Loader.ban.getBoolean("Warn."+n+".WarnLater.Wait")==true) {
				Loader.ban.set("Warn."+n+".WarnLater.Wait", false);
				 Loader.msg(BanSystem.getLaterWarn(n), TheAPI.getPlayer(n));
				return;
			}
	 }
	 public void setWarn(String player,CommandSender sender, String reason) {
			int warns = Loader.ban.getInt("Warn."+player+".Amount");
			Loader.ban.set("Warn."+player+".Amount",warns+1);
			Loader.ban.set("Warn."+player+".WarnLater.Reason",reason);
			Loader.ban.set("Warn."+player+".WarnLater.WarnedBy",sender.getName());
			Loader.ban.set("Warn."+player+".WarnLater.Time",(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date())));
			Loader.msg(Loader.s("Prefix")+Loader.s("BanSystem.Warn").replace("%reason%", reason).replace("%player%", player).replace("%playername%", BanSystem.getName(player)).replace("%warnedby%", sender.getName()), TheAPI.getPlayer(player));
			if(TheAPI.getPlayer(player)!=null)
				Loader.msg(Loader.s("Prefix")+Loader.s("BanSystem.Warned").replace("%reason%", reason).replace("%player%", player).replace("%playername%", BanSystem.getName(player)).replace("%warnedby%", sender.getName()), TheAPI.getPlayer(player));
			else
				Loader.ban.set("Warn."+player+".WarnLater.Wait",true);
			BanSystem.KickMaxWarns(player);
	 }

}
