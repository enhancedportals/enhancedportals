package enhanced.portals.inventory;

import enhanced.portals.portal.dial.TileDialingDevice;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDialingEditTexture extends ContainerTextureFrame {
    TileDialingDevice dial;

    public ContainerDialingEditTexture(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
