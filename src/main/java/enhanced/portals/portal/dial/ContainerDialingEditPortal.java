package enhanced.portals.portal.dial;

import enhanced.portals.inventory.ContainerTexturePortal;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDialingEditPortal extends ContainerTexturePortal {
    TileDialingDevice dial;

    public ContainerDialingEditPortal(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
