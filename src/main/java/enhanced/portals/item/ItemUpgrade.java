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
import enhanced.base.utilities.Localization;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.block.BlockFrame;
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

public class ItemUpgrade extends Item {
    public static ItemUpgrade instance;

    static IIcon baseIcon;
    static IIcon[] overlayIcons = new IIcon[BlockFrame.FRAME_TYPES - 2];

    public ItemUpgrade(String n) {
        super();
        instance = this;
        setCreativeTab(EnhancedPortals.instance.creativeTab);
        setUnlocalizedName(n);
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
        if (pass == 1)
            return overlayIcons[damage];

        return baseIcon;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 0; i < overlayIcons.length; i++)
            par3List.add(new ItemStack(par1, 1, i));
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName() + "." + ItemFrame.unlocalizedName[par1ItemStack.getItemDamage() + 2];
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return false;

        TileEntity tile = world.getTileEntity(x, y, z);
        int blockMeta = stack.getItemDamage() + 2;

        if (tile instanceof TileFrame) {
            TileFrame frame = (TileFrame) tile;
            TileController controller = frame.getPortalController();

            if (controller == null) {
                frame = null;
                world.setBlock(x, y, z, BlockFrame.instance, blockMeta, 2);
                decrementStack(player, stack);
                return true;
            } else {
                if (controller.getDiallingDevices().size() > 0 && blockMeta == BlockFrame.NETWORK_INTERFACE) {
                    player.addChatComponentMessage(new ChatComponentText(Localization.getChatError(EnhancedPortals.MOD_ID, "dialAndNetwork")));
                    return false;
                } else if (controller.getNetworkInterfaces().size() > 0 && blockMeta == BlockFrame.DIALLING_DEVICE) {
                    player.addChatComponentMessage(new ChatComponentText(Localization.getChatError(EnhancedPortals.MOD_ID, "dialAndNetwork")));
                    return false;
                } else if (controller.getModuleManipulator() != null && blockMeta == BlockFrame.MODULE_MANIPULATOR) {
                    player.addChatComponentMessage(new ChatComponentText(Localization.getChatError(EnhancedPortals.MOD_ID, "multipleMod")));
                    return false;
                }

                controller.removeFrame(frame.getChunkCoordinates());
                frame = null;
                world.setBlock(x, y, z, BlockFrame.instance, blockMeta, 2);
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
        }

        return false;
    }

    @Override
    public void registerIcons(IIconRegister register) {
        baseIcon = register.registerIcon("enhancedportals:blank_upgrade");

        for (int i = 0; i < overlayIcons.length; i++)
            overlayIcons[i] = register.registerIcon("enhancedportals:upgrade_" + i);
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
}
