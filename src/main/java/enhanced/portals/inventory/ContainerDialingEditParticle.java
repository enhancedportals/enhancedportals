package enhanced.portals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import enhanced.portals.portal.frame.TileDialingDevice;

public class ContainerDialingEditParticle extends ContainerTextureParticle {
    TileDialingDevice dial;

    public ContainerDialingEditParticle(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
