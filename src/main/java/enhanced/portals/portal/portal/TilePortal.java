package enhanced.portals.portal.portal;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.portal.TilePortalPart;
import enhanced.portals.portal.frame.TileController;

public class TilePortal extends TilePortalPart {
    @Override
    public void addDataToPacket(NBTTagCompound tag) {

    }

    public IIcon getBlockTexture(int side) {
        TileController controller = getPortalController();

        if (controller != null) {
            if (controller.activeTextureData.hasCustomPortalTexture() && ProxyClient.customPortalTextures.size() > controller.activeTextureData.getCustomPortalTexture() && ProxyClient.customPortalTextures.get(controller.activeTextureData.getCustomPortalTexture()) != null)
                return ProxyClient.customPortalTextures.get(controller.activeTextureData.getCustomPortalTexture());
            else if (controller.activeTextureData.getPortalItem() != null && controller.activeTextureData.getPortalItem().getItem() instanceof ItemBlock)
                return Block.getBlockFromItem(controller.activeTextureData.getPortalItem().getItem()).getIcon(side, controller.activeTextureData.getPortalItem().getItemDamage());
        } else if (portalController != null)
            EnhancedPortals.proxy.waitForController(portalController, getBlockPos());

        return EPBlocks.portal.getIcon(side, 0);
    }

    public int getColour() {
        TileController controller = getPortalController();

        if (controller != null)
            return controller.activeTextureData.getPortalColour();
        else if (portalController != null)
            EnhancedPortals.proxy.waitForController(portalController, getBlockPos());

        return 0xFFFFFF;
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {

    }

    public void onEntityCollidedWithBlock(Entity entity) {
        TileController controller = getPortalController();

        if (controller != null)
            controller.onEntityEnterPortal(entity, this);
    }

    @Override
    public void writeToGui(ByteBuf buffer) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void readFromGui(ByteBuf buffer) {
        // TODO Auto-generated method stub
        
    }
}
