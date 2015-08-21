package enhanced.portals.portal;

import buildcraft.api.tools.IToolWrench;
import enhanced.base.tile.TileBase;
import enhanced.base.utilities.BlockPos;
import enhanced.base.utilities.WorldUtilities;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.network.GuiHandler;
import enhanced.portals.portal.controller.TileController;
import enhanced.portals.portal.frame.TileFrame;
import enhanced.portals.portal.portal.TilePortal;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TilePortalPart extends TileBase {
    public BlockPos portalController;
    TileController cachedController;

    public boolean activate(EntityPlayer player, ItemStack stack) {
        if (player.isSneaking())
            return false;
        
        if (stack != null) {
            if (stack.getItem() instanceof IToolWrench) {
                if (getPortalController() != null && getPortalController().isFinalized && (this instanceof TileFrame || this instanceof TilePortal)) {
                    GuiHandler.openGui(player, getPortalController(), EPGuis.PORTAL_CONTROLLER_A);
                    return true;
                }
            }
        }
        
        return false;
    }

    public abstract void addDataToPacket(NBTTagCompound tag);

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();

        if (portalController != null)
            portalController.writeToNBT(tag, "Controller");

        addDataToPacket(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    public TileController getPortalController() {
        if (cachedController != null)
            return cachedController;

        TileEntity tile = portalController == null ? null : WorldUtilities.getTileEntity(getWorldObj(), portalController);

        if (tile != null && tile instanceof TileController) {
            cachedController = (TileController) tile;
            return cachedController;
        }

        return null;
    }

    public void onBlockPlaced(EntityLivingBase entity, ItemStack stack) {
        for (int i = 0; i < 6; i++) {
            BlockPos c = BlockPos.offset(getBlockPos(), ForgeDirection.getOrientation(i));
            TileEntity tile = WorldUtilities.getTileEntity(getWorldObj(), c);

            if (tile != null && tile instanceof TilePortalPart)
                ((TilePortalPart) tile).onNeighborPlaced(entity, xCoord, yCoord, zCoord);
        }
    }

    public abstract void onDataPacket(NBTTagCompound tag);

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.func_148857_g();

        portalController = BlockPos.readFromNBT(tag, "Controller");
        cachedController = null;

        onDataPacket(tag);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void onNeighborPlaced(EntityLivingBase entity, int x, int y, int z) {
        TileController controller = getPortalController();

        if (controller != null)
            controller.deconstruct();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        portalController = BlockPos.readFromNBT(compound, "Controller");
    }

    public void setPortalController(BlockPos c) {
        portalController = c;
        cachedController = null;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        if (portalController != null)
            portalController.writeToNBT(compound, "Controller");
    }
}
