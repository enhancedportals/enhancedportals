package enhanced.portals.network;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import enhanced.base.utilities.BlockPos;
import enhanced.portals.Reference.EPMod;
import enhanced.portals.Reference.EPRenderers;
import enhanced.portals.client.PortalRenderer;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalTextureManager;

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

    public static int editingDialEntry = -1;
    public static PortalTextureManager dialEntryTexture = new PortalTextureManager();

    public static ArrayList<IIcon> customFrameTextures = new ArrayList<IIcon>();
    public static ArrayList<IIcon> customPortalTextures = new ArrayList<IIcon>();
    public static ArrayList<ParticleSet> particleSets = new ArrayList<ParticleSet>();

    public static HashMap<BlockPos, ArrayList<BlockPos>> waitingForController = new HashMap<BlockPos, ArrayList<BlockPos>>();

    @Override
    public void waitForController(BlockPos controller, BlockPos frame) {
        if (waitingForController.containsKey(controller))
            waitingForController.get(controller).add(frame);
        else {
            waitingForController.put(controller, new ArrayList<BlockPos>());
            waitingForController.get(controller).add(frame);
        }
    }

    @Override
    public ArrayList<BlockPos> getControllerList(BlockPos controller) {
        return waitingForController.get(controller);
    }

    @Override
    public void clearControllerList(BlockPos controller) {
        waitingForController.remove(controller);
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

    @Override
    public File getWorldDir() {
        return new File(getBaseDir(), "saves/" + DimensionManager.getWorld(0).getSaveHandler().getWorldDirectoryName());
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
