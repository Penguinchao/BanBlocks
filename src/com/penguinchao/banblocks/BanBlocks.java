package com.penguinchao.banblocks;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;


public class BanBlocks extends JavaPlugin {
	private boolean debugEnabled;
	protected BlockListeners blockListeners;
	protected List<Integer> bannedBlocks;
	protected List<String> permittedRegions;
	//WorldGuard Hooks
	WorldGuardPlugin worldGuard;
	
	public void onEnable(){
		saveDefaultConfig();
		debugEnabled = getConfig().getBoolean("debug-enabled");
		debugTrace("Debug Enabled");
		debugTrace("Getting WorldGuard");
		worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		if(worldGuard == null){
			getLogger().info("Could not find WorldGuard. Disabling");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		//Initialize Lists
		getLogger().info("Getting Banned Blocks");
		bannedBlocks = getConfig().getIntegerList("banned-blocks");
		getLogger().info("Getting Exempt Regions");
		permittedRegions = getConfig().getStringList("exempt-regions");
		//Create Listeners
		if(bannedBlocks == null){
			getLogger().info("No banned blocks found");
		}else if(bannedBlocks.size() == 0){
			getLogger().info("No banned blocks found");
		}else{
			blockListeners = new BlockListeners(this);
		}
	}
	protected void debugTrace(String message){
		if(debugEnabled){
			getLogger().info("[DEBUG] "+message);
		}
	}
}
