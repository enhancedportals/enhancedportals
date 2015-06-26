package enhanced.portals.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import enhanced.base.item.ItemBase;
import enhanced.base.utilities.Localisation;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.tile.TileController;
import enhanced.portals.tile.TileDialingDevice;
import enhanced.portals.tile.TileFrame;
import enhanced.portals.tile.TileNetworkInterface;
import enhanced.portals.tile.TilePortalManipulator;
import enhanced.portals.tile.TilePortalPart;
import enhanced.portals.tile.TileRedstoneInterface;
import enhanced.portals.tile.TileTransferEnergy;
import enhanced.portals.tile.TileTransferFluid;
import enhanced.portals.tile.TileTransferItem;
import enhanced.portals.utility.Reference.EPBlocks;
import enhanced.portals.utility.Reference.EPMod;
import enhanced.portals.utility.Reference.PortalFrames;

public class ItemUpgrade extends ItemBase {
    public ItemUpgrade(String n) {
        super(EPMod.ID, n, EnhancedPortals.instance.creativeTab);
        setHasSubtypes(true);
        setMaxDamage(0);
    }

    private void decrementStack(EntityPlayer player, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;

            if (stack.stackSize <= 0)
                stack = null;
        }
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        if (pass == 1) {
            PortalFrames frame = PortalFrames.get(damage);

            if (frame != null)
                return frame.getItemOverlay();
        }

        return super.getIconFromDamageForRenderPass(damage, pass);
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < PortalFrames.count() - 2; i++)
            par3List.add(new ItemStack(par1, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack) {
        PortalFrames frame = PortalFrames.get(itemStack.getItemDamage() + 2);
        String name = "unknown";

        if (frame != null)
            name = frame.getName();

        return super.getUnlocalizedName() + "." + name;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return false;

        TileEntity tile = world.getTileEntity(x, y, z);
        PortalFrames type = PortalFrames.get(stack.getItemDamage() + 2);

        if (tile instanceof TileFrame) {
            TileFrame frame = (TileFrame) tile;
            TileController controller = frame.getPortalController();

            if (controller == null) {
                frame = null;
                world.setBlock(x, y, z, EPBlocks.frame, type.ordinal(), 2);
                decrementStack(player, stack);
                return true;
            }

            if (controller.getDiallingDevices().size() > 0 && type == PortalFrames.NETWORK) {
                player.addChatComponentMessage(new ChatComponentText(Localisation.getChatError(EPMod.ID, "dialAndNetwork")));
                return false;
            } else if (controller.getNetworkInterfaces().size() > 0 && type == PortalFrames.DIAL) {
                player.addChatComponentMessage(new ChatComponentText(Localisation.getChatError(EPMod.ID, "dialAndNetwork")));
                return false;
            } else if (controller.getModuleManipulator() != null && type == PortalFrames.PORTAL_MANIPULATOR) {
                player.addChatComponentMessage(new ChatComponentText(Localisation.getChatError(EPMod.ID, "multipleMod")));
                return false;
            }

            controller.removeFrame(frame.getChunkCoordinates());
            frame = null;
            world.setBlock(x, y, z, EPBlocks.frame, type.ordinal(), 2);
            decrementStack(player, stack);
            TilePortalPart t = (TilePortalPart) world.getTileEntity(x, y, z);

            if (t instanceof TileRedstoneInterface)
                controller.addRedstoneInterface(t.getChunkCoordinates());
            else if (t instanceof TileDialingDevice)
                controller.addDialDevice(t.getChunkCoordinates());
            else if (t instanceof TileNetworkInterface)
                controller.addNetworkInterface(t.getChunkCoordinates());
            else if (t instanceof TilePortalManipulator)
                controller.setModuleManipulator(t.getChunkCoordinates());
            else if (t instanceof TileTransferEnergy)
                controller.addTransferEnergy(t.getChunkCoordinates());
            else if (t instanceof TileTransferFluid)
                controller.addTransferFluid(t.getChunkCoordinates());
            else if (t instanceof TileTransferItem)
                controller.addTransferItem(t.getChunkCoordinates());

            t.setPortalController(controller.getChunkCoordinates());
            world.markBlockForUpdate(controller.xCoord, controller.yCoord, controller.zCoord);
            return true;
        }

        return false;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        PortalFrames.registerItemIcons(register);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
}
