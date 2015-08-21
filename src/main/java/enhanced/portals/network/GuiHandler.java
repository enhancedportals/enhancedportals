package enhanced.portals.network;

import cpw.mods.fml.common.network.IGuiHandler;
import enhanced.base.tile.TileBase;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.client.gui.GuiTextureFrame;
import enhanced.portals.client.gui.GuiTextureParticle;
import enhanced.portals.client.gui.GuiTexturePortal;
import enhanced.portals.inventory.ContainerTextureFrame;
import enhanced.portals.inventory.ContainerTextureParticle;
import enhanced.portals.inventory.ContainerTexturePortal;
import enhanced.portals.item.manual.ContainerManual;
import enhanced.portals.item.manual.GuiManual;
import enhanced.portals.portal.controller.ContainerPortalController;
import enhanced.portals.portal.controller.ContainerPortalControllerGlyphs;
import enhanced.portals.portal.controller.GuiPortalController;
import enhanced.portals.portal.controller.GuiPortalControllerGlyphs;
import enhanced.portals.portal.controller.TileController;
import enhanced.portals.portal.dial.ContainerDialingAdd;
import enhanced.portals.portal.dial.ContainerDialingDevice;
import enhanced.portals.portal.dial.ContainerDialingEdit;
import enhanced.portals.portal.dial.ContainerDialingEditIdentifier;
import enhanced.portals.portal.dial.ContainerDialingEditParticle;
import enhanced.portals.portal.dial.ContainerDialingEditPortal;
import enhanced.portals.portal.dial.ContainerDialingEditTexture;
import enhanced.portals.portal.dial.ContainerDialingManual;
import enhanced.portals.portal.dial.GuiDialingAdd;
import enhanced.portals.portal.dial.GuiDialingDevice;
import enhanced.portals.portal.dial.GuiDialingEdit;
import enhanced.portals.portal.dial.GuiDialingEditFrame;
import enhanced.portals.portal.dial.GuiDialingEditIdentifier;
import enhanced.portals.portal.dial.GuiDialingEditParticle;
import enhanced.portals.portal.dial.GuiDialingEditPortal;
import enhanced.portals.portal.dial.GuiDialingManual;
import enhanced.portals.portal.dial.TileDialingDevice;
import enhanced.portals.portal.manipulator.ContainerPortalManipulator;
import enhanced.portals.portal.manipulator.GuiPortalManipulator;
import enhanced.portals.portal.manipulator.TilePortalManipulator;
import enhanced.portals.portal.network.ContainerNetworkInterface;
import enhanced.portals.portal.network.ContainerNetworkInterfaceGlyphs;
import enhanced.portals.portal.network.GuiNetworkInterface;
import enhanced.portals.portal.network.GuiNetworkInterfaceGlyphs;
import enhanced.portals.portal.redstone.ContainerRedstoneInterface;
import enhanced.portals.portal.redstone.GuiRedstoneInterface;
import enhanced.portals.portal.redstone.TileRedstoneInterface;
import enhanced.portals.portal.transfer.ContainerTransferEnergy;
import enhanced.portals.portal.transfer.ContainerTransferFluid;
import enhanced.portals.portal.transfer.ContainerTransferItem;
import enhanced.portals.portal.transfer.GuiTransferEnergy;
import enhanced.portals.portal.transfer.GuiTransferFluid;
import enhanced.portals.portal.transfer.GuiTransferItem;
import enhanced.portals.portal.transfer.TileTransferEnergy;
import enhanced.portals.portal.transfer.TileTransferFluid;
import enhanced.portals.portal.transfer.TileTransferItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
    public static void openGui(EntityPlayer player, TileEntity tile, int gui) {
        player.openGui(EnhancedPortals.instance, gui, tile.getWorldObj(), tile.xCoord, tile.yCoord, tile.zCoord);
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == EPGuis.MANUAL)
            return new GuiManual(player);

        TileEntity t = world.getTileEntity(x, y, z);

        if (!(t instanceof TileBase))
            return null;

        TileBase tile = (TileBase) t;

        if (ID == EPGuis.PORTAL_CONTROLLER_A)
            return new GuiPortalController((TileController) tile, player);
        else if (ID == EPGuis.PORTAL_CONTROLLER_B)
            return new GuiPortalControllerGlyphs((TileController) tile, player);
        else if (ID == EPGuis.REDSTONE_INTERFACE)
            return new GuiRedstoneInterface((TileRedstoneInterface) tile, player);
        else if (ID == EPGuis.NETWORK_INTERFACE_A)
            return new GuiNetworkInterface((TileController) tile, player);
        else if (ID == EPGuis.NETWORK_INTERFACE_B)
            return new GuiNetworkInterfaceGlyphs((TileController) tile, player);
        else if (ID == EPGuis.MODULE_MANIPULATOR)
            return new GuiPortalManipulator((TilePortalManipulator) tile, player);
        else if (ID == EPGuis.DIALING_DEVICE_A)
            return new GuiDialingDevice((TileDialingDevice) tile, player);
        else if (ID == EPGuis.DIALING_DEVICE_B)
            return new GuiDialingManual((TileDialingDevice) tile, player);
        else if (ID == EPGuis.DIALING_DEVICE_C)
            return new GuiDialingAdd((TileDialingDevice) tile, player);
        else if (ID == EPGuis.DIALING_DEVICE_D)
            return new GuiDialingEdit((TileDialingDevice) tile, player);
        else if (ID == EPGuis.DIALING_DEVICE_E)
            return new GuiDialingEditIdentifier((TileDialingDevice) tile, player);
        else if (ID == EPGuis.TEXTURE_A)
            return new GuiTextureFrame((TileController) tile, player);
        else if (ID == EPGuis.TEXTURE_B)
            return new GuiTexturePortal((TileController) tile, player);
        else if (ID == EPGuis.TEXTURE_C)
            return new GuiTextureParticle((TileController) tile, player);
        else if (ID == EPGuis.TEXTURE_DIALING_SAVE_A)
            return new GuiDialingEditFrame((TileDialingDevice) tile, player, false);
        else if (ID == EPGuis.TEXTURE_DIALING_SAVE_B)
            return new GuiDialingEditPortal((TileDialingDevice) tile, player, false);
        else if (ID == EPGuis.TEXTURE_DIALING_SAVE_C)
            return new GuiDialingEditParticle((TileDialingDevice) tile, player, false);
        else if (ID == EPGuis.TEXTURE_DIALING_EDIT_A)
            return new GuiDialingEditFrame((TileDialingDevice) tile, player, true);
        else if (ID == EPGuis.TEXTURE_DIALING_EDIT_B)
            return new GuiDialingEditPortal((TileDialingDevice) tile, player, true);
        else if (ID == EPGuis.TEXTURE_DIALING_EDIT_C)
            return new GuiDialingEditParticle((TileDialingDevice) tile, player, true);
        else if (ID == EPGuis.TRANSFER_FLUID)
            return new GuiTransferFluid((TileTransferFluid) tile, player);
        else if (ID == EPGuis.TRANSFER_ENERGY)
            return new GuiTransferEnergy((TileTransferEnergy) tile, player);
        else if (ID == EPGuis.TRANSFER_ITEM)
            return new GuiTransferItem((TileTransferItem) tile, player);

        return null;
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == EPGuis.MANUAL)
            return new ContainerManual(player.inventory);

        TileEntity t = world.getTileEntity(x, y, z);

        if (!(t instanceof TileBase))
            return null;

        TileBase tile = (TileBase) t;

        if (ID == EPGuis.PORTAL_CONTROLLER_A)
            return new ContainerPortalController((TileController) tile, player.inventory);
        else if (ID == EPGuis.PORTAL_CONTROLLER_B)
            return new ContainerPortalControllerGlyphs((TileController) tile, player.inventory);
        else if (ID == EPGuis.REDSTONE_INTERFACE)
            return new ContainerRedstoneInterface((TileRedstoneInterface) tile, player.inventory);
        else if (ID == EPGuis.NETWORK_INTERFACE_A)
            return new ContainerNetworkInterface((TileController) tile, player.inventory);
        else if (ID == EPGuis.NETWORK_INTERFACE_B)
            return new ContainerNetworkInterfaceGlyphs((TileController) tile, player.inventory);
        else if (ID == EPGuis.MODULE_MANIPULATOR)
            return new ContainerPortalManipulator((TilePortalManipulator) tile, player.inventory);
        //else if (ID == EPGuis.DIMENSIONAL_BRIDGE_STABILIZER)
        //    return new ContainerDimensionalBridgeStabilizer((TileStabilizerMain) tile, player.inventory);
        else if (ID == EPGuis.DIALING_DEVICE_A)
            return new ContainerDialingDevice((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.DIALING_DEVICE_B)
            return new ContainerDialingManual((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.DIALING_DEVICE_C)
            return new ContainerDialingAdd((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.DIALING_DEVICE_D)
            return new ContainerDialingEdit((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.DIALING_DEVICE_E)
            return new ContainerDialingEditIdentifier((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.TEXTURE_A)
            return new ContainerTextureFrame((TileController) tile, player.inventory);
        else if (ID == EPGuis.TEXTURE_B)
            return new ContainerTexturePortal((TileController) tile, player.inventory);
        else if (ID == EPGuis.TEXTURE_C)
            return new ContainerTextureParticle((TileController) tile, player.inventory);
        else if (ID == EPGuis.TEXTURE_DIALING_EDIT_A || ID == EPGuis.TEXTURE_DIALING_SAVE_A)
            return new ContainerDialingEditTexture((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.TEXTURE_DIALING_EDIT_B || ID == EPGuis.TEXTURE_DIALING_SAVE_B)
            return new ContainerDialingEditPortal((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.TEXTURE_DIALING_EDIT_C || ID == EPGuis.TEXTURE_DIALING_SAVE_C)
            return new ContainerDialingEditParticle((TileDialingDevice) tile, player.inventory);
        else if (ID == EPGuis.TRANSFER_FLUID)
            return new ContainerTransferFluid((TileTransferFluid) tile, player.inventory);
        else if (ID == EPGuis.TRANSFER_ENERGY)
            return new ContainerTransferEnergy((TileTransferEnergy) tile, player.inventory);
        else if (ID == EPGuis.TRANSFER_ITEM)
            return new ContainerTransferItem((TileTransferItem) tile, player.inventory);

        return null;
    }
}
