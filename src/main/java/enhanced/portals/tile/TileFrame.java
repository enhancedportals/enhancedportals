package enhanced.portals.tile;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.block.BlockFrame;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.utility.GeneralUtils;
import enhanced.portals.utility.Reference.EPConfiguration;
import enhanced.portals.utility.Reference.PortalFrames;

public abstract class TileFrame extends TilePortalPart {
    protected boolean wearingGoggles = GeneralUtils.isWearingGoggles();

    public void breakBlock(Block b, int oldMetadata) {
        if (b == worldObj.getBlock(xCoord, yCoord, zCoord))
            return;

        TileController controller = getPortalController();

        if (controller != null)
            controller.onPartFrameBroken();
    }

    public IIcon getBlockTexture(int side, int pass) {
        if (pass == 0) {
            TileController controller = getPortalController();

            if (controller != null) {
                if (controller.activeTextureData.hasCustomFrameTexture() && ProxyClient.customFrameTextures.size() > controller.activeTextureData.getCustomFrameTexture() && ProxyClient.customFrameTextures.get(controller.activeTextureData.getCustomFrameTexture()) != null)
                    return ProxyClient.customFrameTextures.get(controller.activeTextureData.getCustomFrameTexture());
                else if (controller.activeTextureData.getFrameItem() != null && controller.activeTextureData.getFrameItem().getItem() instanceof ItemBlock)
                    return Block.getBlockFromItem(controller.activeTextureData.getFrameItem().getItem()).getIcon(side, controller.activeTextureData.getFrameItem().getItemDamage());
            } else if (portalController != null)
                EnhancedPortals.proxy.waitForController(new ChunkCoordinates(portalController.posX, portalController.posY, portalController.posZ), getChunkCoordinates());
            else
                return BlockFrame.connectedTextures.getBaseIcon();

            return BlockFrame.connectedTextures.getIconForSide(worldObj, xCoord, yCoord, zCoord, side);
        }

        PortalFrames frame = PortalFrames.get(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));

        if (frame != null) {
            if (frame == PortalFrames.DIAL)
                return frame.getOverlay();

            return shouldShowOverlay() ? frame.getOverlay() : PortalFrames.BASIC.getOverlay();
        }

        return PortalFrames.BASIC.getOverlay();
    }

    public int getColour() {
        TileController controller = getPortalController();

        if (controller != null)
            return controller.activeTextureData.getFrameColour();
        else if (portalController != null)
            EnhancedPortals.proxy.waitForController(new ChunkCoordinates(portalController.posX, portalController.posY, portalController.posZ), getChunkCoordinates());

        return 0xFFFFFF;
    }

    public void onBlockDismantled() {
        TileController controller = getPortalController();

        if (controller != null)
            controller.deconstruct();
    }

    protected boolean shouldShowOverlay() {
        return wearingGoggles || EPConfiguration.forceFrameOverlay;
    }

    @Override
    public void updateEntity() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && Minecraft.getSystemTime() % 10 == 0) {
            boolean wGoggles = GeneralUtils.isWearingGoggles();

            if (wGoggles != wearingGoggles) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                wearingGoggles = wGoggles;
            }
        }
    }
}
