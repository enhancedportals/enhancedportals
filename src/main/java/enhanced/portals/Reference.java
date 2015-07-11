package enhanced.portals;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import enhanced.portals.block.BlockDecorBorderedQuartz;
import enhanced.portals.block.BlockDecorEnderInfusedMetal;
import enhanced.portals.block.BlockFrame;
import enhanced.portals.block.BlockPortal;
import enhanced.portals.item.ItemBlankPortalModule;
import enhanced.portals.item.ItemBlankUpgrade;
import enhanced.portals.item.ItemGlasses;
import enhanced.portals.item.ItemManual;
import enhanced.portals.item.ItemPortalModule;
import enhanced.portals.item.ItemUpgrade;

public class Reference {
    public static class EPMod {
        public static final String name = "Enhanced Portals";
        public static final String ID = "enhancedportals";
        public static final String shortID = "ep3";
        public static final String version = "3.0.12";
        public static final String dependencies = "required-after:enhancedcore";
        public static final String url = "https://raw.githubusercontent.com/enhancedportals/VERSION/master/VERSION%20-%20Enhanced%20Portals";
        public static final String proxyClient = "enhanced.portals.network.ProxyClient";
        public static final String proxyCommon = "enhanced.portals.network.ProxyCommon";
    }

    public static class EPBlocks {
        public static final BlockFrame frame = new BlockFrame("frame");
        public static final BlockPortal portal = new BlockPortal("portal");

        public static final BlockDecorBorderedQuartz decorBorderedQuartz = new BlockDecorBorderedQuartz("decor_frame");
        public static final BlockDecorEnderInfusedMetal decorEnderInfusedMetal = new BlockDecorEnderInfusedMetal("decor_dbs");
    }

    public static class EPItems {
        public static final ItemGlasses glasses = new ItemGlasses("glasses");
        public static final ItemManual manual = new ItemManual("manual");
        public static final ItemPortalModule portalModule = new ItemPortalModule("portal_module");
        public static final ItemUpgrade upgrade = new ItemUpgrade("upgrade");
        public static final ItemBlankPortalModule portalModuleBlank = new ItemBlankPortalModule("blank_portal_module");
        public static final ItemBlankUpgrade upgradeBlank = new ItemBlankUpgrade("blank_upgrade");
    }

    public static class EPRenderers {
        public static final int glassesRenderIndex = FMLCommonHandler.instance().getSide() == Side.CLIENT ? RenderingRegistry.addNewArmourRendererPrefix("epGoggles") : 0;
        public static final int portal = FMLCommonHandler.instance().getSide() == Side.CLIENT ? RenderingRegistry.getNextAvailableRenderId() : 0;
        public static final Random random = new Random();
        public static int renderPass = 0;
    }

    public static class EPConfiguration {
        public static int redstoneFluxCost = 10000;
        public static int redstoneFluxTime = 20;
        public static int connectionsPerRow = 2;

        public static boolean disableParticles = false;
        public static boolean disableSounds = false;
        public static boolean forceFrameOverlay = false;
        public static boolean portalDestroysBlocks = false;
        public static boolean fasterPortalCooldown = false;
        public static boolean requirePower = true;
        public static boolean recipeVanilla = true;
        public static boolean recipeTE = true;

        public static double powerUseMultiplier = 1.0;
        public static double powerStorageMultiplier = 1.0;
    }

    public static class EPGuis {
        public static final int PORTAL_CONTROLLER_A = 0;
        public static final int PORTAL_CONTROLLER_B = 1;
        public static final int NETWORK_INTERFACE_A = 2;
        public static final int NETWORK_INTERFACE_B = 3;
        public static final int DIALING_DEVICE_A = 4;
        public static final int DIALING_DEVICE_B = 5;
        public static final int DIALING_DEVICE_C = 6;
        public static final int DIALING_DEVICE_D = 7;
        public static final int DIALING_DEVICE_E = 26;
        public static final int TEXTURE_A = 8;
        public static final int TEXTURE_B = 9;
        public static final int TEXTURE_C = 10;
        public static final int TEXTURE_DIALING_EDIT_A = 11;
        public static final int TEXTURE_DIALING_EDIT_B = 12;
        public static final int TEXTURE_DIALING_EDIT_C = 13;
        public static final int TEXTURE_DIALING_SAVE_A = 14;
        public static final int TEXTURE_DIALING_SAVE_B = 15;
        public static final int TEXTURE_DIALING_SAVE_C = 16;
        public static final int REDSTONE_INTERFACE = 17;
        public static final int MODULE_MANIPULATOR = 20;
        public static final int TRANSFER_FLUID = 21;
        public static final int TRANSFER_ENERGY = 22;
        public static final int TRANSFER_ITEM = 23;
        public static final int MANUAL = 25;
    }

    public static class Locale {
        public static final String BLOCK_PORTAL_FRAME_PART = "block.portalFramePart";
        public static final String BLOCK_MULTIBLOCK_STRUCTURE = "block.multiblockStructure";
        public static final String BLOCK_DBS_SIZE = "block.dbsSize";
        public static final String ITEM_PORTAL_MODULE = "item.portalModule";
        public static final String ITEM_LOCATION_SET = "item.locationSet";
        public static final String GUI_FACING_X = "gui.facing.";
        public static final String GUI_CANCEL = "gui.cancel";
        public static final String GUI_SAVE = "gui.save";
        public static final String GUI_CLEAR = "gui.clear";
        public static final String GUI_RANDOM = "gui.random";
        public static final String GUI_TEXTURES = "gui.textures";
        public static final String GUI_DIAL = "gui.dial";
        public static final String GUI_GLYPHS = "gui.glyphs";
        public static final String GUI_NO_UID_SET = "gui.noUidSet";
        public static final String GUI_NOT_SET = "gui.notSet";
        public static final String GUI_UID_IN_USE = "gui.uidInUse";
        public static final String GUI_PRIVATE = "gui.private";
        public static final String GUI_PUBLIC = "gui.public";
        public static final String GUI_OUTPUT = "gui.output";
        public static final String GUI_INPUT = "gui.input";
        public static final String GUI_FACADE = "gui.facade";
        public static final String GUI_RESET = "gui.reset";
        public static final String GUI_RED = "gui.red";
        public static final String GUI_GREEN = "gui.green";
        public static final String GUI_BLUE = "gui.blue";
        public static final String GUI_SENDING = "gui.sending";
        public static final String GUI_RECEIVING = "gui.receiving";
        public static final String GUI_EDIT = "gui.edit";
        public static final String GUI_DELETE = "gui.delete";
        public static final String GUI_COLOUR = "gui.colour";
        public static final String GUI_UNIQUE_IDENTIFIER = "gui.uniqueIdentifier";
        public static final String GUI_NETWORK_IDENTIFIER = "gui.networkIdentifier";
        public static final String GUI_STORED_IDENTIFIERS = "gui.storedIdentifiers";
        public static final String GUI_WAITING_FOR_DATA_FROM_SERVER = "gui.waitingForDataFromServer";
        public static final String GUI_CLICK_TO_MODIFY = "gui.clickToModify";
        public static final String GUI_TERMINATE = "gui.terminate";
        public static final String GUI_MANUAL_ENTRY = "gui.manualEntry";
        public static final String GUI_INFORMATION = "gui.information";
        public static final String GUI_ACTIVE_PORTALS = "gui.activePortals";
        public static final String GUI_INSTABILITY = "gui.instability";
        public static final String GUI_MODULES = "gui.modules";
        public static final String GUI_NETWORKED_PORTALS = "gui.networkedPortals";
        public static final String GUI_PORTAL_CREATED = "gui.portalCreated";
        public static final String GUI_CREATE_PORTAL_ON_SIGNAL = "gui.createPortalOnSignal";
        public static final String GUI_PORTAL_REMOVED = "gui.portalRemoved";
        public static final String GUI_REMOVE_PORTAL_ON_SIGNAL = "gui.removePortalOnSignal";
        public static final String GUI_PORTAL_ACTIVE = "gui.portalActive";
        public static final String GUI_CREATE_PORTAL_ON_PULSE = "gui.createPortalOnPulse";
        public static final String GUI_PORTAL_INACTIVE = "gui.portalInactive";
        public static final String GUI_REMOVE_PORTAL_ON_PULSE = "gui.removePortalOnPulse";
        public static final String GUI_ENTITY_TELEPORT = "gui.entityTeleport";
        public static final String GUI_DIAL_STORED_IDENTIFIER = "gui.dialStoredIdentifier";
        public static final String GUI_PLAYER_TELEPORT = "gui.playerTeleport";
        public static final String GUI_DIAL_STORED_IDENTIFIER2 = "gui.dialStoredIdentifier2";
        public static final String GUI_ANIMAL_TELEPORT = "gui.animalTeleport";
        public static final String GUI_DIAL_RANDOM_IDENTIFIER = "gui.dialRandomIdentifier";
        public static final String GUI_MONSTER_TELEPORT = "gui.monsterTeleport";
        public static final String GUI_DIAL_RANDOM_IDENTIFIER2 = "gui.dialRandomIdentifier2";
        public static final String GUI_DIALLING_DEVICE = "gui.dialDevice";
        public static final String GUI_DIMENSIONAL_BRIDGE_STABILIZER = "gui.dimensionalBridgeStabilizer";
        public static final String GUI_PORTAL_MANIPULATOR = "gui.moduleManipulator";
        public static final String GUI_NETWORK_INTERFACE = "gui.networkInterface";
        public static final String GUI_PORTAL_CONTROLLER = "gui.portalController";
        public static final String GUI_REDSTONE_INTERFACE = "gui.redstoneInterface";
        public static final String GUI_FRAME = "gui.frame";
        public static final String GUI_PARTICLE = "gui.particle";
        public static final String GUI_PORTAL = "gui.portal";
        public static final String GUI_TRANSFER_ENERGY = "gui.transferEnergy";
        public static final String GUI_TRANSFER_FLUID = "gui.transferFluid";
        public static final String GUI_TRANSFER_ITEM = "gui.transferItem";
        public static final String ITEM_MANUAL_DESC = "item.manual.desc";
    }

    public static enum PortalModules {
        PARTICLES_REMOVE, PARTICLES_RAINBOW, SOUNDS_REMOVE, MOMENTUM, PORTAL_INVISIBLE, PARTICLES_TINTSHADE, FACING, FEATHERFALL;

        public static PortalModules[] MODULES = values();

        public static int count() {
            return MODULES.length;
        }

        public static PortalModules get(int meta) {
            if (meta < count())
                return MODULES[meta];

            return null;
        }
    }

    public static enum PortalFrames {
        BASIC, CONTROLLER, REDSTONE, NETWORK, DIAL, UNUSED, PORTAL_MANIPULATOR, TRANSFER_FLUID, TRANSFER_ITEM, TRANSFER_ENERGY;

        public static PortalFrames[] FRAMES = values();
        static String[] name = new String[] { "frame", "controller", "redstone", "network_interface", "dial_device", "program_interface", "upgrade", "fluid", "item", "energy" };
        static IIcon[] iconOver = new IIcon[FRAMES.length];
        static IIcon[] iconFull = new IIcon[FRAMES.length];
        static IIcon[] itemOver = new IIcon[FRAMES.length];

        public IIcon getOverlay() {
            return iconOver[ordinal()];
        }

        public IIcon getItemOverlay() {
            return itemOver[ordinal()];
        }

        public IIcon getFull() {
            return iconFull[ordinal()];
        }

        public String getName() {
            return name[ordinal()];
        }

        public static int count() {
            return FRAMES.length;
        }

        public static void registerBlockIcons(IIconRegister register) {
            for (int i = 0; i < values().length; i++) {
                iconOver[i] = register.registerIcon(EPMod.ID + ":frame_" + i);
                iconFull[i] = register.registerIcon(EPMod.ID + ":frame_" + i + "b");
            }
        }

        public static void registerItemIcons(IIconRegister register) {
            for (int i = 0; i < values().length - 2; i++)
                itemOver[i] = register.registerIcon(EPMod.ID + ":upgrade_" + i);
        }

        public static PortalFrames get(int metadata) {
            return metadata < count() ? FRAMES[metadata] : null;
        }
    }
}
