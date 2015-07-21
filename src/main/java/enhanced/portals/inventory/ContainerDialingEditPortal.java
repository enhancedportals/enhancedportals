package enhanced.portals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import enhanced.portals.portal.frame.TileDialingDevice;

public class ContainerDialingEditPortal extends ContainerTexturePortal {
    TileDialingDevice dial;

    public ContainerDialingEditPortal(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
