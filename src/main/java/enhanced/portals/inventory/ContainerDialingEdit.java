package enhanced.portals.inventory;

import enhanced.portals.EnhancedPortals;
import enhanced.portals.Reference.EPGuis;
import enhanced.portals.portal.GlyphElement;
import enhanced.portals.portal.GlyphIdentifier;
import enhanced.portals.portal.PortalTextureManager;
import enhanced.portals.portal.dial.TileDialingDevice;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class ContainerDialingEdit extends ContainerDialingAdd {
    public ContainerDialingEdit(TileDialingDevice d, InventoryPlayer p) {
        super(d, p);
    }

    @Override
    public void handleGuiPacket(NBTTagCompound tag, EntityPlayer player) {
        if (tag.hasKey("id") && tag.hasKey("uid") && tag.hasKey("texture") && tag.hasKey("name")) {
            PortalTextureManager ptm = new PortalTextureManager();
            ptm.readFromNBT(tag, "texture");
            dial.glyphList.set(tag.getInteger("id"), new GlyphElement(tag.getString("name"), new GlyphIdentifier(tag.getString("uid")), ptm));
            player.openGui(EnhancedPortals.instance, EPGuis.DIALING_DEVICE_A, dial.getWorldObj(), dial.xCoord, dial.yCoord, dial.zCoord);
        }
    }
}
