package enhancedportals.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import enhancedportals.EnhancedPortals;
import enhancedportals.block.BlockDecorBorderedQuartz;
import enhancedportals.block.BlockDecorEnderInfusedMetal;
import enhancedportals.block.BlockFrame;
import enhancedportals.block.BlockPortal;
import enhancedportals.block.BlockStabilizer;
import enhancedportals.block.BlockStabilizerEmpty;
import enhancedportals.crafting.ThermalExpansion;
import enhancedportals.crafting.Vanilla;
import enhancedportals.item.ItemBlankPortalModule;
import enhancedportals.item.ItemBlankUpgrade;
import enhancedportals.item.ItemFrame;
import enhancedportals.item.ItemGlasses;
import enhancedportals.item.ItemLocationCard;
import enhancedportals.item.ItemManual;
import enhancedportals.item.ItemNanobrush;
import enhancedportals.item.ItemPortalModule;
import enhancedportals.item.ItemStabilizer;
import enhancedportals.item.ItemUpgrade;
import enhancedportals.item.ItemWrench;
import enhancedportals.item.PotionFeatherfall;
import enhancedportals.network.packet.PacketGui;
import enhancedportals.network.packet.PacketGuiData;
import enhancedportals.network.packet.PacketRequestGui;
import enhancedportals.network.packet.PacketRerender;
import enhancedportals.network.packet.PacketTextureData;
import enhancedportals.portal.NetworkManager;
import enhancedportals.tile.TileController;
import enhancedportals.tile.TileDialingDevice;
import enhancedportals.tile.TileFrameBasic;
import enhancedportals.tile.TileNetworkInterface;
import enhancedportals.tile.TilePortal;
import enhancedportals.tile.TilePortalManipulator;
import enhancedportals.tile.TileRedstoneInterface;
import enhancedportals.tile.TileStabilizer;
import enhancedportals.tile.TileStabilizerMain;
import enhancedportals.tile.TileTransferEnergy;
import enhancedportals.tile.TileTransferFluid;
import enhancedportals.tile.TileTransferItem;
import enhancedportals.utility.CreativeTabEP3;

public class CommonProxy {
    public static int CONFIG_REDSTONE_FLUX_COST = 10000, CONFIG_REDSTONE_FLUX_TIMER = 20;
    public static boolean CONFIG_FORCE_FRAME_OVERLAY, CONFIG_DISABLE_SOUNDS, CONFIG_DISABLE_PARTICLES, CONFIG_PORTAL_DESTROYS_BLOCKS, CONFIG_FASTER_PORTAL_COOLDOWN, CONFIG_REQUIRE_POWER, CONFIG_UPDATE_NOTIFIER, CONFIG_RECIPES_VANILLA, CONFIG_RECIPES_TE;
    public static double CONFIG_POWER_MULTIPLIER, CONFIG_POWER_STORAGE_MULTIPLIER;
    public static int CONFIG_PORTAL_CONNECTIONS_PER_ROW = 2;

    public int gogglesRenderIndex = 0;
    public NetworkManager networkManager;
    static Configuration config;
    public static String UPDATE_LATEST_VER;
    public static final Logger logger = LogManager.getLogger("EnhancedPortals");
    public static final CreativeTabs creativeTab = new CreativeTabEP3();
    
    public static Potion featherfallPotion;

    public void waitForController(ChunkCoordinates controller, ChunkCoordinates frame) {

    }

    public ArrayList<ChunkCoordinates> getControllerList(ChunkCoordinates controller) {
        return null;
    }

    public void clearControllerList(ChunkCoordinates controller) {

    }

    public File getBaseDir() {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getFile(".");
    }

    public File getResourcePacksDir() {
        return new File(getBaseDir(), "resourcepacks");
    }

    public File getWorldDir() {
        return new File(getBaseDir(), DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
    }

    public void miscSetup() {
        ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(ItemPortalModule.instance, 1, 4), 1, 1, 2));
    }

    public void registerBlocks() {
        GameRegistry.registerBlock(new BlockFrame("frame"), ItemFrame.class, "frame");
        GameRegistry.registerBlock(new BlockPortal("portal"), "portal");
        GameRegistry.registerBlock(new BlockStabilizer("dbs"), ItemStabilizer.class, "dbs");
        GameRegistry.registerBlock(new BlockDecorBorderedQuartz("decor_frame"), "decor_frame");
        GameRegistry.registerBlock(new BlockDecorEnderInfusedMetal("decor_dbs"), "decor_dbs");
        GameRegistry.registerBlock(new BlockStabilizerEmpty("dbs_empty"), "dbs_empty");
    }

    public void registerItems() {
        GameRegistry.registerItem(new ItemWrench("wrench"), "wrench");
        GameRegistry.registerItem(new ItemNanobrush("nanobrush"), "nanobrush");
        GameRegistry.registerItem(new ItemGlasses("glasses"), "glasses");
        GameRegistry.registerItem(new ItemLocationCard("location_card"), "location_card");
        GameRegistry.registerItem(new ItemPortalModule("portal_module"), "portal_module");
        GameRegistry.registerItem(new ItemUpgrade("upgrade"), "upgrade");
        GameRegistry.registerItem(new ItemBlankPortalModule("blank_portal_module"), "blank_portal_module");
        GameRegistry.registerItem(new ItemBlankUpgrade("blank_upgrade"), "blank_upgrade");
        GameRegistry.registerItem(new ItemManual("manual"), "manual");
        
        if (CONFIG_RECIPES_TE && Loader.isModLoaded(EnhancedPortals.MODID_THERMALEXPANSION)) {
            ThermalExpansion.registerItems();
            ThermalExpansion.registerMachineRecipes();
        }
    }
    
    public void registerPotions() {
        Potion[] potionTypes = null;

        for (Field f : Potion.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
                    Field modfield = Field.class.getDeclaredField("modifiers");
                    modfield.setAccessible(true);
                    modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    potionTypes = (Potion[])f.get(null);
                    final Potion[] newPotionTypes = new Potion[256];
                    System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
                    f.set(null, newPotionTypes);
                }
            } catch (Exception e) {
                System.err.println("Severe error, please report this to the mod author:");
                System.err.println(e);
            }
        }
        
        featherfallPotion = new PotionFeatherfall(40, false, 0);
    }

    public void registerPackets() {
        EnhancedPortals.packetPipeline.registerPacket(PacketRequestGui.class);
        EnhancedPortals.packetPipeline.registerPacket(PacketTextureData.class);
        EnhancedPortals.packetPipeline.registerPacket(PacketRerender.class);
        EnhancedPortals.packetPipeline.registerPacket(PacketGuiData.class);
        EnhancedPortals.packetPipeline.registerPacket(PacketGui.class);
    }

    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TilePortal.class, "epP");
        GameRegistry.registerTileEntity(TileFrameBasic.class, "epF");
        GameRegistry.registerTileEntity(TileController.class, "epPC");
        GameRegistry.registerTileEntity(TileRedstoneInterface.class, "epRI");
        GameRegistry.registerTileEntity(TileNetworkInterface.class, "epNI");
        GameRegistry.registerTileEntity(TileDialingDevice.class, "epDD");
        GameRegistry.registerTileEntity(TilePortalManipulator.class, "epMM");
        GameRegistry.registerTileEntity(TileStabilizer.class, "epDBS");
        GameRegistry.registerTileEntity(TileStabilizerMain.class, "epDBSM");
        GameRegistry.registerTileEntity(TileTransferEnergy.class, "epTE");
        GameRegistry.registerTileEntity(TileTransferFluid.class, "epTF");
        GameRegistry.registerTileEntity(TileTransferItem.class, "epTI");
    }

    public void setupConfiguration(File c) {
        config = new Configuration(c);
        CONFIG_FORCE_FRAME_OVERLAY = config.get("Misc", "ForceShowFrameOverlays", false).getBoolean(false);
        CONFIG_DISABLE_SOUNDS = config.get("Overrides", "DisableSounds", false).getBoolean(false);
        CONFIG_DISABLE_PARTICLES = config.get("Overrides", "DisableParticles", false).getBoolean(false);
        CONFIG_PORTAL_DESTROYS_BLOCKS = config.get("Portal", "PortalsDestroyBlocks", true).getBoolean(true);
        CONFIG_FASTER_PORTAL_COOLDOWN = config.get("Portal", "FasterPortalCooldown", false).getBoolean(false);
        CONFIG_REQUIRE_POWER = config.get("Power", "RequirePower", true).getBoolean(true);
        CONFIG_POWER_MULTIPLIER = config.get("Power", "PowerMultiplier", 1.0).getDouble(1.0);
        CONFIG_POWER_STORAGE_MULTIPLIER = config.get("Power", "DBSPowerStorageMultiplier", 1.0).getDouble(1.0);
        CONFIG_PORTAL_CONNECTIONS_PER_ROW = config.get("Portal", "ActivePortalsPerRow", 2).getInt(2);
        CONFIG_UPDATE_NOTIFIER = config.get("Misc", "NotifyOfUpdates", true).getBoolean(true);
        CONFIG_RECIPES_VANILLA = config.get("Crafting", "Vanilla", true).getBoolean(true);
        CONFIG_RECIPES_TE = config.get("Crafting", "ThermalExpansion", true).getBoolean(true);

        config.save();

        if (CONFIG_POWER_MULTIPLIER < 0)
            CONFIG_REQUIRE_POWER = false;

        if (CONFIG_POWER_STORAGE_MULTIPLIER < 0.01)
            CONFIG_POWER_STORAGE_MULTIPLIER = 0.01;

        try {
            URL versionIn = new URL(EnhancedPortals.UPDATE_URL);
            BufferedReader in = new BufferedReader(new InputStreamReader(versionIn.openStream()));
            UPDATE_LATEST_VER = in.readLine();

            if (FMLCommonHandler.instance().getSide() == Side.SERVER && !UPDATE_LATEST_VER.equals(EnhancedPortals.MOD_VERSION))
                logger.info("You're using an outdated version (v" + EnhancedPortals.MOD_VERSION + "). The newest version is: " + UPDATE_LATEST_VER);
        } catch (Exception e) {
            logger.warn("Unable to get the latest version information");
            UPDATE_LATEST_VER = EnhancedPortals.MOD_VERSION;
        }
    }

    public static boolean Notify(EntityPlayer player, String lateVers) {
        if (CONFIG_UPDATE_NOTIFIER == true) {
            player.addChatMessage(new ChatComponentText("Enhanced Portals has been updated to v" + lateVers + " :: You are running v" + EnhancedPortals.MOD_VERSION));
            return true;
        } else {
            logger.info("You're using an outdated version (v" + EnhancedPortals.MOD_VERSION + ")");
            return false;
        }
    }

    public void setupCrafting() {
        if (CONFIG_RECIPES_VANILLA)
            Vanilla.registerRecipes();
        if (CONFIG_RECIPES_TE && Loader.isModLoaded(EnhancedPortals.MODID_THERMALEXPANSION))
            ThermalExpansion.registerRecipes();
    }
}
