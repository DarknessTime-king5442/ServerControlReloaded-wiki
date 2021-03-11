package me.DevTec.ServerControlReloaded.Events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.DevTec.ServerControlReloaded.Commands.Message.PrivateMessageManager;
import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.SCR.Loader.Placeholder;
import me.DevTec.ServerControlReloaded.Utils.ChatFormatter;
import me.DevTec.ServerControlReloaded.Utils.Colors;
import me.DevTec.ServerControlReloaded.Utils.MultiWorldsGUI;
import me.DevTec.ServerControlReloaded.Utils.Rule;
import me.DevTec.ServerControlReloaded.Utils.TabList;
import me.DevTec.ServerControlReloaded.Utils.setting;
import me.devtec.theapi.TheAPI;
import me.devtec.theapi.utils.ChatMessage;
import me.devtec.theapi.utils.StringUtils;
import me.devtec.theapi.utils.datakeeper.User;
import me.devtec.theapi.utils.json.Writer;
import me.devtec.theapi.utils.nms.NMSAPI;
import me.devtec.theapi.utils.reflections.Ref;

public class ChatFormat implements Listener {
	static Loader plugin = Loader.getInstance;
	static Pattern colorPattern = Pattern.compile("[XxA-Fa-fUu0-9]");

	@SuppressWarnings("unchecked")
	public static Collection<?> colorizeList(Collection<?> json, Player p, String msg) {
		ArrayList<Object> colorized = new ArrayList<>(json.size());
		for (Object e : json) {
			if (e instanceof Collection) {
				colorized.add(colorizeList((Collection<?>) e,p,msg));
				continue;
			}
			if (e instanceof Map) {
				colorized.add(colorizeMap((Map<String, Object>) e,p,msg));
				continue;
			}
			if (e instanceof String) {
				colorized.add(r(p,(String)e,msg, true));
				continue;
			}
			colorized.add(e);
		}
		return colorized;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> colorizeMap(Map<String, Object> jj, Player p, String msg) {
		HashMap<String, Object> json = new HashMap<>(jj.size());
		for (Entry<String, Object> e : jj.entrySet()) {
			if (e.getValue() instanceof Collection) {
				json.put(e.getKey(), colorizeList((Collection<?>) e.getValue(), p, msg));
				continue;
			}
			if (e.getValue() instanceof Map) {
				json.put(e.getKey(), colorizeMap((Map<String, Object>) e.getValue(), p, msg));
				continue;
			}
			if (e.getValue() instanceof String && !e.getKey().equals("color")) {
				json.put(e.getKey(), r(p,(String) e.getValue(),msg, true));
				continue;
			}
			json.put(e.getKey(), e.getValue());
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	public static Object r(Player p, Object s, String msg, boolean usejson) {
		if(s.toString().trim().isEmpty())return s;
		if (usejson && Loader.config.getBoolean("Chat-Groups-Options.Json")) {
			try {
				if(s instanceof Map && s!=null) {
					return colorizeMap((Map<String, Object>) s,p,msg);
				} //else continue in code below
				if(s instanceof Collection && s!=null) {
					return colorizeList((Collection<Object>) s,p,msg);
				} //else continue in code below
			}catch(Exception err) {}
		}
		s=s+"";
		if(usejson)s=s.toString().replace("&u", "<#&>u").replace("&U", "<#&>u");
		String orig = (String)s;
		if(s.toString().toLowerCase().contains("&u")) {
			s=TabList.replace((String) s, p, false);
			if(s==null)s=orig;
			return rainbow((String)s, msg, p);
		}
		s=TabList.replace(s.toString(), p, true);
		if(s==null)s=orig;
		if (msg != null && s.toString().contains("%message%"))
			s=s.toString().replace("%message%", msg);
		return s;
	}

	private static String rainbow(String s, String msg, Player p) {
		List<String> sd = new ArrayList<>();
		StringBuffer d = new StringBuffer();
		s=s.replace("%message%", r(msg, p));
		int found = 0, part = 0, parts = 0;
		String noHex = "";
		for (char c : s.toString().toCharArray()) {
			if(part!=0) {
				if(colorPattern.matcher(c + "").find()) {
					noHex+=c;
					if(++parts==6) {
						part=0;
						noHex="";
						parts=0;
						found=0;
					}
					continue;
				}else {
					d.append(noHex);
					part=0;
					noHex="";
					parts=0;
					found=0;
				}
			}
			if (c == '&'||c == '§') {
				if (found == 1)
					d.append(c);
				found = 1;
				continue;
			}
			if (found == 1 && colorPattern.matcher(c + "").find()) {
				found = 0;
				part=0;
				parts=0;
				sd.add(d.toString());
				d = d.delete(0, d.length());
				noHex="&x";
				d.append("§" + c);
				continue;
			}
			if (found == 1 && (c=='x'||c=='X')) {
				part = 1;
				noHex="&x";
				continue;
			}
			if (found == 1) {
				found = 0;
				d.append("&" + c);
				continue;
			}
			found = 0;
			d.append(c);
		}
		if (d.length() != 0)
			sd.add(d.toString());
		d = d.delete(0, d.length());
		for (String ff : sd) {
			if (ff.toLowerCase().startsWith("§u")) {
				if(ff.contains("%message%") && msg!=null) {
					ff = StringUtils.colorize(StringUtils.color.colorize(ff.substring(2)));
					d.append(ff);
					continue;
				}
				ff = StringUtils.colorize(StringUtils.color.colorize(ff.substring(2)));
			}
			d.append(ff);
		}
		return d.toString();
	}
	
	public static String r(String msg, CommandSender p) {
		if (setting.color_chat)
			return Colors.colorize(msg, false, p);
		else
			return msg;
	}

	private boolean is(String s) {
		for (Player p : TheAPI.getOnlinePlayers()) {
			if (s.equalsIgnoreCase(p.getName()))
				return true;

		}
		return false;
	}

	private int count(String string) {
		int upperCaseCount = 0;
		for (char c : string.toCharArray())
			if (Character.isAlphabetic(c) && Character.isUpperCase(c))
				++upperCaseCount;
		return upperCaseCount;
	}

	private String removeDoubled(String s) {
		char prevchar = 0;
		int count = 0;
		StringBuilder sb = new StringBuilder();
		for (char c : s.toCharArray()) {
			if (prevchar != c) {
				sb.append(c);
				count = 0;
			}else {
				prevchar = c;
				if(++count>=count(c))continue;
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	private int count(char c) {
		return Character.isDigit(c)?6:Character.isAlphabetic(c)?2:4;
	}

	static Map<Player, String> old = new HashMap<>();

	private boolean isSim(Player p, String msg) {
		if (Loader.config.getBoolean("SpamWords.SimiliarMessage")) {
			String o = old.put(p, msg);
			if (o!=null && o.length() >= 5 && msg.length() >= o.length())
				return msg.contains(o.substring(1, o.length() - 1));
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.MONITOR)
	public void chatFormat(AsyncPlayerChatEvent e) {
		if(e.isCancelled())return;
		Player p = e.getPlayer();
		ChatFormatter.setupName(p);
		if (TheAPI.getCooldownAPI(p.getName()).getTimeToExpire("world-create") != -1) {
			e.setCancelled(true);
			if (e.getMessage().toLowerCase().equals("cancel")) {
				User d = TheAPI.getUser(p);
				TheAPI.getCooldownAPI(p.getName()).removeCooldown("world-create");
				d.remove("MultiWorlds-Create");
				d.remove("MultiWorlds-Generator");
				d.save();
				TheAPI.sendTitle(p,"", "&6Cancelled");
			}else
			if (TheAPI.getCooldownAPI(p.getName()).expired("world-create")) {
				TheAPI.getCooldownAPI(p.getName()).removeCooldown("world-create");
				MultiWorldsGUI.openInvCreate(p);
			} else {
				TheAPI.getCooldownAPI(p.getName()).removeCooldown("world-create");
				TheAPI.getUser(p).setAndSave("MultiWorlds-Create", Colors.remove(e.getMessage()));
				MultiWorldsGUI.openInvCreate(p);
			}
			return;
		}
		String msg = e.getMessage();
		if (!p.hasPermission("SCR.Other.Admin")) {
			if (!p.hasPermission("SCR.Other.RulesBypass")) {
		for (Rule rule : Loader.rules) {
			if(!Loader.events.getStringList("onChat.Rules").contains(rule.getName()))continue;
			msg = rule.apply(msg);
			if (msg == null) break;
		}
		if (msg == null) {
			e.setCancelled(true);
			return;
		}}
		String message = msg;
		String d = ""; // anti doubled letters
		int up = 0; // anti caps
		if (setting.spam_double) {
			if (message.split(" ").length == 0) {
				if (!is(message)) {
					up = up + count(message);
					String removed = removeDoubled(message);
					d +=" " + (message.length() - removed.length() >= 5 ? removed : message);
				} else
					d +=" " + message;
			} else
				for (String s : message.split(" ")) {
					if (!is(s)) {
						up = up + count(s);
						String removed = removeDoubled(message);
						d = d + " " + (message.length() - removed.length() >= 5 ? removed : s);
					} else
						d = d + " " + s;
				}
			d = d.replaceFirst(" ", "");
		} else
			d = message;
		String build = d;
		if (setting.caps_chat && !p.hasPermission("SCR.Other.Caps")) {
			if (up != 0 && up / ((double) d.length() / 100) >= 60 && d.length() > 5) {
				build = "";
				if (d.split(" ").length == 0) {
					if (!is(d)) {
						build = build + " " + d.toLowerCase();
					} else
						build = build + " " + d;
				} else
					for (String s : d.split(" ")) {
						if (!is(s)) {
							build = build + " " + s.toLowerCase();
						} else
							build = build + " " + s;
					}
				build = build.replaceFirst(" ", "");
			}
		}
		message = build;
		if (Loader.config.getBoolean("SpamWords.SimiliarMessage") && !p.hasPermission("SCR.Other.SimiliarMessage"))
			if (isSim(p, message)) {
				e.setCancelled(true);
				return;
			}
		msg=message;
		}
		if(PrivateMessageManager.hasChatLock(p)) {
			if(PrivateMessageManager.getLockType(p).equalsIgnoreCase("msg")) {
				PrivateMessageManager.reply(p, msg);
				String r = PrivateMessageManager.getReply(p);
				PrivateMessageManager.setReply(r.equalsIgnoreCase("console")?TheAPI.getConsole():TheAPI.getPlayerOrNull(r), p.getName());
			}else
			if(PrivateMessageManager.getLockType(p).equalsIgnoreCase("helpop")) {
				TheAPI.broadcast(Loader.config.getString("Format.HelpOp").replace("%sender%", p.getName())
						.replace("%sendername%", TheAPI.getPlayerOrNull(p.getName())!=null?TheAPI.getPlayerOrNull(p.getName()).getDisplayName():p.getName()).replace("%message%", msg), Loader.cmds.getString("Message.Helpop.SubPermission.Receive"));
				if (!Loader.has(p, "Helpop", "Message", "Receive"))
					TheAPI.msg(Loader.config.getString("Format.HelpOp").replace("%sender%", p.getName()).replace("%sendername%", TheAPI.getPlayerOrNull(p.getName())!=null?TheAPI.getPlayerOrNull(p.getName()).getDisplayName():p.getName()).replace("%message%", msg), p);
			}
			e.setCancelled(true);
			return;
		}
		if (setting.lock_chat && !Loader.has(p, "ChatLock", "Other")) {
			e.setCancelled(true);
			Loader.sendMessages(p, "ChatLock.IsLocked");
			Loader.sendBroadcasts(p, "ChatLock.Message", Placeholder.c().add("%player%", p.getName())
					.add("%playername%", p.getDisplayName()).add("%message%", msg), Loader.getPerm("ChatLock", "Other"));
			e.setMessage(r(msg, p));
			return;
		}
		Iterator<Player> a = e.getRecipients().iterator();
		while(a.hasNext()) {
			Player d = a.next();
			if(d.equals(p))continue;
			if(PrivateMessageManager.getIgnoreList(d.getName()).contains(p.getName()))a.remove();
		}
		if(Loader.config.getString("Options.Chat.Type").equalsIgnoreCase("per_world")
				||Loader.config.getString("Options.Chat.Type").equalsIgnoreCase("perworld")||
				Loader.config.getString("Options.Chat.Type").equalsIgnoreCase("world")) {
			Iterator<Player> as = e.getRecipients().iterator();
			while(as.hasNext()) {
				Player s = as.next();
				if(p!=s && !s.hasPermission("SCR.Other.ChatTypeBypass")) {
					if(!p.getWorld().equals(s.getWorld()))as.remove();
				}
			}
		}
		if(Loader.config.getString("Options.Chat.Type").equalsIgnoreCase("per_distance")
				||Loader.config.getString("Options.Chat.Type").equalsIgnoreCase("distance")) {
			Iterator<Player> as = e.getRecipients().iterator();
			double distance = Loader.config.getDouble("Options.Chat.Distance");
			while(as.hasNext()) {
				Player s = as.next();
				if(p!=s && !s.hasPermission("SCR.Other.ChatTypeBypass")) {
					if(!p.getWorld().equals(s.getWorld()))as.remove();
					else if(p.getLocation().distance(s.getLocation())>distance)as.remove();
				}
			}
		}
		Object format = ChatFormatter.chat(p, ".");
		String colorOfFormat = StringUtils.colorize(format!=null ? getColorOf(p,format) :"");
		if(Loader.config.getBoolean("Options.ChatNotification.Enabled")) {
			Sound sound = null;
			String[] title = new String[] {Loader.config.getString("Options.ChatNotification.Title"), Loader.config.getString("Options.ChatNotification.SubTitle")};
			String actionbar = Loader.config.getString("Options.ChatNotification.ActionBar").replace("%target%", p.getName()).replace("%targetname%", ChatFormatter.displayName(p)).replace("%targetcustomname%", ChatFormatter.customName(p));
			String color = Loader.config.getString("Options.ChatNotification.Color");
			try {
			sound = Sound.valueOf(Loader.config.getString("Options.ChatNotification.Sound").toUpperCase());
			}catch(Exception | NoSuchFieldError err) {}
			for(Player s : e.getRecipients()) {
				if(p.canSee(s) && p!=s) {
					if(msg.contains(s.getName())) {
						if(msg.equals(s.getName())) {
							msg=StringUtils.colorize(color+s.getName());
							if(sound!=null)
								s.playSound(s.getLocation(), sound, 0, 0);
								if(!(title[0].trim().isEmpty() && title[1].trim().isEmpty()))
									TheAPI.sendTitle(s, title[0].trim().isEmpty()?"":TabList.replace(title[0], s, true), title[1].trim().isEmpty()?"":TabList.replace(title[1], s, true));
								if(!actionbar.trim().isEmpty())TheAPI.sendActionBar(s, TabList.replace(actionbar, s, true));
							break;
						}
						String[] sp = msg.split(s.getName());
						String build = colorOfFormat;
						int added = sp.length-1;
						boolean first = true;
						for(int i = 0; i < sp.length; ++i) {
							String last = first?build+=sp[i]+colorOfFormat:build;
							if(added-->0)
							build+=StringUtils.colorize(color+s.getName())+StringUtils.colorize(StringUtils.getLastColors(last));
							try{
								if(first) {
									first=false;
									build+=sp[++i];
								}else
									build+=sp[i];
							}catch(Exception err) {}
						}
						if(msg.endsWith(s.getName()))
							build+=StringUtils.colorize(color+s.getName());
						msg=build;
						if(sound!=null)
						s.playSound(s.getLocation(), sound, 1,1);
						if(!(title[0].trim().isEmpty() && title[1].trim().isEmpty()))
							TheAPI.sendTitle(s, title[0].trim().isEmpty()?"":TabList.replace(title[0], s, true), title[1].trim().isEmpty()?"":TabList.replace(title[1], s, true));
						if(!actionbar.trim().isEmpty())TheAPI.sendActionBar(s, TabList.replace(actionbar, s, true));
					}
				}
			}
		}
		e.setMessage(r(msg,p));
		if (Loader.config.getBoolean("Chat-Groups-Options.Enabled")) {
			format = ChatFormatter.chat(p, (format instanceof String?StringUtils.colorize(colorOfFormat)+r(msg,p):colorOfFormat+"%message%"));
			if (format != null) {
				if(format instanceof String)
					Ref.set(e, "format", ((String)format).replace("%", "%%"));
				else
				if (Loader.config.getBoolean("Chat-Groups-Options.Json") && (format instanceof Map || format instanceof Collection)) {
					
					List<Map<String,Object>> list = ChatMessage.fixListMap((List<Map<String,Object>>)format);
					ListIterator<Map<String, Object>> aww = list.listIterator();
					while(aww.hasNext()) {
						Map<String, Object> aw = aww.next();
						if(aw.containsKey("text")) {
							if(aw.get("text").toString().contains("%message%")) {
								aww.remove();
								if(aw.get("text").toString().contains("<#&>u")) {
									aw.put("text", rainbow(aw.get("text").toString().replace("<#&>u","&u"), msg, p));
								}
								for(Map<String, Object> sd : new ChatMessage(aw.get("text").toString().replace("%message%", r(msg,p).replaceAll("#([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])", "#<#/>$1$2$3$4$5$6"))).get())
									aww.add(fix(aw,sd));
							}
						}
					}
					Ref.set(e, "format", convertToLegacy(list).replace("%", "%%"));
					if(!e.isCancelled())
					Ref.sendPacket(e.getRecipients(), NMSAPI.getPacketPlayOutChat(NMSAPI.ChatType.SYSTEM, NMSAPI.getIChatBaseComponentJson(Writer.write(list))));
					e.getRecipients().clear(); //for our custom chat
				}
			}
		}
	}

	private String convertToLegacy(List<Map<String, Object>> list) {
		StringBuilder b = new StringBuilder();
		for(Map<String, Object> text : list)
			b.append(StringUtils.colorize(getColor(""+text.getOrDefault("color","")))+text.get("text"));
		return b.toString();
	}
	String getColor(String color) {
		if(color.trim().isEmpty())return "";
		if(color.startsWith("#"))return color;
		try {
		return ChatColor.valueOf(color.toUpperCase())+"";
		}catch(Exception | NoSuchFieldError err) {
			return "";
		}
	}

	private Map<String, Object> fix(Map<String, Object> sdx, Map<String, Object> sd) {
		for(Entry<String, Object> g : sdx.entrySet())
			if(!g.getKey().equals("text") && !sd.containsKey(g.getKey()))
				sd.put(g.getKey(), g.getValue());
		for(Entry<String, Object> f : sd.entrySet()) {
			if(f.getValue() instanceof String) {
				f.setValue(f.getValue().toString().replaceAll("#\\<#\\/\\>([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])([0-9A-Fa-f])", "#$1$2$3$4$5$6"));
			}
		}
		return sd;
	}

	/**
	 * @see see Get last colors from String (HEX SUPPORT!)
	 * @return String
	 */
	public static String getLastColors(String last) {
		String color = "";
		String format = "";
		
		char was = 0;
		int count = 0;
		String hex = "";
		boolean hexPart = false;
		for(char c : last.toCharArray()) {
			if(c=='§') {
				was=c;
				continue;
			}
			if((was=='§')&&(Character.isDigit(c)||c=='a'||c=='b'||c=='c'||c=='d'||c=='e'||c=='f')) {
				if(hexPart) {
					hex+=c;
					if(count++==5) {
						was=c;
						color="#"+hex;
						hex="";
						hexPart=false;
						format="";
						count=0;
					}
					continue;
				}else {
					format="";
					color="&"+c;
					hex="";
					count=0;
				}
			}
			if((was=='§')&&(c=='r'||c=='n'||c=='m'||c=='l'||c=='o'||c=='k'||c=='x')) {
				if(c=='r') {
					format=was+"r";
					count=0;
				}else
				if(was=='#') {
					color="";
					hex="";
					count=0;
					format="§"+c;
				}else {
					if(c=='x') {
						hexPart=true;
						count=0;
						hex="";
						continue;
					}else
						if(!format.contains(("§"+c).toLowerCase()))
					format+=("§"+c).toLowerCase();
					count=0;
				}
			}
			was=c;
		}
		return color+format;
	}

	@SuppressWarnings("unchecked")
	private String getColorOf(Player p, Object format) {
		String text = null;
		if(format instanceof Map) {
			if(((Map<String, Object>) format).containsKey("color") && ((Map<String, Object>) format).containsKey("text")) {
				text=((Map<String, Object>) format).get("color").toString();
			}else {
				if((((Map<String, Object>)format).get("text")+"").contains("%message%")) {
					text=r((((Map<String, Object>) format).get("text")+"").split("\\%message\\%")[0],p);
				}
			}
		}else
		if(format instanceof Collection) {
			for(Object o : ((Collection<?>)format)) {
				if(o instanceof Map) {
					if(((Map<String, Object>) o).containsKey("color") && ((Map<String, Object>) o).containsKey("text")) {
						if((((Map<String, Object>) o).get("text")+"").contains("%message%")) {
						return text=((Map<String, Object>) o).get("color").toString();
						}
						text=((Map<String, Object>) o).get("color").toString();
					}else {
						if((((Map<String, Object>) o).get("text")+"").contains("%message%")) {
							text=r((((Map<String, Object>) o).get("text")+"").split("\\%message\\%")[0],p);
						}
					}
				}else {
					if((""+o).contains("%message%")) {
						text=r((""+o).split("\\%message\\%")[0],p);
					}
				}
			}
		}else
			text=r((""+format).split("\\%message\\%")[0],p);
		if(text==null)text="";
		return StringUtils.getLastColors(text);
	}
}