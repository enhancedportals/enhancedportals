package enhanced.portals.network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import enhanced.base.utilities.Localisation;
import enhanced.portals.client.PortalRenderer;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalTextureManager;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPItems;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.EPRenderers;
import enhanced.portals.utility.Reference.PortalFrames;

public class ProxyClient extends ProxyCommon {
    public class ParticleSet {
        public int[] frames;
        public int type;

        public ParticleSet(int t, int[] s) {
            frames = s;
            type = t;
        }
    }

    public static GlyphIdentifier saveGlyph;
    public static PortalTextureManager saveTexture;
    public static String saveName;
    public static int editingID = -1;

    public static String manualEntry = "subject";
    public static int manualPage = 0;
    public static int chapterNum = 0;
    public static int chapterPage = 0;
    public static int chapterPageTotal = 0;

    public static int editingDialEntry = -1;
    public static PortalTextureManager dialEntryTexture = new PortalTextureManager();

    public static ArrayList<IIcon> customFrameTextures = new ArrayList<IIcon>();
    public static ArrayList<IIcon> customPortalTextures = new ArrayList<IIcon>();
    public static ArrayList<ParticleSet> particleSets = new ArrayList<ParticleSet>();

    static HashMap<String, ItemStack[]> craftingRecipes = new HashMap<String, ItemStack[]>();
    public static HashMap<ChunkCoordinates, ArrayList<ChunkCoordinates>> waitingForController = new HashMap<ChunkCoordinates, ArrayList<ChunkCoordinates>>();

    @Override
    public void waitForController(ChunkCoordinates controller, ChunkCoordinates frame) {
        if (waitingForController.containsKey(controller))
            waitingForController.get(controller).add(frame);
        else {
            waitingForController.put(controller, new ArrayList<ChunkCoordinates>());
            waitingForController.get(controller).add(frame);
        }
    }

    @Override
    public ArrayList<ChunkCoordinates> getControllerList(ChunkCoordinates controller) {
        return waitingForController.get(controller);
    }

    @Override
    public void clearControllerList(ChunkCoordinates controller) {
        waitingForController.remove(controller);
    }

    public static ItemStack[] getCraftingRecipeForManualEntry() {
        return craftingRecipes.get(manualEntry);
    }

    public static boolean manualChapterExists(int chapter_num) {
        return Localisation.exists(EPMod.ID, "manual.chapter." + chapter_num + ".title");
    }

    public static boolean manualChapterPageExists(int chapter_num, int chapter_page) {
        return Localisation.exists(EPMod.ID, "manual.chapter." + chapter_num + ".page." + chapter_page);
    }

    public static int manualChapterLastPage(int chapter_num) {
        int max_pages = 10;
        for (int i = max_pages; i > 0; i--)
            if (Localisation.exists(EPMod.ID, "manual.chapter." + chapter_num + ".page." + i))
                return i;
        return -1;
    }

    public static void manualChangeEntry(String entry) {
        manualEntry = entry;
        manualPage = 0;
    }

    public static boolean manualEntryHasPage(int page) {
        return !Localisation.get(EPMod.ID, "manual." + manualEntry + ".page." + page).contains(".page.");
    }

    public static boolean resourceExists(String file) {
        IReloadableResourceManager resourceManager = (IReloadableResourceManager) FMLClientHandler.instance().getClient().getResourceManager();

        try {
            resourceManager.getResource(new ResourceLocation(EPMod.ID, file));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean setManualPageFromBlock(Block b, int meta) {
        if (b == EPBlocks.frame) {
            manualChangeEntry("frame" + meta);
            return true;
        } else if (b == EPBlocks.portal) {
            manualChangeEntry("portal");
            return true;
        } else if (b == EPBlocks.decorEnderInfusedMetal) {
            manualChangeEntry("decorStabilizer");
            return true;
        } else if (b == EPBlocks.dimensionalBridgeStabilizer) {
            manualChangeEntry("dbs");
            return true;
        } else if (b == EPBlocks.dimensionalBridgeStabilizerEmpty) {
            manualChangeEntry("dbsEmpty");
            return true;
        } else if (b == EPBlocks.decorBorderedQuartz) {
            manualChangeEntry("decorBorderedQuartz");
            return true;
        }

        return false;
    }

    public static boolean setManualPageFromItem(ItemStack s) {
        Item i = s.getItem();

        if (i instanceof ItemBlock)
            return setManualPageFromBlock(Block.getBlockFromItem(i), s.getItemDamage());
        else if (i == EPItems.portalModuleBlank) {
            manualChangeEntry("blank_module");
            return true;
        } else if (i == EPItems.upgradeBlank) {
            manualChangeEntry("blank_upgrade");
            return true;
        } else if (i == EPItems.glasses) {
            manualChangeEntry("glasses");
            return true;
        } else if (i == EPItems.locationCard) {
            manualChangeEntry("location_card");
            return true;
        } else if (i == EPItems.nanobrush) {
            manualChangeEntry("nanobrush");
            return true;
        } else if (i == EPItems.wrench) {
            manualChangeEntry("wrench");
            return true;
        } else if (i == EPItems.portalModule) {
            manualChangeEntry("module" + s.getItemDamage());
            return true;
        } else if (i == EPItems.upgrade) {
            manualChangeEntry("upgrade" + s.getItemDamage());
            return true;
        }

        return false;
    }

    @Override
    public File getWorldDir() {
        return new File(getBaseDir(), "saves/" + DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
    }

    @Override
    public void registerRecipes() {
        super.registerRecipes();

        craftingRecipes.put("frame0", new ItemStack[] { new ItemStack(Blocks.stone), new ItemStack(Items.iron_ingot), new ItemStack(Blocks.stone), new ItemStack(Items.iron_ingot), new ItemStack(Blocks.quartz_block), new ItemStack(Items.iron_ingot), new ItemStack(Blocks.stone), new ItemStack(Items.iron_ingot), new ItemStack(Blocks.stone), new ItemStack(EPBlocks.frame, 4, 0) });
        craftingRecipes.put("frame" + PortalFrames.CONTROLLER.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame), new ItemStack(Items.diamond), null, null, null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.CONTROLLER.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.REDSTONE.ordinal(), new ItemStack[] { null, new ItemStack(Items.redstone), null, new ItemStack(Items.redstone), new ItemStack(EPBlocks.frame, 1, 0), new ItemStack(Items.redstone), null, new ItemStack(Items.redstone), null, new ItemStack(EPBlocks.frame, 1, PortalFrames.REDSTONE.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.NETWORK.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame, 1, 0), new ItemStack(Items.ender_pearl), null, null, null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.NETWORK.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.DIAL.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame, 1, PortalFrames.NETWORK.ordinal()), new ItemStack(Items.diamond), null, null, null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.DIAL.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.PORTAL_MANIPULATOR.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame, 1, 0), new ItemStack(Items.diamond), new ItemStack(Items.emerald), new ItemStack(EPItems.portalModuleBlank), null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.PORTAL_MANIPULATOR.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.TRANSFER_ENERGY.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame, 1, 0), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Blocks.redstone_block), null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_ENERGY.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.TRANSFER_FLUID.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame, 1, 0), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Items.bucket), null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_FLUID.ordinal()) });
        craftingRecipes.put("frame" + PortalFrames.TRANSFER_ITEM.ordinal(), new ItemStack[] { new ItemStack(EPBlocks.frame, 1, 0), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Blocks.chest), null, null, null, null, null, new ItemStack(EPBlocks.frame, 1, PortalFrames.TRANSFER_ITEM.ordinal()) });
        craftingRecipes.put("decorStabilizer", new ItemStack[] { new ItemStack(Blocks.iron_block), new ItemStack(Items.iron_ingot), new ItemStack(Items.iron_ingot), new ItemStack(Items.ender_pearl), new ItemStack(Items.ender_pearl), null, null, null, null, new ItemStack(EPBlocks.decorEnderInfusedMetal, 9) });
        craftingRecipes.put("dbs", new ItemStack[] { new ItemStack(Blocks.iron_block), new ItemStack(Items.ender_pearl), new ItemStack(Blocks.iron_block), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Items.ender_pearl), new ItemStack(Blocks.iron_block), new ItemStack(Items.ender_pearl), new ItemStack(Blocks.iron_block), new ItemStack(EPBlocks.dimensionalBridgeStabilizer, 6) });
        craftingRecipes.put("dbsEmpty", new ItemStack[] {});
        craftingRecipes.put("decorBorderedQuartz", new ItemStack[] { new ItemStack(Blocks.stone), new ItemStack(Blocks.quartz_block), new ItemStack(Blocks.stone), new ItemStack(Blocks.quartz_block), new ItemStack(Blocks.quartz_block), new ItemStack(Blocks.quartz_block), new ItemStack(Blocks.stone), new ItemStack(Blocks.quartz_block), new ItemStack(Blocks.stone), new ItemStack(EPBlocks.decorBorderedQuartz, 9) });
        craftingRecipes.put("blank_module", new ItemStack[] { new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_nugget), new ItemStack(Items.iron_ingot), new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_nugget), new ItemStack(EPItems.portalModuleBlank) });
        craftingRecipes.put("blank_upgrade", new ItemStack[] { new ItemStack(Items.diamond), null, null, new ItemStack(Items.paper), null, null, new ItemStack(Items.dye, 1, 1), null, null, new ItemStack(EPItems.upgradeBlank) });
        craftingRecipes.put("glasses", new ItemStack[] { new ItemStack(Items.dye, 1, 1), null, new ItemStack(Items.dye, 1, 6), new ItemStack(Blocks.glass_pane), new ItemStack(Items.leather), new ItemStack(Blocks.glass_pane), new ItemStack(Items.leather), null, new ItemStack(Items.leather), new ItemStack(EPItems.glasses) });
        craftingRecipes.put("location_card", new ItemStack[] { new ItemStack(Items.iron_ingot), new ItemStack(Items.paper), new ItemStack(Items.iron_ingot), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.iron_ingot), new ItemStack(Items.dye, 1, 4), new ItemStack(Items.iron_ingot), new ItemStack(EPItems.locationCard, 16) });
        craftingRecipes.put("wrench", new ItemStack[] { new ItemStack(Items.iron_ingot), null, new ItemStack(Items.iron_ingot), null, new ItemStack(Items.quartz), null, null, new ItemStack(Items.iron_ingot), null, new ItemStack(EPItems.wrench) });
        craftingRecipes.put("nanobrush", new ItemStack[] { new ItemStack(Blocks.wool), new ItemStack(Items.string), null, new ItemStack(Items.string), new ItemStack(Items.stick), null, null, null, new ItemStack(Items.stick), new ItemStack(EPItems.nanobrush) });
        craftingRecipes.put("module0", new ItemStack[] { new ItemStack(Items.redstone), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.gunpowder), null, null, null, null, null, null, new ItemStack(EPItems.portalModule, 1, 0) });
        craftingRecipes.put("module1", new ItemStack[] { new ItemStack(Items.dye, 1, 1), new ItemStack(Items.dye, 1, 2), new ItemStack(Items.dye, 1, 4), null, new ItemStack(EPItems.portalModuleBlank), null, null, null, null, new ItemStack(EPItems.portalModule, 1, 1) });
        craftingRecipes.put("module2", new ItemStack[] { new ItemStack(Items.redstone), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Blocks.noteblock), null, null, null, null, null, null, new ItemStack(EPItems.portalModule, 1, 2) });
        craftingRecipes.put("module3", new ItemStack[] { new ItemStack(Blocks.anvil), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.feather), null, null, null, null, null, null, new ItemStack(EPItems.portalModule, 1, 3) });
        // craftingRecipes.put("module4", new ItemStack[] { });
        craftingRecipes.put("module5", new ItemStack[] { new ItemStack(Items.dye, 1, 15), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.dye, 1, 0), null, null, null, null, null, null, new ItemStack(EPItems.portalModule, 1, 5) });
        craftingRecipes.put("module6", new ItemStack[] { new ItemStack(Items.compass), new ItemStack(EPItems.portalModuleBlank), null, null, null, null, null, null, null, new ItemStack(EPItems.portalModule, 1, 6) });
        craftingRecipes.put("module7", new ItemStack[] { new ItemStack(Items.feather), new ItemStack(Items.feather), new ItemStack(Items.feather), new ItemStack(Items.feather), new ItemStack(EPItems.portalModuleBlank), new ItemStack(Items.feather), new ItemStack(Items.feather), new ItemStack(Items.feather), new ItemStack(Items.feather), new ItemStack(EPItems.portalModule, 1, 7) });
        craftingRecipes.put("upgrade0", new ItemStack[] { null, new ItemStack(Items.redstone), null, new ItemStack(Items.redstone), new ItemStack(EPItems.upgradeBlank), new ItemStack(Items.redstone), null, new ItemStack(Items.redstone), null, new ItemStack(EPItems.upgrade, 1, 0) });
        craftingRecipes.put("upgrade1", new ItemStack[] { new ItemStack(EPItems.upgradeBlank), new ItemStack(Items.ender_pearl), null, null, null, null, null, null, null, new ItemStack(EPItems.upgrade, 1, 1) });
        craftingRecipes.put("upgrade2", new ItemStack[] { new ItemStack(EPItems.upgrade, 1, 1), new ItemStack(Items.diamond), null, null, null, null, null, null, null, new ItemStack(EPItems.upgrade, 1, 2) });
        craftingRecipes.put("upgrade3", new ItemStack[] {});
        craftingRecipes.put("upgrade4", new ItemStack[] { new ItemStack(EPItems.upgradeBlank), new ItemStack(Items.diamond), new ItemStack(Items.emerald), new ItemStack(EPItems.portalModuleBlank), null, null, null, null, null, new ItemStack(EPItems.upgrade, 1, 4) });
        craftingRecipes.put("upgrade5", new ItemStack[] { new ItemStack(EPItems.upgradeBlank), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Items.bucket), null, null, null, null, null, new ItemStack(EPItems.upgrade, 1, 5) });
        craftingRecipes.put("upgrade6", new ItemStack[] { new ItemStack(EPItems.upgradeBlank), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Blocks.chest), null, null, null, null, null, new ItemStack(EPItems.upgrade, 1, 6) });
        craftingRecipes.put("upgrade7", new ItemStack[] { new ItemStack(EPItems.upgradeBlank), new ItemStack(Items.ender_pearl), new ItemStack(Items.diamond), new ItemStack(Blocks.redstone_block), null, null, null, null, null, new ItemStack(EPItems.upgrade, 1, 7) });
    }

    @Override
    public void postInit() {
        super.postInit();

        // Randomly chooses a particle then spawns it, stays static
        particleSets.add(new ParticleSet(0, new int[] { 0, 1, 2, 3, 4, 5, 6, 7 }));
        particleSets.add(new ParticleSet(0, new int[] { 16, 17 }));
        particleSets.add(new ParticleSet(0, new int[] { 19, 20, 21, 22 }));
        particleSets.add(new ParticleSet(0, new int[] { 48, 49 }));
        particleSets.add(new ParticleSet(0, new int[] { 96, 97 }));
        particleSets.add(new ParticleSet(0, new int[] { 112, 113, 114 }));
        particleSets.add(new ParticleSet(0, new int[] { 128, 129, 130, 131, 132, 133, 134, 135 }));
        particleSets.add(new ParticleSet(0, new int[] { 144, 145, 146, 147, 148, 149, 150, 151 }));
        particleSets.add(new ParticleSet(0, new int[] { 160, 161, 162, 163, 164, 165, 166, 167 }));
        particleSets.add(new ParticleSet(0, new int[] { 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250 }));

        // Will play through their animation once, then die
        particleSets.add(new ParticleSet(1, new int[] { 7, 6, 5, 4, 3, 2, 1 }));
        particleSets.add(new ParticleSet(1, new int[] { 135, 134, 133, 132, 131, 130, 129, 128 }));
        particleSets.add(new ParticleSet(1, new int[] { 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250 }));

        // Static
        particleSets.add(new ParticleSet(2, new int[] { 32 }));
        particleSets.add(new ParticleSet(2, new int[] { 33 }));
        particleSets.add(new ParticleSet(2, new int[] { 64 }));
        particleSets.add(new ParticleSet(2, new int[] { 65 }));
        particleSets.add(new ParticleSet(2, new int[] { 66 }));
        particleSets.add(new ParticleSet(2, new int[] { 80 }));
        particleSets.add(new ParticleSet(2, new int[] { 81 }));
        particleSets.add(new ParticleSet(2, new int[] { 82 }));
        particleSets.add(new ParticleSet(2, new int[] { 83 }));

        // Rendering
        RenderingRegistry.registerBlockHandler(EPRenderers.portal, new PortalRenderer());
    }
}
