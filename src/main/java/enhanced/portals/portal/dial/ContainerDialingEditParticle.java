package enhanced.portals.portal.dial;

import enhanced.portals.inventory.ContainerTextureParticle;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerDialingEditParticle extends ContainerTextureParticle {
    TileDialingDevice dial;

    public ContainerDialingEditParticle(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
