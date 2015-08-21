package enhanced.portals.inventory;

import enhanced.portals.portal.dial.TileDialingDevice;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDialingEditPortal extends ContainerTexturePortal {
    TileDialingDevice dial;

    public ContainerDialingEditPortal(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
