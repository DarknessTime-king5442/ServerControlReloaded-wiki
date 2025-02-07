package me.DevTec.ServerControlReloaded.Modules.Mirror;

import me.devtec.theapi.utils.Position;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class MirrorEvents implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(!MirrorManager.isMirroring(p)) return;
		MirrorType type = MirrorManager.getType(p);
		Block block = e.getBlock();
		MirrorManager.mirrorPlace(p, type, block);
		
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(!MirrorManager.isMirroring(p)) return;
		MirrorType type = MirrorManager.getType(p);
		Position loc = MirrorManager.getLocation(p);
		Block block = e.getBlock();
		new Position(block).setTypeAndUpdate(Material.AIR);
		e.setCancelled(true);
		if(type==MirrorType.AXISX || type==MirrorType.AXISZ) {
			int axis, bl, v;
			if(type==MirrorType.AXISX) {
				axis=loc.getBlockZ();
				bl=block.getZ();
			}
			else  {
				axis=loc.getBlockX();
				bl=block.getX();
			}
			if(axis==bl) return;
			v= axis+(axis-bl);
			Position n;
			if(type==MirrorType.AXISX)
				n = new Position(loc.getWorld(), block.getX(), block.getY(), v);
			else
				n = new Position(loc.getWorld(), v, block.getY(), block.getZ());
			n.setTypeAndUpdate(Material.AIR);
		}
		if(type==MirrorType.CENTER) {
			int axisX, axisZ; // Souřadnice os X a Z
			int vX, vZ; // Výsledný rozdíl mezi osou a položeným blockem
			Position loc1, loc2, loc3;
			axisX = loc.getBlockX();
			axisZ = loc.getBlockZ();
			
			vZ = axisZ- block.getZ();
			vX = axisX- block.getX();
			loc1 = new Position( loc.getWorld(), axisX+vX, block.getY(), block.getZ());
			loc2 = new Position( loc.getWorld(), block.getX(), block.getY(), axisZ+vZ );
			loc3 = new Position( loc.getWorld(), axisX+vX , block.getY(), axisZ+vZ );

			loc1.setTypeAndUpdate(Material.AIR);
			loc2.setTypeAndUpdate(Material.AIR);
			loc3.setTypeAndUpdate(Material.AIR);
		}
	}
	@EventHandler
	public void eventSignChanged(SignChangeEvent event){
		MirrorSigns.setSigns(event.getPlayer(), event.getBlock(), event.getLines());
	}
	
	
}
