package me.DevTec.ServerControlReloaded.Events;

import me.DevTec.ServerControlReloaded.SCR.Loader;
import me.DevTec.ServerControlReloaded.Utils.XMaterial;
import me.DevTec.ServerControlReloaded.Utils.setting;
import me.devtec.theapi.TheAPI;
import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.NetherWartsState;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;
import org.bukkit.material.MaterialData;
import org.bukkit.material.NetherWarts;


public class FarmingSystem implements Listener {
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onClick(PlayerInteractEvent e) {
		if (e.isCancelled() || !setting.farming || 
				!e.getPlayer().hasPermission("SCR.Other.FarmingSystem") ||
				e.getAction()!=Action.RIGHT_CLICK_BLOCK ||
				Loader.config.getStringList("Options.FarmingSystem.ForbiddenWorlds").contains(e.getClickedBlock().getWorld().getName()) || 
				e.getAction()==Action.RIGHT_CLICK_BLOCK && !FarmingSystemAccess.hasAccess(e.getPlayer(), e.getClickedBlock().getLocation()))return;
		BlockState s = e.getClickedBlock().getState();
		if (e.getClickedBlock().getType()==XMaterial.NETHER_WART.getMaterial()) {
			MaterialData md = s.getData();
			NetherWarts data = (NetherWarts)md;
			if(data.getState()==NetherWartsState.RIPE) {
			data.setState(NetherWartsState.SEEDED);
			s.setData(data);
			s.update(true,false);
            e.setCancelled(true);
            int random = TheAPI.generateRandomInt(5);
			if (random != 0)
			TheAPI.giveItem(e.getPlayer(), new ItemStack(XMaterial.NETHER_WART.getMaterial(), random));
			}
			return;
		}
		MaterialData md = s.getData();
		if (md instanceof Crops)
			if (((Crops) md).getState() == CropState.RIPE) {
				if (e.getClickedBlock().getType().name().equals("WHEAT")
						|| e.getClickedBlock().getType().name().equals("CROPS")) {
					((Crops) md).setState(CropState.SEEDED);
					s.setData(md);
					s.update(true,false);
		            e.setCancelled(true);
					int random = TheAPI.generateRandomInt(2);
					if (random != 0)
					TheAPI.giveItem(e.getPlayer(), new ItemStack(XMaterial.WHEAT_SEEDS.getMaterial(), random));
					TheAPI.giveItem(e.getPlayer(), new ItemStack(Material.WHEAT, 1));
					return;
				}
				try {
					if (e.getClickedBlock().getType().name().equals("BEETROOTS")) {
						((Crops) md).setState(CropState.SEEDED);
						s.setData(md);
						s.update(true,false);
			            e.setCancelled(true);
						int random = TheAPI.generateRandomInt(3);
						if (random != 0)
						TheAPI.giveItem(e.getPlayer(), new ItemStack(Material.BEETROOT_SEEDS, random));
						TheAPI.giveItem(e.getPlayer(), new ItemStack(Material.BEETROOT, 1));
						return;
					}
				} catch (Exception | NoSuchFieldError ss) {
				}
				try {
					if (e.getClickedBlock().getType().name().contains("POTATO")) {
						((Crops) md).setState(CropState.SEEDED);
						s.setData(md);
						s.update(true,false);
			            e.setCancelled(true);
						int random = TheAPI.generateRandomInt(4);
						if (random != 0)
						TheAPI.giveItem(e.getPlayer(), new ItemStack(XMaterial.POTATO.getMaterial(), random));
						return;
					}
				} catch (Exception | NoSuchFieldError es) {
				}
				try {
					if (e.getClickedBlock().getType().name().contains("CARROT")) {
						((Crops) md).setState(CropState.SEEDED);
						s.setData(md);
						s.update(true,false);
			            e.setCancelled(true);
						int random = TheAPI.generateRandomInt(4);
						if (random != 0)
						TheAPI.giveItem(e.getPlayer(), new ItemStack(XMaterial.CARROT.getMaterial(), random));
						return;
					}
				} catch (Exception | NoSuchFieldError es) {
				}
			}
	}
}
