package enhanced.portals.network;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
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
import enhanced.base.xmod.ThermalExpansion;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.block.BlockDecorBorderedQuartz;
import enhanced.portals.block.BlockDecorEnderInfusedMetal;
import enhanced.portals.block.BlockFrame;
import enhanced.portals.block.BlockPortal;
import enhanced.portals.block.BlockStabilizer;
import enhanced.portals.block.BlockStabilizerEmpty;
import enhanced.portals.item.ItemBlankPortalModule;
import enhanced.portals.item.ItemBlankUpgrade;
import enhanced.portals.item.ItemDiamondNugget;
import enhanced.portals.item.ItemFrame;
import enhanced.portals.item.ItemGlasses;
import enhanced.portals.item.ItemLocationCard;
import enhanced.portals.item.ItemManual;
import enhanced.portals.item.ItemNanobrush;
import enhanced.portals.item.ItemPortalModule;
import enhanced.portals.item.ItemStabilizer;
import enhanced.portals.item.ItemUpgrade;
import enhanced.portals.item.ItemWrench;
import enhanced.portals.item.PotionFeatherfall;
import enhanced.portals.network.packet.PacketGui;
import enhanced.portals.network.packet.PacketGuiData;
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
import enhanced.portals.tile.TileStabilizer;
import enhanced.portals.tile.TileStabilizerMain;
import enhanced.portals.tile.TileTransferEnergy;
import enhanced.portals.tile.TileTransferFluid;
import enhanced.portals.tile.TileTransferItem;

public class ProxyCommon extends BaseProxy {
    public static int CONFIG_REDSTONE_FLUX_COST = 10000, CONFIG_REDSTONE_FLUX_TIMER = 20;
    public static boolean CONFIG_FORCE_FRAME_OVERLAY, CONFIG_DISABLE_SOUNDS, CONFIG_DISABLE_PARTICLES, CONFIG_PORTAL_DESTROYS_BLOCKS, CONFIG_FASTER_PORTAL_COOLDOWN, CONFIG_REQUIRE_POWER, CONFIG_UPDATE_NOTIFIER, CONFIG_RECIPES_VANILLA, CONFIG_RECIPES_TE;
    public static double CONFIG_POWER_MULTIPLIER, CONFIG_POWER_STORAGE_MULTIPLIER;
    public static int CONFIG_PORTAL_CONNECTIONS_PER_ROW = 2;

    public int gogglesRenderIndex = 0;
    public NetworkManager networkManager;
    public static String UPDATE_LATEST_VER;

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

    @Override
    public void registerBlocks() {
        GameRegistry.registerBlock(new BlockFrame("frame"), ItemFrame.class, "frame");
        GameRegistry.registerBlock(new BlockPortal("portal"), "portal");
        GameRegistry.registerBlock(new BlockStabilizer("dbs"), ItemStabilizer.class, "dbs");
        GameRegistry.registerBlock(new BlockDecorBorderedQuartz("decor_frame"), "decor_frame");
        GameRegistry.registerBlock(new BlockDecorEnderInfusedMetal("decor_dbs"), "decor_dbs");
        GameRegistry.registerBlock(new BlockStabilizerEmpty("dbs_empty"), "dbs_empty");
    }

    @Override
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

        if (CONFIG_RECIPES_TE && Loader.isModLoaded(ThermalExpansion.MOD_ID)) {
            GameRegistry.registerItem(new ItemDiamondNugget("diamondNugget"), "nuggetDiamond");
            OreDictionary.registerOre("nuggetDiamond", ItemDiamondNugget.instance);
            GameRegistry.addShapelessRecipe(new ItemStack(ItemDiamondNugget.instance, 9), Items.diamond);
            GameRegistry.addShapelessRecipe(new ItemStack(Items.diamond, 1), ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance, ItemDiamondNugget.instance);

            ThermalExpansion.addTransposerFill(10000, new ItemStack(BlockFrame.instance, 1, 0), new ItemStack(BlockFrame.instance, 1, BlockFrame.REDSTONE_INTERFACE), new FluidStack(FluidRegistry.getFluidID("redstone"), 400), false);
            ThermalExpansion.addTransposerFill(10000, new ItemStack(ItemBlankUpgrade.instance, 1, 0), new ItemStack(ItemUpgrade.instance, 1, 0), new FluidStack(FluidRegistry.getFluidID("redstone"), 400), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(BlockFrame.instance, 1, 0), new ItemStack(BlockFrame.instance, 1, BlockFrame.NETWORK_INTERFACE), new FluidStack(FluidRegistry.getFluidID("ender"), 250), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(ItemBlankUpgrade.instance, 1, 1), new ItemStack(ItemUpgrade.instance, 1, 1), new FluidStack(FluidRegistry.getFluidID("ender"), 250), false);
            ThermalExpansion.addTransposerFill(15000, new ItemStack(BlockStabilizerEmpty.instance, 1, 0), new ItemStack(BlockStabilizer.instance, 1, 0), new FluidStack(FluidRegistry.getFluidID("ender"), 125), false);
        }
    }

    @Override
    public void registerPotions() {
        Potion[] potionTypes = null;

        for (Field f : Potion.class.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                if (f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
                    Field modfield = Field.class.getDeclaredField("modifiers");
                    modfield.setAccessible(true);
                    modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);

                    potionTypes = (Potion[]) f.get(null);
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

    @Override
    public void registerPackets() {
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketRequestGui.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketTextureData.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketRerender.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketGuiData.class);
        EnhancedPortals.instance.packetPipeline.registerPacket(PacketGui.class);
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
        GameRegistry.registerTileEntity(TileStabilizer.class, "epDBS");
        GameRegistry.registerTileEntity(TileStabilizerMain.class, "epDBSM");
        GameRegistry.registerTileEntity(TileTransferEnergy.class, "epTE");
        GameRegistry.registerTileEntity(TileTransferFluid.class, "epTF");
        GameRegistry.registerTileEntity(TileTransferItem.class, "epTI");
    }

    @Override
    public void init() {

    }

    @Override
    public void postInit() {
        ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(ItemPortalModule.instance, 1, 4), 1, 1, 2));
    }

    @Override
    public void registerRecipes() {
        if (CONFIG_RECIPES_VANILLA) {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockFrame.instance, 4, 0), new Object[] { "SIS", "IQI", "SIS", 'S', Blocks.stone, 'Q', Blocks.quartz_block, 'I', Items.iron_ingot }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.REDSTONE_INTERFACE), new Object[] { " R ", "RFR", " R ", 'F', new ItemStack(BlockFrame.instance, 1, 0), 'R', Items.redstone }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.NETWORK_INTERFACE), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 0), new Object[] { " R ", "RFR", " R ", 'F', new ItemStack(ItemBlankUpgrade.instance), 'R', Items.redstone }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 1), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockStabilizer.instance, 6), new Object[] { "QPQ", "PDP", "QPQ", 'D', Items.diamond, 'Q', Blocks.iron_block, 'P', Items.ender_pearl }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.PORTAL_CONTROLLER), new ItemStack(BlockFrame.instance, 1, 0), Items.diamond));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.DIALLING_DEVICE), new ItemStack(BlockFrame.instance, 1, BlockFrame.NETWORK_INTERFACE), Items.diamond));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.MODULE_MANIPULATOR), new ItemStack(BlockFrame.instance, 1, 0), Items.diamond, Items.emerald, new ItemStack(ItemBlankPortalModule.instance)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_ENERGY), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl, Items.diamond, Blocks.redstone_block));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_FLUID), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl, Items.diamond, Items.bucket));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_ITEM), new ItemStack(BlockFrame.instance, 1, 0), Items.ender_pearl, Items.diamond, Blocks.chest));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 2), new ItemStack(ItemUpgrade.instance, 1, 1), Items.diamond));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 4), new ItemStack(ItemBlankUpgrade.instance), Items.diamond, Items.emerald, new ItemStack(ItemBlankPortalModule.instance)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 7), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl, Items.diamond, Blocks.redstone_block));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 5), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl, Items.diamond, Items.bucket));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 6), new ItemStack(ItemBlankUpgrade.instance), Items.ender_pearl, Items.diamond, Blocks.chest));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockDecorBorderedQuartz.instance, 9), new Object[] { "SQS", "QQQ", "SQS", 'S', Blocks.stone, 'Q', Blocks.quartz_block }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockDecorEnderInfusedMetal.instance, 9), Blocks.iron_block, Items.iron_ingot, Items.iron_ingot, Items.ender_pearl, Items.ender_pearl));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemBlankPortalModule.instance), true, new Object[] { "NNN", "NIN", "NNN", 'I', Items.iron_ingot, 'N', Items.gold_nugget }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemBlankUpgrade.instance, 8, 0), new Object[] { "D", "P", "R", 'P', Items.paper, 'D', Items.diamond, 'R', "dyeRed" }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 0), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Items.redstone), new ItemStack(Items.gunpowder)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 1), new ItemStack(ItemBlankPortalModule.instance), "dyeRed", "dyeBlue", "dyeGreen"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 2), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Items.redstone), new ItemStack(Blocks.noteblock)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 3), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Blocks.anvil), new ItemStack(Items.feather)));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 5), new ItemStack(ItemBlankPortalModule.instance), "dyeWhite", "dyeBlack"));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 6), new ItemStack(ItemBlankPortalModule.instance), new ItemStack(Items.compass)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemPortalModule.instance, 1, 7), new Object[] { "FFF", "FXF", "FFF", 'X', new ItemStack(ItemBlankPortalModule.instance), 'F', new ItemStack(Items.feather) }));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemManual.instance), new ItemStack(Items.book), new ItemStack(ItemLocationCard.instance)));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemNanobrush.instance), new Object[] { "WT ", "TS ", "  S", 'W', Blocks.wool, 'T', Items.string, 'S', "stickWood" }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemNanobrush.instance), new Object[] { " TW", " ST", "S  ", 'W', Blocks.wool, 'T', Items.string, 'S', "stickWood" }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemLocationCard.instance, 16), new Object[] { "IPI", "PPP", "IDI", 'I', Items.iron_ingot, 'P', Items.paper, 'D', "dyeBlue" }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemWrench.instance), new Object[] { "I I", " Q ", " I ", 'I', Items.iron_ingot, 'Q', Items.quartz }));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ItemGlasses.instance), true, new Object[] { "R B", "GLG", "L L", 'R', "dyeRed", 'B', "dyeCyan", 'G', Blocks.glass_pane, 'L', Items.leather }));
        }

        if (CONFIG_RECIPES_TE && Loader.isModLoaded(ThermalExpansion.MOD_ID)) {
            String diamondNugget = "nuggetDiamond";

            ItemStack machineFrameBasic = ThermalExpansion.getItemStack("Frame"), machineFrameHardened = ThermalExpansion.getItemStack("Frame", 1), powerCoilGold = ThermalExpansion.getItemStack("powerCoilGold");

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockFrame.instance, 4, 0), "SQS", "QFQ", "SQS", 'S', Blocks.stone, 'Q', Items.quartz, 'F', machineFrameBasic));
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockStabilizerEmpty.instance, 3, 0), "INI", "NFN", "ICI", 'F', machineFrameHardened, 'C', powerCoilGold, 'I', Items.iron_ingot, 'N', diamondNugget));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BlockFrame.instance, 1, BlockFrame.TRANSFER_ENERGY), BlockFrame.instance, Items.ender_pearl, Items.diamond, powerCoilGold));
            GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ItemUpgrade.instance, 1, 7), ItemBlankUpgrade.instance, Items.ender_pearl, Items.diamond, powerCoilGold));
        }
    }

    @Override
    protected void registerConfiguration() {
        CONFIG_FORCE_FRAME_OVERLAY = config.get("General", "ForceShowFrameOverlays", false, "Forces the frame overlays to be shown instead of having to wear the Glasses").getBoolean();
        CONFIG_DISABLE_SOUNDS = config.get("General", "DisableSounds", false, "Disables all portal sounds").getBoolean();
        CONFIG_DISABLE_PARTICLES = config.get("General", "DisableParticles", false, "Disables all portal particles").getBoolean();
        CONFIG_PORTAL_DESTROYS_BLOCKS = config.get("Portal", "PortalsDestroyBlocks", true, "").getBoolean();
        CONFIG_FASTER_PORTAL_COOLDOWN = config.get("Portal", "FasterPortalCooldown", false, "Should every entity get the faster portal cooldown? (Reduced to 10 ticks from 300) Note this can cause entities to walk in and out of portals very quickly.").getBoolean();
        CONFIG_REQUIRE_POWER = config.get("Power", "RequirePower", true, "Should the portals require power to operate?").getBoolean();
        CONFIG_POWER_MULTIPLIER = config.get("Power", "PowerMultiplier", 1.0, "Multiplier for how much power the portals use. 0.5 is half, 2.0 is double etc.").getDouble();
        CONFIG_POWER_STORAGE_MULTIPLIER = config.get("Power", "DBSPowerStorageMultiplier", 1.0, "Multiplier for how much power the Dimensional Bridge Stabilizer can store. 0.5 is half, 2.0 is double etc.").getDouble();
        CONFIG_PORTAL_CONNECTIONS_PER_ROW = config.get("Portal", "ActivePortalsPerRow", 2, "The amount of simultanous active portals the DBS can hold (per row)").getInt();
        EnhancedPortals.instance.CHECK_FOR_UPDATES = config.get("General", "UpdateCheck", true, "Allow checking for updates from " + EnhancedPortals.MOD_URL).getBoolean();
        CONFIG_RECIPES_VANILLA = config.get("General", "VanillaRecipes", true, "Should the recipes using the vanilla materials be enabled?").getBoolean();
        CONFIG_RECIPES_TE = config.get("General", "ThermalExpansionRecipes", true, "Should the recipes using the Thermal Expansion materials be enabled?").getBoolean();

        if (CONFIG_POWER_MULTIPLIER < 0)
            CONFIG_REQUIRE_POWER = false;

        if (CONFIG_POWER_STORAGE_MULTIPLIER < 0.01)
            CONFIG_POWER_STORAGE_MULTIPLIER = 0.01;
    }
}
