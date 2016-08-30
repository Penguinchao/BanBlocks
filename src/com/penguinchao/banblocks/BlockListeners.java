package com.penguinchao.banblocks;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class BlockListeners implements Listener {
	private BanBlocks main;
	public BlockListeners(BanBlocks banBlocks) {
		main = banBlocks;
		main.getServer().getPluginManager().registerEvents(this, main);
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockUpdate(BlockPhysicsEvent event){
		boolean isBannedBlock = false;
		for(Integer entry : main.bannedBlocks){
			if(event.getBlock().getTypeId() == entry.intValue()){
				main.debugTrace("[onBlockUpdate] "+event.getBlock().getTypeId()+" equals "+entry.intValue());
				isBannedBlock = true;
				break;
			}else{
				main.debugTrace("[onBlockUpdate] "+event.getBlock().getTypeId()+" doesn't equal "+entry.intValue());
			}
		}
		if(isBannedBlock){
			RegionManager regionManager = main.worldGuard.getRegionManager(event.getBlock().getWorld());
			ApplicableRegionSet regionSet = regionManager.getApplicableRegions(event.getBlock().getLocation());
			boolean isInRegion = false;
			for(ProtectedRegion region : regionSet){
				main.debugTrace("[onBlockUpdate] Checking region "+region.getId());
				if(isInRegion){
					main.debugTrace("[onBlockUpdate] Done checking regions");
					break;
				}
				for(String entry : main.permittedRegions){
					if(entry == region.getId()){
						main.debugTrace("[onBlockUpdate] "+entry+" is "+regionSet);
						isInRegion = true;
						break;
					}else{
						main.debugTrace("[onBlockUpdate] "+entry+" is not "+regionSet);
					}
				}
			}
			if(isInRegion){
				main.debugTrace("[onBlockUpdate] In protected region - doing nothing");
			}else{
				main.debugTrace("[onBlockUpdate] Not in protected region - breaking");
				event.getBlock().breakNaturally();
			}
		}
		main.debugTrace("[onBlockUpdate] Done");
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void onPortalTravel(PlayerPortalEvent event){
		main.debugTrace("[onPortalTravel]");
		if(main.getConfig().getBoolean("prevent-portal-travel")){
			main.debugTrace("[onPortalTravel] prevent-portal-travel is true");
			//Check Portal Cause
			TeleportCause cause = event.getCause();
			main.debugTrace("[onPortalTravel] Teleport Cause is "+event.getCause().toString());
			if(cause.equals(TeleportCause.PLUGIN)){
				main.debugTrace("[onPortalTravel] Allowing plugin teleportation");
				main.debugTrace("[onPortalTravel] Done");
				return;
			}else if(cause.equals(TeleportCause.COMMAND)){
				main.debugTrace("[onPortalTravel] Allowing command teleportation");
				main.debugTrace("[onPortalTravel] Done");
				return;
			}else if(cause.equals(TeleportCause.ENDER_PEARL)){
				main.debugTrace("[onPortalTravel] Allowing ender pearl teleportation");
				main.debugTrace("[onPortalTravel] Done");
				return;
			}else if(cause.equals(TeleportCause.UNKNOWN)){
				main.debugTrace("[onPortalTravel] Allowing unknown teleportation");
				main.debugTrace("[onPortalTravel] Done");
				return;
			}else if(cause.toString().contains("MOD")){
				main.debugTrace("[onPortalTravel] Event String is: "+event.toString());
				main.debugTrace("[onPortalTravel] Cause String is: "+cause.toString());
				main.debugTrace("[onPortalTravel] Agent String is: "+event.getPortalTravelAgent().toString());
				Block sender = event.getFrom().getBlock();
				main.debugTrace("[onPortalTravel] Block String is: "+sender.toString());
				main.debugTrace("[onPortalTravel] Material String is: "+sender.getType().toString());
				if(sender.getType().toString().contains("PORTAL") || sender.getType().toString().contains("portal")){
					main.debugTrace("[onPortalTravel] Block is a portal");
				}else{
					main.debugTrace("[onPortalTravel] Block was not a portal");
					main.debugTrace("[onPortalTravel] Done");
					return;
				}
			}else{
				//TODO
			}
			//Check if in allowed region
			Location portalLocation = event.getFrom();
			RegionManager regionManager = main.worldGuard.getRegionManager(portalLocation.getBlock().getWorld());
			ApplicableRegionSet regionSet = regionManager.getApplicableRegions(portalLocation.getBlock().getLocation());
			boolean isInRegion = false;
			for(ProtectedRegion region : regionSet){
				if(isInRegion){
					break;
				}
				for(String entry : main.permittedRegions){
					if(entry == region.getId()){
						isInRegion = true;
						break;
					}
				}
			}
			if(isInRegion){
				main.debugTrace("[onPortalTravel] Player is in allowed region");
			}else{
				main.debugTrace("[onPortalTravel] Player is not in allowed region");
				event.setCancelled(true);
			}
		}else{
			main.debugTrace("[onPortalTravel] prevent-portal-travel is false");
		}
		main.debugTrace("[onPortalTravel] Done");
	}
}