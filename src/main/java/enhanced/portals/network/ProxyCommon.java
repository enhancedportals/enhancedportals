package enhanced.portals.network;

import java.io.File;
import java.util.ArrayList;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import enhanced.base.mod.BaseProxy;
import enhanced.base.network.packet.PacketGui;
import enhanced.base.network.packet.PacketGuiData;
import enhanced.base.utilities.BlockPos;
import enhanced.base.xmod.ThermalExpansion;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.Reference.EPConfiguration;
import enhanced.portals.Reference.EPItems;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.PortalFrames;
import enhanced.portals.item.ItemFrame;
import enhanced.portals.network.packet.PacketRequestGui;
import enhanced.portals.network.packet.PacketRerender;
import enhanced.portals.network.packet.PacketTextureData;
import enhanced.portals.portal.NetworkManager;
import enhanced.portals.portal.controller.TileController;
import enhanced.portals.portal.dial.TileDialingDevice;
import enhanced.portals.portal.frame.TileFrame;
import enhanced.portals.portal.manipulator.TilePortalManipulator;
import enhanced.portals.portal.network.TileNetworkInterface;
import enhanced.portals.portal.portal.TilePortal;
import enhanced.portals.portal.redstone.TileRedstoneInterface;
import enhanced.portals.portal.transfer.TileTransferEnergy;
import enhanced.portals.portal.transfer.TileTransferFluid;
import enhanced.portals.portal.transfer.TileTransferItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ProxyCommon extends BaseProxy {
    public NetworkManager networkManager;

    public void waitForController(BlockPos controller, BlockPos frame) {

    }

    public ArrayList<BlockPos> getControllerList(BlockPos controller) {
        return null;
    }

    public void clearControllerList(BlockPos controller) {

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
        GameRegistry.registerBlock(EPBlocks.decorBorderedQuartz, "decor_frame");
        GameRegistry.registerBlock(EPBlocks.decorEnderInfusedMetal, "decor_dbs");
    }

    @Override
    public void registerItems() {
        GameRegistry.registerItem(EPItems.glasses, "glasses");
        GameRegistry.registerItem(EPItems.portalModule, "portal_module");
        GameRegistry.registerItem(EPItems.upgrade, "upgrade");
        GameRegistry.registerItem(EPItems.portalModuleBlank, "blank_portal_module");
        GameRegistry.registerItem(EPItems.upgradeBlank, "blank_upgrade");
        GameRegistry.registerItem(EPItems.manual, "manual");

        if (EPConfiguration.recipeTE && Loader.isModLoaded(ThermalExpansion.MOD_ID)) {
            ThermalExpansion.addTransposerFill(10000, new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.REDSTONE.ordinal()), new FluidStack(FluidRegistry.getFluid("redstone"), 400), false);
            ThermalExpansion.addTransposerFill(10000, new ItemStack(EPItems.upgradeBlank, 1, 0), new ItemStack(EPItems.upgrade, 1, 0), new FluidStack(FluidRegistry.getFluid("redstone"), 400), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(EPBlocks.frame, 1, PortalFrames.BASIC.ordinal()), new ItemStack(EPBlocks.frame, 1, PortalFrames.NETWORK.ordinal()), new FluidStack(FluidRegistry.getFluid("ender"), 250), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(EPItems.upgradeBlank, 1, 1), new ItemStack(EPItems.upgrade, 1, 1), new FluidStack(FluidRegistry.getFluid("ender"), 250), false);
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
        GameRegistry.registerTileEntity(TileFrame.class, "epF");
        GameRegistry.registerTileEntity(TileController.class, "epPC");
        GameRegistry.registerTileEntity(TileRedstoneInterface.class, "epRI");
        GameRegistry.registerTileEntity(TileNetworkInterface.class, "epNI");
        GameRegistry.registerTileEntity(TileDialingDevice.class, "epDD");
        GameRegistry.registerTileEntity(TilePortalManipulator.class, "epMM");
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
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPItems.glasses), true, new Object[] { "R B", "GLG", "L L", 'R', "dyeRed", 'B', "dyeCyan", 'G', Blocks.glass_pane, 'L', Items.leather }));
        }

        if (EPConfiguration.recipeTE && Loader.isModLoaded(ThermalExpansion.MOD_ID)) {
            ItemStack machineFrameBasic = ThermalExpansion.getItemStack("Frame"), powerCoilGold = ThermalExpansion.getItemStack("powerCoilGold");

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(EPBlocks.frame, 4, 0), "SQS", "QFQ", "SQS", 'S', Blocks.stone, 'Q', Items.quartz, 'F', machineFrameBasic));
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
        EPConfiguration.initializationCost = config.get("Power", "InitializationCost", EPConfiguration.initializationCost, "The amount of power required to create a portal connection. (This is taken from both portals)").getInt();
        EPConfiguration.entityBaseCost = config.get("Power", "BaseEntityCost", EPConfiguration.entityBaseCost, "The base amount of power required to transport an entity. The actual amount required is calculated like this (base cost * (width * height)) (at 100% stability) Note that the actual cost is double this, as it is taken from both portals.").getInt();
        EPConfiguration.keepAliveCost = config.get("Portal", "KeepAliveCost", EPConfiguration.keepAliveCost, "The amount of power required to keep the portal connection alive. Taken each tick the portal connection is active.").getInt();
        EnhancedPortals.instance.CHECK_FOR_UPDATES = config.get("General", "UpdateCheck", EnhancedPortals.instance.CHECK_FOR_UPDATES, "Allow checking for updates from " + EPMod.url).getBoolean();
        EPConfiguration.recipeVanilla = config.get("General", "VanillaRecipes", EPConfiguration.recipeVanilla, "Should the recipes using the vanilla materials be enabled?").getBoolean();
        EPConfiguration.recipeTE = config.get("General", "ThermalExpansionRecipes", EPConfiguration.recipeTE, "Should the recipes using the Thermal Expansion materials be enabled?").getBoolean();
        
        if (!EPConfiguration.requirePower) {
            EPConfiguration.initializationCost = 0;
            EPConfiguration.entityBaseCost = 0;
            EPConfiguration.keepAliveCost = 0;
        }
    }
}
