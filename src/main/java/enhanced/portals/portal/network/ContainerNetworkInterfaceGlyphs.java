package enhanced.portals.portal.network;

import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.controller.TileController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerNetworkInterfaceGlyphs extends BaseContainer {
    TileController controller;

    public ContainerNetworkInterfaceGlyphs(TileController c, InventoryPlayer p) {
        super(null, p, GuiNetworkInterfaceGlyphs.CONTAINER_SIZE + BaseGui.bufferSpace + BaseGui.playerInventorySize);
        controller = c;
        hideInventorySlots();
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("nid")) {
            EnhancedPortals.proxy.networkManager.setPortalNID(controller, new GlyphIdentifier(tag.getString("nid")));
            player.openGui(EnhancedPortals.instance, EPGuis.NETWORK_INTERFACE_A, controller.getWorldObj(), controller.xCoord, controller.yCoord, controller.zCoord);
        }
    }
}
