package enhanced.portals.network;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import enhanced.base.mod.BaseProxy;
import enhanced.base.network.packet.PacketGui;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.xmod.ThermalExpansion;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.item.ItemDiamondNugget;
import enhanced.portals.item.ItemFrame;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.network.packet.PacketRerender;
import enhanced.portals.network.packet.PacketTextureData;
import enhanced.portals.portal.NetworkManager;
import enhanced.portals.tile.TileController;
import enhanced.portals.tile.TileDialingDevice;
import enhanced.portals.tile.TileFrameBasic;
import enhanced.portals.tile.TileNetworkInterface;
import enhanced.portals.tile.TilePortal;
import enhanced.portals.tile.TilePortalManipulator;
import enhanced.portals.tile.TileRedstoneInterface;
import enhanced.portals.tile.TileTransferEnergy;
import enhanced.portals.tile.TileTransferFluid;
import enhanced.portals.tile.TileTransferItem;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPConfiguration;
import enhanced.portals.utility.Reference.EPItems;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.PortalFrames;

public class ProxyCommon extends BaseProxy {
    public NetworkManager networkManager;

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

    @Override
    public void registerBlocks() {
        GameRegistry.registerBlock(EPBlocks.frame, ItemFrame.class, "frame");
        GameRegistry.registerBlock(EPBlocks.portal, "portal");
        //GameRegistry.registerBlock(EPBlocks.dimensionalBridgeStabilizer, ItemStabilizer.class, "dbs");
        GameRegistry.registerBlock(EPBlocks.decorBorderedQuartz, "decor_frame");
        GameRegistry.registerBlock(EPBlocks.decorEnderInfusedMetal, "decor_dbs");
        GameRegistry.registerBlock(EPBlocks.dimensionalBridgeStabilizerEmpty, "dbs_empty");
    }

    @Override
    public void registerItems() {
        GameRegistry.registerItem(EPItems.wrench, "wrench");
        GameRegistry.registerItem(EPItems.nanobrush, "nanobrush");
        GameRegistry.registerItem(EPItems.glasses, "glasses");
        GameRegistry.registerItem(EPItems.locationCard, "location_card");
        GameRegistry.registerItem(EPItems.portalModule, "portal_module");
        GameRegistry.registerItem(EPItems.upgrade, "upgrade");
        GameRegistry.registerItem(EPItems.portalModuleBlank, "blank_portal_module");
        GameRegistry.registerItem(EPItems.upgradeBlank, "blank_upgrade");
        GameRegistry.registerItem(EPItems.manual, "manual");

        if (EPConfiguration.recipeTE && Loader.isModLoaded(ThermalExpansion.MOD_ID)) {
            GameRegistry.registerItem(new ItemDiamondNugget("diamondNugget"), "nuggetDiamond");
            OreDictionary.registerOre("nuggetDiamond", EPItems.nuggetDiamond);
            GameRegistry.addShapelessRecipe(new ItemStack(EPItems.nuggetDiamond, 9), Items.diamond);
            GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond, 1), EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond, EPItems.nuggetDiamond);

            ThermalExpansion.addTransposerFill(10000, new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.REDSTONE.ordinal()), new FluidStack(FluidRegistry.getFluidID("redstone"), 400), false);
            ThermalExpansion.addTransposerFill(10000, new ItemStack(EPItems.upgradeBlank, 1, 0), new ItemStack(EPItems.upgrade, 1, 0), new FluidStack(FluidRegistry.getFluidID("redstone"), 400), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.NETWORK.ordinal()), new FluidStack(FluidRegistry.getFluidID("ender"), 250), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(EPItems.upgradeBlank, 1, 1), new ItemStack(EPItems.upgrade, 1, 1), new FluidStack(FluidRegistry.getFluidID("ender"), 250), false);
            //ThermalExpansion.addTransposerFill(15000, new ItemStack(EPBlocks.dimensionalBridgeStabilizerEmpty, 1, 0), new ItemStack(EPBlocks.dimensionalBridgeStabilizer, 1, 0), new FluidStack(FluidRegistry.getFluidID("ender"), 125), false);
        }
    }

    @Override
    public void registerPackets() {
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketTextureData.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketRerender.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketGuiData.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketGui.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketRequestGui.class);
    }

    @Override
    public void registerTileEntities() {
        GameRegistry.registerTileEntity(TilePortal.class, "epP");
        GameRegistry.registerTileEntity(TileFrameBasic.class, "epF");
        GameRegistry.registerTileEntity(TileController.class, "epPC");
        GameRegistry.registerTileEntity(TileRedstoneInterface.class, "epRI");
        GameRegistry.registerTileEntity(TileNetworkInterface.class, "epNI");
        GameRegistry.registerTileEntity(TileDialingDevice.class, "epDD");
        GameRegistry.registerTileEntity(TilePortalManipulator.class, "epMM");
        //GameRegistry.registerTileEntity(TileStabilizer.class, "epDBS");
        //GameRegistry.registerTileEntity(TileStabilizerMain.class, "epDBSM");
        GameRegistry.registerTileEntity(TileTransferEnergy.class, "epTE");
        GameRegistry.registerTileEntity(TileTransferFluid.class, "epTF");
        GameRegistry.registerTileEntity(TileTransferItem.class, "epTI");
    }

    @Override
    public void init() {

    }

    @Override
    public void postInit() {
        ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(EPItems.portalModule, 1, 4), 1, 1, 2));
    }

    @Override
    public void registerRecipes() {
        if (EPConfiguration.recipeVanilla) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.frame, 4, PortalFrames.BASIC.ordinal()), new Object[] { "SIS", "IQI", "SIS", 'S', Blocks.stone, 'Q', Blocks.quartz_block, 'I', Items.iron_ingot }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.REDSTONE.ordinal()), new Object[] { " R ", "RFR", " R ", 'F', new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), 'R', Items.redstone }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.NETWORK.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), Items.ender_pearl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.upgrade, 1, 0), new Object[] { " R ", "RFR", " R ", 'F', new ItemStack(EPItems.upgradeBlank), 'R', Items.redstone }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 1), new ItemStack(EPItems.upgradeBlank), Items.ender_pearl));
            //GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.dimensionalBridgeStabilizer, 6), new Object[] { "QPQ", "PDP", "QPQ", 'D', Items.diamond, 'Q', Blocks.iron_block, 'P', Items.ender_pearl }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.CONTROLLER.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), Items.diamond));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.DIAL.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.NETWORK.ordinal()), Items.diamond));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.PORTAL_MANIPULATOR.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), Items.diamond, Items.emerald, new ItemStack(EPItems.portalModuleBlank)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_ENERGY.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), Items.ender_pearl, Items.diamond, Blocks.redstone_block));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_FLUID.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), Items.ender_pearl, Items.diamond, Items.bucket));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_ITEM.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), Items.ender_pearl, Items.diamond, Blocks.chest));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 2), new ItemStack(EPItems.upgrade, 1, 1), Items.diamond));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 4), new ItemStack(EPItems.upgradeBlank), Items.diamond, Items.emerald, new ItemStack(EPItems.portalModuleBlank)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 7), new ItemStack(EPItems.upgradeBlank), Items.ender_pearl, Items.diamond, Blocks.redstone_block));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 5), new ItemStack(EPItems.upgradeBlank), Items.ender_pearl, Items.diamond, Items.bucket));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 6), new ItemStack(EPItems.upgradeBlank), Items.ender_pearl, Items.diamond, Blocks.chest));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.decorBorderedQuartz, 9), new Object[] { "SQS", "QQQ", "SQS", 'S', Blocks.stone, 'Q', Blocks.quartz_block }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.decorEnderInfusedMetal, 9), Blocks.iron_block, Items.iron_ingot, Items.iron_ingot, Items.ender_pearl, Items.ender_pearl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.portalModuleBlank), true, new Object[] { "NNN", "NIN", "NNN", 'I', Items.iron_ingot, 'N', Items.gold_nugget }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.upgradeBlank, 8, 0), new Object[] { "D", "P", "R", 'P', Items.paper, 'D', Items.diamond, 'R', "dyeRed" }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.portalModule, 1, 0), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.redstone), new ItemStack(Items.gunpowder)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.portalModule, 1, 1), new ItemStack(EPItems.portalModuleBlank), "dyeRed", "dyeBlue", "dyeGreen"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.portalModule, 1, 2), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.redstone), new ItemStack(Blocks.noteblock)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.portalModule, 1, 3), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Blocks.anvil), new ItemStack(Items.feather)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.portalModule, 1, 5), new ItemStack(EPItems.portalModuleBlank), "dyeWhite", "dyeBlack"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.portalModule, 1, 6), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.compass)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.portalModule, 1, 7), new Object[] { "FFF", "FXF", "FFF", 'X', new ItemStack(EPItems.portalModuleBlank), 'F', new ItemStack(Items.feather) }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.manual), new ItemStack(Items.book), new ItemStack(EPItems.locationCard)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.nanobrush), new Object[] { "WT ", "TS ", "  S", 'W', Blocks.wool, 'T', Items.string, 'S', "stickWood" }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.nanobrush), new Object[] { " TW", " ST", "S  ", 'W', Blocks.wool, 'T', Items.string, 'S', "stickWood" }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.locationCard, 16), new Object[] { "IPI", "PPP", "IDI", 'I', Items.iron_ingot, 'P', Items.paper, 'D', "dyeBlue" }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.wrench), new Object[] { "I I", " Q ", " I ", 'I', Items.iron_ingot, 'Q', Items.quartz }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.glasses), true, new Object[] { "R B", "GLG", "L L", 'R', "dyeRed", 'B', "dyeCyan", 'G', Blocks.glass_pane, 'L', Items.leather }));
        }

        if (EPConfiguration.recipeTE && Loader.isModLoaded(ThermalExpansion.MOD_ID)) {
            String diamondNugget = "nuggetDiamond";

            ItemStack machineFrameBasic = ThermalExpansion.getItemStack("Frame"), machineFrameHardened = ThermalExpansion.getItemStack("Frame", 1), powerCoilGold = ThermalExpansion.getItemStack("powerCoilGold");

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.frame, 4, 0), "SQS", "QFQ", "SQS", 'S', Blocks.stone, 'Q', Items.quartz, 'F', machineFrameBasic));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.dimensionalBridgeStabilizerEmpty, 3, 0), "INI", "NFN", "ICI", 'F', machineFrameHardened, 'C', powerCoilGold, 'I', Items.iron_ingot, 'N', diamondNugget));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_ENERGY.ordinal()), EPBlocks.frame, Items.ender_pearl, Items.diamond, powerCoilGold));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(EPItems.upgrade, 1, 7), EPItems.upgradeBlank, Items.ender_pearl, Items.diamond, powerCoilGold));
        }
    }

    @Override
    protected void registerConfiguration() {
        EPConfiguration.forceFrameOverlay = config.get("General", "ForceShowFrameOverlays", EPConfiguration.forceFrameOverlay, "Forces the frame overlays to be shown instead of having to wear the Glasses").getBoolean();
        EPConfiguration.disableSounds = config.get("General", "DisableSounds", EPConfiguration.disableSounds, "Disables all portal sounds").getBoolean();
        EPConfiguration.disableParticles = config.get("General", "DisableParticles", EPConfiguration.disableParticles, "Disables all portal particles").getBoolean();
        EPConfiguration.portalDestroysBlocks = config.get("Portal", "PortalsDestroyBlocks", EPConfiguration.portalDestroysBlocks, "Destroy any blocks placed in the way of an initialized portal to allow a connection?").getBoolean();
        EPConfiguration.requirePower = config.get("Power", "RequirePower", EPConfiguration.requirePower, "Should the portals require power to operate?").getBoolean();
        EPConfiguration.powerUseMultiplier = config.get("Power", "PowerMultiplier", EPConfiguration.powerUseMultiplier, "Multiplier for how much power the portals use. 0.5 is half, 2.0 is double etc.").getDouble();
        EPConfiguration.powerStorageMultiplier = config.get("Power", "DBSPowerStorageMultiplier", EPConfiguration.powerStorageMultiplier, "Multiplier for how much power the Dimensional Bridge Stabilizer can store. 0.5 is half, 2.0 is double etc.").getDouble();
        EPConfiguration.connectionsPerRow = config.get("Portal", "ActivePortalsPerRow", EPConfiguration.connectionsPerRow, "The amount of simultanous active portals the DBS can hold (per row)").getInt();
        EnhancedPortals.instance.CHECK_FOR_UPDATES = config.get("General", "UpdateCheck", EnhancedPortals.instance.CHECK_FOR_UPDATES, "Allow checking for updates from " + EPMod.url).getBoolean();
        EPConfiguration.recipeVanilla = config.get("General", "VanillaRecipes", EPConfiguration.recipeVanilla, "Should the recipes using the vanilla materials be enabled?").getBoolean();
        EPConfiguration.recipeTE = config.get("General", "ThermalExpansionRecipes", EPConfiguration.recipeTE, "Should the recipes using the Thermal Expansion materials be enabled?").getBoolean();
        
        if (EPConfiguration.powerUseMultiplier < 0)
            EPConfiguration.requirePower = false;

        if (EPConfiguration.powerStorageMultiplier < 0.01)
            EPConfiguration.powerStorageMultiplier = 0.01;
    }
}
