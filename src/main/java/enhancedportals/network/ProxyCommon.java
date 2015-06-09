package enhancedportals.network;

import net.minecraft.creativetab.CreativeTabs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.registry.GameRegistry;
import enhancedportals.EnhancedPortals;
import enhancedportals.base.CreativeTab;
import enhancedportals.items.FrameItem;
import enhancedportals.machines.StabilizerControllerTileEntity;
import enhancedportals.machines.StabilizerTileEntity;
import enhancedportals.portal.structure.SBFrame;
import enhancedportals.portal.structure.STController;
import enhancedportals.portal.structure.STDiallingDevice;
import enhancedportals.portal.structure.STFrame;
import enhancedportals.portal.structure.STNetworkInterface;
import enhancedportals.portal.structure.STPortalManipulator;
import enhancedportals.portal.structure.STRedstoneInterface;
import enhancedportals.portal.structure.STTransferEnergy;
import enhancedportals.portal.structure.STTransferFluid;
import enhancedportals.portal.structure.STTransferItem;
import enhancedportals.util.WorldCoordinates;

public class ProxyCommon {
	public static final Logger logger = LogManager.getLogger(EnhancedPortals.MOD_NAME);
	public static final CreativeTabs creativeTab = new CreativeTab();
	
	// CONFIG
	public static int CONFIG_FLUX_COST = 10000;
	public static int CONFIG_FLUX_TIMER = 20;
	
	public static byte CONFIG_CT_LEVEL = 2; // 0 off, 1 fast, 2 adv
	public static byte CONFIG_PLAYER_COOLDOWN_RATE = 10;
	
	public static boolean CONFIG_DBS = true;    // Dimensional Bridge Stabilizer
	public static boolean CONFIG_UPDATE_NOTIFIER = true;
	
	// UPDATE
	public static String UPDATE_LATEST_VER;
	
	// TESTING -- REMOVE ME
	public static WorldCoordinates first;
	
	/** Handles all pre initialization<br>
	 * Packet, Block, TE, Item registration **/
	public void pre() {
		registerBlocks();
		registerTileEntities();
		registerItems();
		registerPackets();
	}
	
	/** Handles initialization **/
	public void init() {
		
	}
	
	/** Handles post initialization **/
	public void post() {
		
	}
	
	/** Registers all blocks **/
	public void registerBlocks() {
		GameRegistry.registerBlock(new SBFrame("frame"), FrameItem.class, "frame");
	}
	
	/** Registers all Tile Entities **/
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(STFrame.class, "epF");
		GameRegistry.registerTileEntity(STController.class, "epPC");
        GameRegistry.registerTileEntity(STRedstoneInterface.class, "epRI");
        GameRegistry.registerTileEntity(STNetworkInterface.class, "epNI");
        GameRegistry.registerTileEntity(STDiallingDevice.class, "epDD");
        GameRegistry.registerTileEntity(STPortalManipulator.class, "epMM");
        GameRegistry.registerTileEntity(StabilizerTileEntity.class, "epDBS");
        GameRegistry.registerTileEntity(StabilizerControllerTileEntity.class, "epDBSM");
        GameRegistry.registerTileEntity(STTransferEnergy.class, "epTE");
        GameRegistry.registerTileEntity(STTransferFluid.class, "epTF");
        GameRegistry.registerTileEntity(STTransferItem.class, "epTI");
	}
	
	/** Registers all items **/
	public void registerItems() {
		
	}
	
	/** Registers all network packets **/
	public void registerPackets () {
		
	}
	
	/** Registers all recipes **/
	public void registerRecipes() {
		
	}
	
	public void checkForUpdates() {
		if (!CONFIG_UPDATE_NOTIFIER) return;
		
		// TODO
	}
}
