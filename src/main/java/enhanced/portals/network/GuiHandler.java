package enhanced.portals.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import enhanced.base.tile.TileBase;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.client.gui.GuiDialingAdd;
import enhanced.portals.client.gui.GuiDialingDevice;
import enhanced.portals.client.gui.GuiDialingEdit;
import enhanced.portals.client.gui.GuiDialingEditFrame;
import enhanced.portals.client.gui.GuiDialingEditIdentifier;
import enhanced.portals.client.gui.GuiDialingEditParticle;
import enhanced.portals.client.gui.GuiDialingEditPortal;
import enhanced.portals.client.gui.GuiDialingManual;
import enhanced.portals.client.gui.GuiManual;
import enhanced.portals.client.gui.GuiModuleManipulator;
import enhanced.portals.client.gui.GuiNetworkInterface;
import enhanced.portals.client.gui.GuiNetworkInterfaceGlyphs;
import enhanced.portals.client.gui.GuiPortalController;
import enhanced.portals.client.gui.GuiPortalControllerGlyphs;
import enhanced.portals.client.gui.GuiRedstoneInterface;
import enhanced.portals.client.gui.GuiTextureFrame;
import enhanced.portals.client.gui.GuiTextureParticle;
import enhanced.portals.client.gui.GuiTexturePortal;
import enhanced.portals.client.gui.GuiTransferEnergy;
import enhanced.portals.client.gui.GuiTransferFluid;
import enhanced.portals.client.gui.GuiTransferItem;
import enhanced.portals.inventory.ContainerDialingAdd;
import enhanced.portals.inventory.ContainerDialingDevice;
import enhanced.portals.inventory.ContainerDialingEdit;
import enhanced.portals.inventory.ContainerDialingEditIdentifier;
import enhanced.portals.inventory.ContainerDialingEditParticle;
import enhanced.portals.inventory.ContainerDialingEditPortal;
import enhanced.portals.inventory.ContainerDialingEditTexture;
import enhanced.portals.inventory.ContainerDialingManual;
import enhanced.portals.inventory.ContainerManual;
import enhanced.portals.inventory.ContainerModuleManipulator;
import enhanced.portals.inventory.ContainerNetworkInterface;
import enhanced.portals.inventory.ContainerNetworkInterfaceGlyphs;
import enhanced.portals.inventory.ContainerPortalController;
import enhanced.portals.inventory.ContainerPortalControllerGlyphs;
import enhanced.portals.inventory.ContainerRedstoneInterface;
import enhanced.portals.inventory.ContainerTextureFrame;
import enhanced.portals.inventory.ContainerTextureParticle;
import enhanced.portals.inventory.ContainerTexturePortal;
import enhanced.portals.inventory.ContainerTransferEnergy;
import enhanced.portals.inventory.ContainerTransferFluid;
import enhanced.portals.inventory.ContainerTransferItem;
import enhanced.portals.portal.frame.TileController;
import enhanced.portals.portal.frame.TileDialingDevice;
import enhanced.portals.portal.frame.TilePortalManipulator;
import enhanced.portals.portal.frame.TileRedstoneInterface;
import enhanced.portals.portal.frame.TileTransferEnergy;
import enhanced.portals.portal.frame.TileTransferFluid;
import enhanced.portals.portal.frame.TileTransferItem;

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
            return new GuiModuleManipulator((TilePortalManipulator) tile, player);
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
            return new ContainerModuleManipulator((TilePortalManipulator) tile, player.inventory);
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
