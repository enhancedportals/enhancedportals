package enhanced.portals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.client.gui.BaseGui;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.client.gui.GuiNetworkInterfaceGlyphs;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.tile.TileController;
import enhanced.portals.utility.Reference.EPGuis;

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
