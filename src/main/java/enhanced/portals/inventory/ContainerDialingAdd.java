package enhanced.portals.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import enhanced.base.inventory.BaseContainer;
import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.portal.GlyphElement;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalTextureManager;
import enhanced.portals.tile.TileDialingDevice;

public class ContainerDialingAdd extends BaseContainer {
    TileDialingDevice dial;

    public ContainerDialingAdd(TileDialingDevice d, InventoryPlayer p) {
        super(null, p);
        dial = d;
        hideInventorySlots();
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("uid") && tag.hasKey("texture") && tag.hasKey("name")) {
            PortalTextureManager ptm = new PortalTextureManager();
            ptm.readFromNBT(tag, "texture");
            dial.glyphList.add(new GlyphElement(tag.getString("name"), new GlyphIdentifier(tag.getString("uid")), ptm));
            player.openGui(EnhancedPortals.instance, EPGuis.DIALING_DEVICE_A, dial.getWorldObj(), dial.xCoord, dial.yCoord, dial.zCoord);
        }
    }
}
