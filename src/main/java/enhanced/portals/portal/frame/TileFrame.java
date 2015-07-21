package enhanced.portals.portal.frame;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPBlocks;
import enhanced.portals.Reference.EPConfiguration;
import enhanced.portals.Reference.EPItems;
import enhanced.portals.Reference.PortalFrames;
import enhanced.portals.network.ProxyClient;
import enhanced.portals.portal.TilePortalPart;

public class TileFrame extends TilePortalPart implements IDismantleable {
    protected boolean wearingGoggles = TileFrame.isWearingGoggles();

    public void breakBlock(Block b, int oldMetadata) {
        if (b == worldObj.getBlock(xCoord, yCoord, zCoord))
            return;

        TileController controller = getPortalController();

        if (controller != null)
            controller.breakBlock(null, 0);
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
                EnhancedPortals.proxy.waitForController(portalController, getBlockPos());
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
            EnhancedPortals.proxy.waitForController(portalController, getBlockPos());

        return 0xFFFFFF;
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, World world, int x, int y, int z, boolean returnDrops) {
        TileController controller = getPortalController();

        if (controller != null)
            controller.deconstruct();

        ItemStack stack = new ItemStack(EPBlocks.frame, 1, getBlockMetadata());
        world.setBlockToAir(xCoord, yCoord, zCoord);

        if (!returnDrops) {
            float f = 0.3F;
            double d1 = world.rand.nextFloat() * f + (1f - f) * 0.5d;
            double d2 = world.rand.nextFloat() * f + (1f - f) * 0.5d;
            double d3 = world.rand.nextFloat() * f + (1f - f) * 0.5d;
            EntityItem item = new EntityItem(getWorldObj(), x + d1, y + d2, z + d3);
            item.delayBeforeCanPickup = 10;
            world.spawnEntityInWorld(item);
        }
        
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(stack);
        return list;
    }

    @Override
    public boolean canDismantle(EntityPlayer player, World world, int x, int y, int z) {
        return true;
    }

    protected boolean shouldShowOverlay() {
        return wearingGoggles || EPConfiguration.forceFrameOverlay;
    }

    public static boolean isWearingGoggles() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (Minecraft.getMinecraft().thePlayer == null)
                return false;

            ItemStack stack = Minecraft.getMinecraft().thePlayer.inventory.armorItemInSlot(3);
            return stack != null && stack.getItem() == EPItems.glasses;
        }

        return false;
    }
    
    @Override
    public void updateEntity() {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT && Minecraft.getSystemTime() % 10 == 0) {
            boolean wGoggles = isWearingGoggles();

            if (wGoggles != wearingGoggles) {
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                wearingGoggles = wGoggles;
            }
        }
    }

    @Override
    public void addDataToPacket(NBTTagCompound tag) {
        
    }

    @Override
    public void onDataPacket(NBTTagCompound tag) {
        
    }

    @Override
    public void writeToGui(ByteBuf buffer) {
        
    }

    @Override
    public void readFromGui(ByteBuf buffer) {
        
    }
}
