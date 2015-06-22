package enhanced.portals.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import enhanced.portals.tile.TileDialingDevice;

public class ContainerDialingEditTexture extends ContainerTextureFrame {
    TileDialingDevice dial;

    public ContainerDialingEditTexture(TileDialingDevice d, InventoryPlayer p) {
        super(d.getPortalController(), p);
        dial = d;
    }
}
